// Devices with a session. One row per user and app installation.
import type { Db } from '../../shared/database.js';
import { newId } from '../../shared/ids.js';
import type { DeviceInput } from './auth.schemas.js';

export class DeviceRepository {
  constructor(private readonly db: Db) {}

  /** Returns the device id and whether this installation is new for the user. */
  async upsertDevice(
    userId: string,
    device: DeviceInput,
  ): Promise<{ deviceId: string; isNew: boolean }> {
    const existing = await this.db
      .selectFrom('devices')
      .select('id')
      .where('user_id', '=', userId)
      .where('installation_id', '=', device.installationId)
      .executeTakeFirst();
    if (existing !== undefined) {
      await this.db
        .updateTable('devices')
        .set({
          name: device.name,
          app_version: device.appVersion,
          last_seen_at: new Date(),
          revoked_at: null,
        })
        .where('id', '=', existing.id)
        .execute();
      return { deviceId: existing.id, isNew: false };
    }
    const deviceId = newId();
    await this.db
      .insertInto('devices')
      .values({
        id: deviceId,
        user_id: userId,
        installation_id: device.installationId,
        name: device.name,
        app_version: device.appVersion,
      })
      .execute();
    return { deviceId, isNew: true };
  }

  async countDevices(userId: string): Promise<number> {
    const row = await this.db
      .selectFrom('devices')
      .select((eb) => eb.fn.countAll<string>().as('total'))
      .where('user_id', '=', userId)
      .executeTakeFirstOrThrow();
    return Number(row.total);
  }

  async isDeviceActive(userId: string, deviceId: string): Promise<boolean> {
    const row = await this.db
      .selectFrom('devices')
      .select('id')
      .where('id', '=', deviceId)
      .where('user_id', '=', userId)
      .where('revoked_at', 'is', null)
      .executeTakeFirst();
    return row !== undefined;
  }
}
