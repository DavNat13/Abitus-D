// Builds the real app against the test database with a mail sender that records mails.
import { randomBytes, randomInt } from 'node:crypto';
import type { FastifyInstance } from 'fastify';
import { createDeps } from '../../src/app/app-deps.js';
import { buildApp } from '../../src/app/build-app.js';
import { type AppConfig, loadConfig } from '../../src/app/config.js';
import type { MailSender, OutgoingMail } from '../../src/modules/mail/mail-sender.js';
import { createDatabase, type Db } from '../../src/shared/database.js';
import { testDatabaseUrl } from './global-setup.js';

const secret = (): string => randomBytes(32).toString('base64url');

export class RecordingMailer implements MailSender {
  readonly sent: OutgoingMail[] = [];
  failNext = false;

  send(mail: OutgoingMail): Promise<void> {
    if (this.failNext) {
      this.failNext = false;
      return Promise.reject(new Error('smtp down'));
    }
    this.sent.push(mail);
    return Promise.resolve();
  }

  to(email: string): OutgoingMail[] {
    return this.sent.filter((mail) => mail.to === email);
  }

  lastCode(email: string): string {
    const codes = this.to(email)
      .map((mail) => /\b(\d{6})\b/.exec(mail.text)?.[1])
      .filter((code): code is string => code !== undefined);
    const code = codes.at(-1);
    if (code === undefined) throw new Error(`no code mailed to ${email}`);
    return code;
  }
}

export interface TestContext {
  readonly app: FastifyInstance;
  readonly db: Db;
  readonly mailer: RecordingMailer;
  readonly config: AppConfig;
  close(): Promise<void>;
}

export async function createTestApp(): Promise<TestContext> {
  const config = loadConfig({
    NODE_ENV: 'test',
    LOG_LEVEL: 'silent',
    DATABASE_URL: testDatabaseUrl(),
    JWT_ACCESS_SECRET: secret(),
    REFRESH_TOKEN_PEPPER: secret(),
    CODE_PEPPER: secret(),
    IP_HASH_PEPPER: secret(),
    JOBS_SECRET: secret(),
    SMTP_HOST: 'localhost',
    SMTP_PORT: '1025',
    MAIL_FROM: 'test@example.test',
  });
  const db = createDatabase(config.databaseUrl);
  const mailer = new RecordingMailer();
  const app = await buildApp(createDeps(config, db, mailer));
  return {
    app,
    db,
    mailer,
    config,
    async close() {
      await app.close();
      await db.destroy();
    },
  };
}

/** A distinct client IP per test so IP limits of one test do not affect another. */
export function randomIp(): string {
  return `10.${String(randomInt(256))}.${String(randomInt(256))}.${String(randomInt(1, 255))}`;
}

export function uniqueEmail(prefix = 'user'): string {
  return `${prefix}.${randomBytes(6).toString('hex')}@example.test`;
}
