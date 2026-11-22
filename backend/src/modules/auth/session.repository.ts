// Refresh tokens and session revocation. Rotation uses a conditional update to detect races and reuse.
import type { Db } from '../../shared/database.js';
import { newId } from '../../shared/ids.js';

export interface RefreshTokenRow {
  readonly id: string;
  readonly userId: string;
  readonly deviceId: string;
  readonly familyId: string;
  readonly expiresAt: Date;
  readonly rotated: boolean;
  readonly revoked: boolean;
}

export class SessionRepository {
  constructor(private readonly db: Db) {}

  async insertRefreshToken(
    row: Omit<RefreshTokenRow, 'id' | 'rotated' | 'revoked'>,
    tokenHash: string,
  ): Promise<void> {
    await this.db
      .insertInto('refresh_tokens')
      .values({
        id: newId(),
        user_id: row.userId,
        device_id: row.deviceId,
        family_id: row.familyId,
        token_hash: tokenHash,
        expires_at: row.expiresAt,
      })
      .execute();
  }

  async findRefreshToken(tokenHash: string): Promise<RefreshTokenRow | undefined> {
    const row = await this.db
      .selectFrom('refresh_tokens')
      .selectAll()
      .where('token_hash', '=', tokenHash)
      .executeTakeFirst();
    if (row === undefined) return undefined;
    return {
      id: row.id,
      userId: row.user_id,
      deviceId: row.device_id,
      familyId: row.family_id,
      expiresAt: row.expires_at,
      rotated: row.rotated_at !== null,
      revoked: row.revoked_at !== null,
    };
  }

  /** Marks the token as rotated. Returns false if another request used it first. */
  async markRotated(tokenId: string): Promise<boolean> {
    const result = await this.db
      .updateTable('refresh_tokens')
      .set({ rotated_at: new Date() })
      .where('id', '=', tokenId)
      .where('rotated_at', 'is', null)
      .where('revoked_at', 'is', null)
      .executeTakeFirst();
    return result.numUpdatedRows === 1n;
  }

  async revokeFamily(familyId: string): Promise<void> {
    await this.db
      .updateTable('refresh_tokens')
      .set({ revoked_at: new Date() })
      .where('family_id', '=', familyId)
      .where('revoked_at', 'is', null)
      .execute();
  }

  /** Ends sessions of one device, of all devices, or of all but one device. */
  async revokeDevices(userId: string, scope: { only?: string; except?: string }): Promise<void> {
    const now = new Date();
    let tokens = this.db
      .updateTable('refresh_tokens')
      .set({ revoked_at: now })
      .where('user_id', '=', userId);
    let devices = this.db
      .updateTable('devices')
      .set({ revoked_at: now })
      .where('user_id', '=', userId);
    if (scope.only !== undefined) {
      tokens = tokens.where('device_id', '=', scope.only);
      devices = devices.where('id', '=', scope.only);
    }
    if (scope.except !== undefined) {
      tokens = tokens.where('device_id', '!=', scope.except);
      devices = devices.where('id', '!=', scope.except);
    }
    await tokens.where('revoked_at', 'is', null).execute();
    await devices.where('revoked_at', 'is', null).execute();
  }
}
