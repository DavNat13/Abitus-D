// Kysely table types. Mirror of database/migrations; keep in sync by hand.
import type { ColumnType, Generated } from 'kysely';
import type { CatalogDatabase } from './database-catalog-types.js';

type Timestamp = ColumnType<Date, Date | string | undefined, Date | string>;
type NullableTimestamp = ColumnType<
  Date | null,
  Date | string | null | undefined,
  Date | string | null
>;

export interface UsersTable {
  id: string;
  email: string;
  email_verified_at: NullableTimestamp;
  password_hash: string;
  display_name: string | null;
  locale: ColumnType<string, string | undefined, string>;
  primary_goal: string | null;
  height_cm: string | null;
  birth_year: number | null;
  created_at: Timestamp;
  updated_at: Timestamp;
  deleted_at: NullableTimestamp;
}

export interface UserPreferencesTable {
  user_id: string;
  locale: ColumnType<string, string | undefined, string>;
  updated_at: Timestamp;
}

export interface DevicesTable {
  id: string;
  user_id: string;
  installation_id: string;
  name: string;
  platform: ColumnType<string, string | undefined, string>;
  app_version: string;
  first_seen_at: Timestamp;
  last_seen_at: Timestamp;
  revoked_at: NullableTimestamp;
}

export interface RefreshTokensTable {
  id: string;
  user_id: string;
  device_id: string;
  family_id: string;
  token_hash: string;
  expires_at: Timestamp;
  rotated_at: NullableTimestamp;
  revoked_at: NullableTimestamp;
  created_at: Timestamp;
}

export type CodePurpose = 'verify_email' | 'reset_password';

export interface VerificationCodesTable {
  id: string;
  email: string;
  purpose: CodePurpose;
  code_hash: string;
  attempts: ColumnType<number, number | undefined, number>;
  expires_at: Timestamp;
  consumed_at: NullableTimestamp;
  created_at: Timestamp;
  ip_hash: string | null;
}

export interface ConsentsTable {
  id: string;
  user_id: string;
  document: 'privacy' | 'terms' | 'age';
  version: string;
  accepted_at: Timestamp;
}

export interface EmailOutboxTable {
  id: string;
  user_id: string | null;
  to_email: string;
  template: string;
  locale: string;
  payload: ColumnType<Record<string, string>, string, string>;
  priority: number;
  status: ColumnType<
    'pending' | 'sent' | 'failed',
    'pending' | undefined,
    'pending' | 'sent' | 'failed'
  >;
  attempts: ColumnType<number, number | undefined, number>;
  last_error: string | null;
  created_at: Timestamp;
  sent_at: NullableTimestamp;
}

export interface AuditLogTable {
  id: Generated<string>;
  user_id: string | null;
  event: string;
  device_id: string | null;
  ip_hash: string | null;
  created_at: Timestamp;
}

export interface RateLimitEventsTable {
  id: Generated<string>;
  action: string;
  key_hash: string;
  created_at: Timestamp;
}

export interface Database extends CatalogDatabase {
  users: UsersTable;
  user_preferences: UserPreferencesTable;
  devices: DevicesTable;
  refresh_tokens: RefreshTokensTable;
  verification_codes: VerificationCodesTable;
  consents: ConsentsTable;
  email_outbox: EmailOutboxTable;
  audit_log: AuditLogTable;
  rate_limit_events: RateLimitEventsTable;
}
