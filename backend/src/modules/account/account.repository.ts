// Account reads and writes. Every query is filtered by the authenticated user id.
import type { Db } from '../../shared/database.js';
import { newId } from '../../shared/ids.js';

export interface DeviceView {
  readonly id: string;
  readonly name: string;
  readonly platform: string;
  readonly appVersion: string;
  readonly firstSeenAt: string;
  readonly lastSeenAt: string;
}

export class AccountRepository {
  constructor(private readonly db: Db) {}

  async profile(userId: string) {
    return this.db
      .selectFrom('users')
      .select(['id', 'email', 'display_name', 'locale'])
      .where('id', '=', userId)
      .where('deleted_at', 'is', null)
      .executeTakeFirst();
  }

  async activeDevices(userId: string): Promise<DeviceView[]> {
    const rows = await this.db
      .selectFrom('devices')
      .select(['id', 'name', 'platform', 'app_version', 'first_seen_at', 'last_seen_at'])
      .where('user_id', '=', userId)
      .where('revoked_at', 'is', null)
      .orderBy('last_seen_at', 'desc')
      .execute();
    return rows.map((row) => ({
      id: row.id,
      name: row.name,
      platform: row.platform,
      appVersion: row.app_version,
      firstSeenAt: row.first_seen_at.toISOString(),
      lastSeenAt: row.last_seen_at.toISOString(),
    }));
  }

  async deviceBelongsTo(userId: string, deviceId: string): Promise<boolean> {
    const row = await this.db
      .selectFrom('devices')
      .select('id')
      .where('id', '=', deviceId)
      .where('user_id', '=', userId)
      .executeTakeFirst();
    return row !== undefined;
  }

  async addConsent(userId: string, document: 'privacy' | 'terms', version: string): Promise<void> {
    await this.db
      .insertInto('consents')
      .values({ id: newId(), user_id: userId, document, version })
      .execute();
  }
}
