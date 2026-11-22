// Sliding-window rate limits stored in PostgreSQL, shared by all serverless instances.
import { sql } from 'kysely';
import { keyedHash } from './crypto/secure-hash.js';
import type { Db } from './database.js';
import { AppError } from './errors.js';

export interface LimitRule {
  readonly action: string;
  readonly max: number;
  readonly windowSeconds: number;
}

export class RateLimiter {
  constructor(
    private readonly db: Db,
    private readonly pepper: string,
  ) {}

  /** Throws RATE_LIMITED when the key already used up the window. */
  async assertAllowed(rule: LimitRule, key: string): Promise<void> {
    const count = await this.count(rule, key);
    if (count >= rule.max) throw new AppError('RATE_LIMITED');
  }

  async record(rule: LimitRule, key: string): Promise<void> {
    await this.db
      .insertInto('rate_limit_events')
      .values({ action: rule.action, key_hash: keyedHash(this.pepper, `${rule.action}:${key}`) })
      .execute();
  }

  /** Records the attempt and then checks it, so concurrent bursts are also counted. */
  async consume(rule: LimitRule, key: string): Promise<void> {
    await this.record(rule, key);
    const count = await this.count(rule, key);
    if (count > rule.max) throw new AppError('RATE_LIMITED');
  }

  private async count(rule: LimitRule, key: string): Promise<number> {
    const row = await this.db
      .selectFrom('rate_limit_events')
      .select((eb) => eb.fn.countAll<string>().as('total'))
      .where('action', '=', rule.action)
      .where('key_hash', '=', keyedHash(this.pepper, `${rule.action}:${key}`))
      .where('created_at', '>', sql<Date>`now() - make_interval(secs => ${rule.windowSeconds})`)
      .executeTakeFirstOrThrow();
    return Number(row.total);
  }
}
