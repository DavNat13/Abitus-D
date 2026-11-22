// Closed input schemas (unknown fields are rejected) with explicit length limits.
import { z } from 'zod';

const MAX_PASSWORD_LENGTH = 128;

export const emailSchema = z.string().trim().toLowerCase().max(254).pipe(z.email());

export const passwordSchema = z.string().min(1).max(MAX_PASSWORD_LENGTH);
export const localeSchema = z.enum(['es', 'en']);
export const codeSchema = z.string().regex(/^\d{6}$/);

export const deviceSchema = z.strictObject({
  installationId: z.uuid(),
  name: z.string().trim().min(1).max(80),
  appVersion: z.string().trim().min(1).max(32),
});

export const registerBody = z.strictObject({
  email: emailSchema,
  password: passwordSchema,
  locale: localeSchema,
  ageConfirmed: z.literal(true),
  termsVersion: z.string().min(1).max(20),
  privacyVersion: z.string().min(1).max(20),
});

export const verifyEmailBody = z.strictObject({
  email: emailSchema,
  code: codeSchema,
  device: deviceSchema,
});

export const emailOnlyBody = z.strictObject({ email: emailSchema });

export const loginBody = z.strictObject({
  email: emailSchema,
  password: passwordSchema,
  device: deviceSchema,
});

export const refreshBody = z.strictObject({ refreshToken: z.string().min(20).max(100) });

export const resetPasswordBody = z.strictObject({
  email: emailSchema,
  code: codeSchema,
  newPassword: passwordSchema,
});

export const changePasswordBody = z.strictObject({
  currentPassword: passwordSchema,
  newPassword: passwordSchema,
});

export const tokensResponse = z.object({
  accessToken: z.string(),
  accessTokenExpiresIn: z.number().int(),
  refreshToken: z.string(),
  userId: z.uuid(),
  deviceId: z.uuid(),
});

export const acceptedResponse = z.object({ status: z.literal('accepted') });

export type DeviceInput = z.infer<typeof deviceSchema>;
export type RegisterInput = z.infer<typeof registerBody>;
export type TokensResponse = z.infer<typeof tokensResponse>;
