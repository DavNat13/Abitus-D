import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import {
  createVerifiedUser,
  login,
  newDevice,
  register,
  STRONG_PASSWORD,
} from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

describe('login', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  it('answers the same for an unknown email and a wrong password', async () => {
    const email = uniqueEmail();
    await createVerifiedUser(ctx, email);
    const wrong = await login(ctx, email, newDevice(), 'Wrong-Password-123');
    const unknown = await login(ctx, uniqueEmail(), newDevice(), 'Wrong-Password-123');
    expect(wrong.statusCode).toBe(401);
    expect(wrong.body).toBe(unknown.body);
  });

  it('requires a verified email', async () => {
    const email = uniqueEmail();
    await register(ctx, email);
    const response = await login(ctx, email);
    expect(response.json()).toMatchObject({ code: 'AUTH_EMAIL_NOT_VERIFIED' });
  });

  it('warns by email only for a new device', async () => {
    const email = uniqueEmail();
    const device = newDevice('Pixel 8');
    await createVerifiedUser(ctx, email, device);
    expect((await login(ctx, email, device)).statusCode).toBe(200);
    expect(
      ctx.mailer.to(email).some((m) => m.subject === 'Nuevo inicio de sesión en tu cuenta'),
    ).toBe(false);
    expect((await login(ctx, email, newDevice('Galaxy S24'))).statusCode).toBe(200);
    const alert = ctx.mailer
      .to(email)
      .find((m) => m.subject === 'Nuevo inicio de sesión en tu cuenta');
    expect(alert?.text).toContain('Galaxy S24');
  });

  it('blocks an email after 10 failed attempts even with the right password', async () => {
    const email = uniqueEmail();
    await createVerifiedUser(ctx, email);
    for (let i = 0; i < 10; i += 1) await login(ctx, email, newDevice(), 'Wrong-Password-123');
    const blocked = await login(ctx, email, newDevice(), STRONG_PASSWORD);
    expect(blocked.statusCode).toBe(429);
    expect(blocked.json()).toMatchObject({ code: 'RATE_LIMITED' });
  });

  it('never logs the password or the email', async () => {
    const lines: string[] = [];
    const email = uniqueEmail('secret');
    const original = process.stdout.write.bind(process.stdout);
    process.stdout.write = (chunk: string | Uint8Array) => {
      lines.push(typeof chunk === 'string' ? chunk : Buffer.from(chunk).toString());
      return true;
    };
    try {
      await login(ctx, email, newDevice(), 'Never-Logged-Password-1');
    } finally {
      process.stdout.write = original;
    }
    expect(lines.join('')).not.toContain('Never-Logged-Password-1');
    expect(lines.join('')).not.toContain(email);
  });
});
