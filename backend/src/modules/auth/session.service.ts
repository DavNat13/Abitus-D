// Sessions per device: access JWT plus a rotating refresh token. Reusing a rotated token
// revokes its whole family and warns the owner by email.
import type { AppDeps } from '../../app/app-deps.js';
import { writeAudit } from '../../shared/audit-log.js';
import { keyedHash, newRefreshToken } from '../../shared/crypto/secure-hash.js';
import { AppError } from '../../shared/errors.js';
import { newId } from '../../shared/ids.js';
import { ACCESS_TOKEN_TTL_SECONDS, signAccessToken } from './access-token.js';
import type { DeviceInput, TokensResponse } from './auth.schemas.js';
import { DeviceRepository } from './device.repository.js';
import { SessionRepository } from './session.repository.js';
import { UserRepository } from './user.repository.js';

const REFRESH_TTL_MS = 60 * 24 * 60 * 60 * 1000;

export class SessionService {
  private readonly sessions: SessionRepository;
  private readonly users: UserRepository;
  private readonly devices: DeviceRepository;

  constructor(private readonly deps: AppDeps) {
    this.sessions = new SessionRepository(deps.db);
    this.users = new UserRepository(deps.db);
    this.devices = new DeviceRepository(deps.db);
  }

  /** Opens a session for a device. Reports whether the device is new to the account. */
  async open(
    userId: string,
    device: DeviceInput,
  ): Promise<{ tokens: TokensResponse; isNewDevice: boolean }> {
    const hadDevices = (await this.devices.countDevices(userId)) > 0;
    const { deviceId, isNew } = await this.devices.upsertDevice(userId, device);
    const tokens = await this.issue(userId, deviceId, newId());
    return { tokens, isNewDevice: isNew && hadDevices };
  }

  async refresh(refreshToken: string): Promise<TokensResponse> {
    const row = await this.sessions.findRefreshToken(this.hash(refreshToken));
    if (row === undefined || row.revoked) throw new AppError('AUTH_REFRESH_INVALID');
    if (row.rotated || !(await this.sessions.markRotated(row.id))) {
      await this.handleReuse(row.userId, row.familyId, row.deviceId);
      throw new AppError('AUTH_REFRESH_INVALID');
    }
    const active = await this.devices.isDeviceActive(row.userId, row.deviceId);
    if (row.expiresAt.getTime() < Date.now() || !active) throw new AppError('AUTH_REFRESH_INVALID');
    return this.issue(row.userId, row.deviceId, row.familyId);
  }

  async logout(userId: string, deviceId: string): Promise<void> {
    await this.sessions.revokeDevices(userId, { only: deviceId });
    await writeAudit(this.deps.db, { userId, event: 'logout', deviceId });
  }

  async logoutAll(userId: string): Promise<void> {
    await this.sessions.revokeDevices(userId, {});
    await writeAudit(this.deps.db, { userId, event: 'logout_all' });
    await this.warn(userId, 'logout_all');
  }

  private async handleReuse(userId: string, familyId: string, deviceId: string): Promise<void> {
    await this.sessions.revokeFamily(familyId);
    await writeAudit(this.deps.db, { userId, event: 'token_reuse', deviceId });
    await this.warn(userId, 'token_reuse');
  }

  private async warn(userId: string, reason: 'logout_all' | 'token_reuse'): Promise<void> {
    const user = await this.users.findById(userId);
    if (user === undefined) return;
    await this.deps.outbox.send({
      template: 'sessions_revoked',
      to: user.email,
      locale: user.locale,
      payload: { reason },
      userId,
    });
  }

  private async issue(userId: string, deviceId: string, familyId: string): Promise<TokensResponse> {
    const refreshToken = newRefreshToken();
    await this.sessions.insertRefreshToken(
      { userId, deviceId, familyId, expiresAt: new Date(Date.now() + REFRESH_TTL_MS) },
      this.hash(refreshToken),
    );
    const accessToken = await signAccessToken(this.deps.config.secrets.jwtAccess, {
      userId,
      deviceId,
    });
    return {
      accessToken,
      accessTokenExpiresIn: ACCESS_TOKEN_TTL_SECONDS,
      refreshToken,
      userId,
      deviceId,
    };
  }

  private hash(token: string): string {
    return keyedHash(this.deps.config.secrets.refreshTokenPepper, token);
  }
}
