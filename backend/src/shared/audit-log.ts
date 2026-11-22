// Security audit events without sensitive content (no emails, tokens or codes).
import type { Db } from './database.js';

export type AuditEvent =
  | 'register'
  | 'email_verified'
  | 'login'
  | 'login_failed'
  | 'new_device'
  | 'token_reuse'
  | 'logout'
  | 'logout_all'
  | 'device_revoked'
  | 'password_reset'
  | 'password_change';

export interface AuditEntry {
  readonly userId: string | null;
  readonly event: AuditEvent;
  readonly deviceId?: string | undefined;
  readonly ipHash?: string | undefined;
}

export async function writeAudit(db: Db, entry: AuditEntry): Promise<void> {
  await db
    .insertInto('audit_log')
    .values({
      user_id: entry.userId,
      event: entry.event,
      device_id: entry.deviceId ?? null,
      ip_hash: entry.ipHash ?? null,
    })
    .execute();
}
