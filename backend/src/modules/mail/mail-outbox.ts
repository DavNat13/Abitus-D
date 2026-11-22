// Mail queue in PostgreSQL. Mails are queued first and delivered right away when possible;
// failures stay pending for the scheduled job. One-time codes are wiped once delivered.
import { sql } from 'kysely';
import type { Db } from '../../shared/database.js';
import { newId } from '../../shared/ids.js';
import { renderMail } from '../../mail-templates/render-mail.js';
import {
  MAIL_PRIORITY,
  TEMPLATE_PRIORITY,
  TEMPLATES_WITH_SECRETS,
  type MailLocale,
  type MailPayloads,
  type MailTemplate,
} from './mail-catalog.js';
import type { MailSender } from './mail-sender.js';

const MAX_ATTEMPTS = 5;
const LOW_PRIORITY_QUOTA_SHARE = 0.7;

export interface QueuedMail<T extends MailTemplate> {
  readonly template: T;
  readonly to: string;
  readonly locale: MailLocale;
  readonly payload: MailPayloads[T];
  readonly userId?: string | undefined;
}

export class MailOutbox {
  constructor(
    private readonly db: Db,
    private readonly sender: MailSender,
    private readonly dailyCap: number,
  ) {}

  async enqueue<T extends MailTemplate>(mail: QueuedMail<T>): Promise<string> {
    const id = newId();
    await this.db
      .insertInto('email_outbox')
      .values({
        id,
        user_id: mail.userId ?? null,
        to_email: mail.to,
        template: mail.template,
        locale: mail.locale,
        payload: JSON.stringify(mail.payload),
        priority: TEMPLATE_PRIORITY[mail.template],
      })
      .execute();
    return id;
  }

  /** Queues and tries to deliver at once. Never throws: a failure is retried later. */
  async send<T extends MailTemplate>(mail: QueuedMail<T>): Promise<void> {
    const id = await this.enqueue(mail);
    await this.deliver(id);
  }

  /** Delivers pending mails in priority order, respecting the daily quota. */
  async processPending(limit: number): Promise<number> {
    const rows = await this.db
      .selectFrom('email_outbox')
      .select('id')
      .where('status', '=', 'pending')
      .orderBy('priority')
      .orderBy('created_at')
      .limit(limit)
      .execute();
    let sent = 0;
    for (const row of rows) if (await this.deliver(row.id)) sent += 1;
    return sent;
  }

  private async deliver(id: string): Promise<boolean> {
    const row = await this.db
      .selectFrom('email_outbox')
      .selectAll()
      .where('id', '=', id)
      .where('status', '=', 'pending')
      .executeTakeFirst();
    if (row === undefined || !(await this.withinQuota(row.priority))) return false;
    const template = row.template as MailTemplate;
    try {
      const rendered = await renderMail(template, row.locale as MailLocale, row.payload as never);
      await this.sender.send({ to: row.to_email, ...rendered });
      const wipe = TEMPLATES_WITH_SECRETS.has(template);
      await this.db
        .updateTable('email_outbox')
        .set({ status: 'sent', sent_at: new Date(), ...(wipe ? { payload: '{}' } : {}) })
        .where('id', '=', id)
        .execute();
      return true;
    } catch (error) {
      const attempts = row.attempts + 1;
      await this.db
        .updateTable('email_outbox')
        .set({
          attempts,
          status: attempts >= MAX_ATTEMPTS ? 'failed' : 'pending',
          last_error: (error instanceof Error ? error.name : 'Error').slice(0, 100),
        })
        .where('id', '=', id)
        .execute();
      return false;
    }
  }

  private async withinQuota(priority: number): Promise<boolean> {
    const row = await this.db
      .selectFrom('email_outbox')
      .select((eb) => eb.fn.countAll<string>().as('total'))
      .where('status', '=', 'sent')
      .where('sent_at', '>', sql<Date>`now() - interval '24 hours'`)
      .executeTakeFirstOrThrow();
    const used = Number(row.total);
    if (used >= this.dailyCap) return false;
    const reservedForSecurity = priority > MAIL_PRIORITY.high;
    return !reservedForSecurity || used < this.dailyCap * LOW_PRIORITY_QUOTA_SHARE;
  }
}
