// Domain errors with a stable code. The client localizes messages by code.
export type ErrorCode =
  | 'VALIDATION_FAILED'
  | 'NOT_FOUND'
  | 'RATE_LIMITED'
  | 'AUTH_INVALID_CREDENTIALS'
  | 'AUTH_EMAIL_NOT_VERIFIED'
  | 'AUTH_INVALID_CODE'
  | 'AUTH_CODE_EXPIRED'
  | 'AUTH_RESEND_TOO_SOON'
  | 'AUTH_WEAK_PASSWORD'
  | 'AUTH_UNAUTHORIZED'
  | 'AUTH_REFRESH_INVALID'
  | 'INTERNAL_ERROR';

const STATUS_BY_CODE: Record<ErrorCode, number> = {
  VALIDATION_FAILED: 400,
  NOT_FOUND: 404,
  RATE_LIMITED: 429,
  AUTH_INVALID_CREDENTIALS: 401,
  AUTH_EMAIL_NOT_VERIFIED: 403,
  AUTH_INVALID_CODE: 400,
  AUTH_CODE_EXPIRED: 400,
  AUTH_RESEND_TOO_SOON: 429,
  AUTH_WEAK_PASSWORD: 400,
  AUTH_UNAUTHORIZED: 401,
  AUTH_REFRESH_INVALID: 401,
  INTERNAL_ERROR: 500,
};

export class AppError extends Error {
  readonly code: ErrorCode;
  readonly status: number;

  constructor(code: ErrorCode) {
    super(code);
    this.name = 'AppError';
    this.code = code;
    this.status = STATUS_BY_CODE[code];
  }
}
