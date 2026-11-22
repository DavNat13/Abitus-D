// Mail templates of Phase 1 with their priority (15-CORREOS.md) and payload fields.
export const MAIL_PRIORITY = { critical: 1, high: 2, medium: 3, low: 4 } as const;

export interface MailPayloads {
  verification_code: { code: string };
  reset_code: { code: string };
  welcome: Record<string, never>;
  new_device: { deviceName: string; when: string };
  password_changed: Record<string, never>;
  sessions_revoked: { reason: 'logout_all' | 'token_reuse' };
}

export type MailTemplate = keyof MailPayloads;
export type MailLocale = 'es' | 'en';

export const TEMPLATE_PRIORITY: Record<MailTemplate, number> = {
  verification_code: MAIL_PRIORITY.critical,
  reset_code: MAIL_PRIORITY.critical,
  new_device: MAIL_PRIORITY.high,
  password_changed: MAIL_PRIORITY.high,
  sessions_revoked: MAIL_PRIORITY.high,
  welcome: MAIL_PRIORITY.low,
};

/** Templates whose payload holds a one-time code; it is wiped after sending. */
export const TEMPLATES_WITH_SECRETS: ReadonlySet<MailTemplate> = new Set([
  'verification_code',
  'reset_code',
]);
