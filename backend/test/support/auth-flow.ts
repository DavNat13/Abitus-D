// Helpers that drive the public API like the app does.
import { randomUUID } from 'node:crypto';
import type { TestContext } from './test-app.js';
import { randomIp } from './test-app.js';

export const STRONG_PASSWORD = 'Rowing-Squat-2026!';

export interface Tokens {
  accessToken: string;
  refreshToken: string;
  userId: string;
  deviceId: string;
}

export function newDevice(name = 'Pixel 8') {
  return { installationId: randomUUID(), name, appVersion: '0.1.0' };
}

export async function post(
  ctx: TestContext,
  url: string,
  payload: object,
  ip = randomIp(),
  token?: string,
) {
  return ctx.app.inject({
    method: 'POST',
    url,
    payload,
    headers: {
      'x-forwarded-for': ip,
      ...(token === undefined ? {} : { authorization: `Bearer ${token}` }),
    },
  });
}

export async function register(ctx: TestContext, email: string, password = STRONG_PASSWORD) {
  return post(ctx, '/v1/auth/register', {
    email,
    password,
    locale: 'es',
    ageConfirmed: true,
    termsVersion: '2026-11',
    privacyVersion: '2026-11',
  });
}

/** Registers and verifies an account; returns the first session tokens. */
export async function createVerifiedUser(
  ctx: TestContext,
  email: string,
  device = newDevice(),
): Promise<Tokens> {
  const registered = await register(ctx, email);
  if (registered.statusCode !== 202) throw new Error(`register failed: ${registered.body}`);
  const verified = await post(ctx, '/v1/auth/verify-email', {
    email,
    code: ctx.mailer.lastCode(email),
    device,
  });
  if (verified.statusCode !== 200) throw new Error(`verify failed: ${verified.body}`);
  return verified.json<Tokens>();
}

export async function login(
  ctx: TestContext,
  email: string,
  device = newDevice(),
  password = STRONG_PASSWORD,
) {
  return post(ctx, '/v1/auth/login', { email, password, device });
}
