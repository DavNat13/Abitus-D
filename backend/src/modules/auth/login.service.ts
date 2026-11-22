// Login with constant-time checks, failure limits per email and new device alerts.
import type { AppDeps } from '../../app/app-deps.js';
import { writeAudit } from '../../shared/audit-log.js';
import { burnPasswordCheck, verifyPassword } from '../../shared/crypto/password-hasher.js';
import { AppError } from '../../shared/errors.js';
import { AUTH_LIMITS } from './auth-limits.js';
import type { DeviceInput, TokensResponse } from './auth.schemas.js';
import { SessionService } from './session.service.js';
import { UserRepository } from './user.repository.js';

export class LoginService {
  private readonly users: UserRepository;
  private readonly sessions: SessionService;

  constructor(private readonly deps: AppDeps) {
    this.users = new UserRepository(deps.db);
    this.sessions = new SessionService(deps);
  }

  async login(
    email: string,
    password: string,
    device: DeviceInput,
    ipHash: string,
  ): Promise<TokensResponse> {
    await this.deps.limiter.consume(AUTH_LIMITS.loginPerIp, ipHash);
    await this.deps.limiter.assertAllowed(AUTH_LIMITS.loginFailuresPerEmail, email);
    const user = await this.users.findByEmail(email);
    const valid =
      user === undefined
        ? await burnPasswordCheck(password)
        : await verifyPassword(user.passwordHash, password);
    if (user === undefined || !valid) {
      await this.deps.limiter.record(AUTH_LIMITS.loginFailuresPerEmail, email);
      await writeAudit(this.deps.db, { userId: user?.id ?? null, event: 'login_failed', ipHash });
      throw new AppError('AUTH_INVALID_CREDENTIALS');
    }
    if (!user.verified) throw new AppError('AUTH_EMAIL_NOT_VERIFIED');
    const { tokens, isNewDevice } = await this.sessions.open(user.id, device);
    await writeAudit(this.deps.db, {
      userId: user.id,
      event: 'login',
      deviceId: tokens.deviceId,
      ipHash,
    });
    if (isNewDevice) {
      await writeAudit(this.deps.db, {
        userId: user.id,
        event: 'new_device',
        deviceId: tokens.deviceId,
        ipHash,
      });
      await this.deps.outbox.send({
        template: 'new_device',
        to: user.email,
        locale: user.locale,
        payload: {
          deviceName: device.name,
          when: new Date().toISOString().slice(0, 16).replace('T', ' '),
        },
        userId: user.id,
      });
    }
    return tokens;
  }
}
