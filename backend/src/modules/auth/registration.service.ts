// Registration, email verification and code resend. Responses are identical whether
// or not the email already has an account (golden rule 23).
import type { AppDeps } from '../../app/app-deps.js';
import { writeAudit } from '../../shared/audit-log.js';
import { hashPassword } from '../../shared/crypto/password-hasher.js';
import { AppError } from '../../shared/errors.js';
import { AUTH_LIMITS } from './auth-limits.js';
import type { DeviceInput, RegisterInput, TokensResponse } from './auth.schemas.js';
import { CodeService } from './code.service.js';
import { assertStrongPassword } from './password-policy.js';
import { SessionService } from './session.service.js';
import { UserRepository } from './user.repository.js';

export class RegistrationService {
  private readonly users: UserRepository;
  private readonly codes: CodeService;
  private readonly sessions: SessionService;

  constructor(private readonly deps: AppDeps) {
    this.users = new UserRepository(deps.db);
    this.codes = new CodeService(deps.db, deps.config.secrets.codePepper);
    this.sessions = new SessionService(deps);
  }

  async register(input: RegisterInput, ipHash: string): Promise<void> {
    await this.deps.limiter.consume(AUTH_LIMITS.registerPerIp, ipHash);
    await this.deps.limiter.consume(AUTH_LIMITS.registerPerEmail, input.email);
    assertStrongPassword(input.password, input.email);
    const passwordHash = await hashPassword(input.password);
    const existing = await this.users.findByEmail(input.email);
    if (existing?.verified === true) return;
    const userId = await this.users.savePending({ ...input, passwordHash }, existing?.id);
    await writeAudit(this.deps.db, { userId, event: 'register', ipHash });
    await this.sendCode(input.email, input.locale, userId, ipHash);
  }

  async verifyEmail(
    email: string,
    code: string,
    device: DeviceInput,
    ipHash: string,
  ): Promise<TokensResponse> {
    await this.deps.limiter.consume(AUTH_LIMITS.verifyPerIp, ipHash);
    await this.codes.consume(email, 'verify_email', code);
    const user = await this.users.findByEmail(email);
    if (user === undefined) throw new AppError('AUTH_INVALID_CODE');
    await this.users.markVerified(user.id);
    const { tokens } = await this.sessions.open(user.id, device);
    await writeAudit(this.deps.db, {
      userId: user.id,
      event: 'email_verified',
      deviceId: tokens.deviceId,
      ipHash,
    });
    await this.deps.outbox.send({
      template: 'welcome',
      to: user.email,
      locale: user.locale,
      payload: {},
      userId: user.id,
    });
    return tokens;
  }

  async resendCode(email: string, ipHash: string): Promise<void> {
    await this.deps.limiter.consume(AUTH_LIMITS.resendPerIp, ipHash);
    const user = await this.users.findByEmail(email);
    if (user === undefined || user.verified) return;
    await this.sendCode(user.email, user.locale, user.id, ipHash);
  }

  /** Issues and mails a code. Cooldown and daily cap are silent to avoid enumeration. */
  private async sendCode(
    email: string,
    locale: 'es' | 'en',
    userId: string,
    ipHash: string,
  ): Promise<void> {
    let code: string;
    try {
      code = await this.codes.issue(email, 'verify_email', ipHash);
    } catch (error) {
      if (error instanceof AppError) return;
      throw error;
    }
    await this.deps.outbox.send({
      template: 'verification_code',
      to: email,
      locale,
      payload: { code },
      userId,
    });
  }
}
