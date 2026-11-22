// Rate limit rules of the auth module (07-BACKEND.md). Values are tuned in Phase 10.
import type { LimitRule } from '../../shared/rate-limiter.js';

const MINUTE = 60;
const HOUR = 60 * MINUTE;

export const AUTH_LIMITS = {
  registerPerIp: { action: 'register_ip', max: 10, windowSeconds: HOUR },
  registerPerEmail: { action: 'register_email', max: 5, windowSeconds: HOUR },
  verifyPerIp: { action: 'verify_ip', max: 30, windowSeconds: 15 * MINUTE },
  resendPerIp: { action: 'resend_ip', max: 20, windowSeconds: HOUR },
  loginPerIp: { action: 'login_ip', max: 50, windowSeconds: 15 * MINUTE },
  loginFailuresPerEmail: { action: 'login_fail_email', max: 10, windowSeconds: 15 * MINUTE },
  forgotPerIp: { action: 'forgot_ip', max: 20, windowSeconds: HOUR },
  forgotPerEmail: { action: 'forgot_email', max: 5, windowSeconds: HOUR },
  resetPerIp: { action: 'reset_ip', max: 30, windowSeconds: 15 * MINUTE },
  refreshPerIp: { action: 'refresh_ip', max: 120, windowSeconds: 15 * MINUTE },
  changePasswordPerUser: { action: 'change_password_user', max: 10, windowSeconds: HOUR },
} as const satisfies Record<string, LimitRule>;

/** Minimum duration of responses that must not reveal whether an account exists. */
export const NON_ENUMERATING_RESPONSE_MS = 1500;
