// Dependencies shared by all modules. Tests build them with fakes where needed.
import { MailOutbox } from '../modules/mail/mail-outbox.js';
import type { MailSender } from '../modules/mail/mail-sender.js';
import type { Db } from '../shared/database.js';
import { RateLimiter } from '../shared/rate-limiter.js';
import type { AppConfig } from './config.js';

export interface AppDeps {
  readonly config: AppConfig;
  readonly db: Db;
  readonly limiter: RateLimiter;
  readonly outbox: MailOutbox;
}

export function createDeps(config: AppConfig, db: Db, mailer: MailSender): AppDeps {
  return {
    config,
    db,
    limiter: new RateLimiter(db, config.secrets.ipHashPepper),
    outbox: new MailOutbox(db, mailer, config.smtp.dailyCap),
  };
}
