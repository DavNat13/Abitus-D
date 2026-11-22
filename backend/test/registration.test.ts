import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, newDevice, post, register } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

describe('registration and verification', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  it('registers, mails a code and verifies into a session', async () => {
    const email = uniqueEmail();
    const response = await register(ctx, email);
    expect(response.statusCode).toBe(202);
    expect(ctx.mailer.to(email)[0]?.subject).toBe('Tu código de verificación');
    const verified = await post(ctx, '/v1/auth/verify-email', {
      email,
      code: ctx.mailer.lastCode(email),
      device: newDevice(),
    });
    expect(verified.statusCode).toBe(200);
    expect(verified.json()).toMatchObject({ accessTokenExpiresIn: 900 });
    expect(ctx.mailer.to(email).map((mail) => mail.subject)).toContain('Bienvenido a Abitus-D');
  });

  it('answers identically when the email already has an account', async () => {
    const email = uniqueEmail();
    await createVerifiedUser(ctx, email);
    const mailsBefore = ctx.mailer.to(email).length;
    const existing = await register(ctx, email);
    const fresh = await register(ctx, uniqueEmail());
    expect(existing.statusCode).toBe(fresh.statusCode);
    expect(existing.body).toBe(fresh.body);
    expect(ctx.mailer.to(email).length).toBe(mailsBefore);
  });

  it('rejects weak passwords, missing age confirmation and unknown fields', async () => {
    const weak = await register(ctx, uniqueEmail(), 'qwertyuiop');
    expect(weak.json()).toMatchObject({ code: 'AUTH_WEAK_PASSWORD' });
    const base = {
      email: uniqueEmail(),
      password: 'Rowing-Squat-2026!',
      locale: 'es',
      termsVersion: '1',
      privacyVersion: '1',
    };
    const noAge = await post(ctx, '/v1/auth/register', { ...base, ageConfirmed: false });
    expect(noAge.statusCode).toBe(400);
    const extra = await post(ctx, '/v1/auth/register', {
      ...base,
      ageConfirmed: true,
      role: 'admin',
    });
    expect(extra.json()).toMatchObject({ code: 'VALIDATION_FAILED' });
  });

  it('locks a code after 5 wrong attempts', async () => {
    const email = uniqueEmail();
    await register(ctx, email);
    const good = ctx.mailer.lastCode(email);
    const wrong = good === '000000' ? '111111' : '000000';
    for (let i = 0; i < 5; i += 1) {
      const attempt = await post(ctx, '/v1/auth/verify-email', {
        email,
        code: wrong,
        device: newDevice(),
      });
      expect(attempt.json()).toMatchObject({ code: 'AUTH_INVALID_CODE' });
    }
    const late = await post(ctx, '/v1/auth/verify-email', {
      email,
      code: good,
      device: newDevice(),
    });
    expect(late.json()).toMatchObject({ code: 'AUTH_CODE_EXPIRED' });
  });

  it('rejects a code that was already used', async () => {
    const email = uniqueEmail();
    await createVerifiedUser(ctx, email);
    const reused = await post(ctx, '/v1/auth/verify-email', {
      email,
      code: ctx.mailer.lastCode(email),
      device: newDevice(),
    });
    expect(reused.statusCode).toBe(400);
  });

  it('resend respects the 60 second wait silently', async () => {
    const email = uniqueEmail();
    await register(ctx, email);
    const resent = await post(ctx, '/v1/auth/resend-code', { email });
    expect(resent.statusCode).toBe(202);
    expect(ctx.mailer.to(email).length).toBe(1);
  });

  it('keeps the code mail pending when SMTP fails', async () => {
    const email = uniqueEmail();
    ctx.mailer.failNext = true;
    expect((await register(ctx, email)).statusCode).toBe(202);
    const row = await ctx.db
      .selectFrom('email_outbox')
      .select(['status', 'attempts'])
      .where('to_email', '=', email)
      .executeTakeFirstOrThrow();
    expect(row).toEqual({ status: 'pending', attempts: 1 });
  });
});
