// Password recovery with a code, and password change with an active session.
// Both end other sessions and confirm the change by email.
import type { AppDeps } from '../../app/app-deps.js';
import { writeAudit } from '../../shared/audit-log.js';
import { hashPassword, verifyPassword } from '../../shared/crypto/password-hasher.js';
import { AppError } from '../../shared/errors.js';
import { AUTH_LIMITS } from './auth-limits.js';
import { CodeService } from './code.service.js';
import { assertStrongPassword } from './password-policy.js';
import { SessionRepository } from './session.repository.js';
import { type UserRow, UserRepository } from './user.repository.js';

export class PasswordService {
  private readonly users: UserRepository;
  private readonly sessions: SessionRepository;
  private readonly codes: CodeService;

  constructor(private readonly deps: AppDeps) {
    this.users = new UserRepository(deps.db);
    this.sessions = new SessionRepository(deps.db);
    this.codes = new CodeService(deps.db, deps.config.secrets.codePepper);
  }

  async forgot(email: string, ipHash: string): Promise<void> {
    await this.deps.limiter.consume(AUTH_LIMITS.forgotPerIp, ipHash);
    await this.deps.limiter.consume(AUTH_LIMITS.forgotPerEmail, email);
    const user = await this.users.findByEmail(email);
    if (user?.verified !== true) return;
    let code: string;
    try {
      code = await this.codes.issue(email, 'reset_password', ipHash);
    } catch (error) {
      if (error instanceof AppError) return;
      throw error;
    }
    await this.deps.outbox.send({
      template: 'reset_code',
      to: email,
      locale: user.locale,
      payload: { code },
      userId: user.id,
    });
  }

  async reset(email: string, code: string, newPassword: string, ipHash: string): Promise<void> {
    await this.deps.limiter.consume(AUTH_LIMITS.resetPerIp, ipHash);
    assertStrongPassword(newPassword, email);
    await this.codes.consume(email, 'reset_password', code);
    const user = await this.users.findByEmail(email);
    if (user === undefined) throw new AppError('AUTH_INVALID_CODE');
    await this.users.updatePassword(user.id, await hashPassword(newPassword));
    await this.sessions.revokeDevices(user.id, {});
    await writeAudit(this.deps.db, { userId: user.id, event: 'password_reset', ipHash });
    await this.confirm(user);
  }

  async change(
    userId: string,
    deviceId: string,
    current: string,
    newPassword: string,
  ): Promise<void> {
    await this.deps.limiter.consume(AUTH_LIMITS.changePasswordPerUser, userId);
    const user = await this.users.findById(userId);
    if (user === undefined || !(await verifyPassword(user.passwordHash, current))) {
      throw new AppError('AUTH_INVALID_CREDENTIALS');
    }
    assertStrongPassword(newPassword, user.email);
    await this.users.updatePassword(user.id, await hashPassword(newPassword));
    await this.sessions.revokeDevices(user.id, { except: deviceId });
    await writeAudit(this.deps.db, { userId, event: 'password_change', deviceId });
    await this.confirm(user);
  }

  private async confirm(user: UserRow): Promise<void> {
    await this.deps.outbox.send({
      template: 'password_changed',
      to: user.email,
      locale: user.locale,
      payload: {},
      userId: user.id,
    });
  }
}
