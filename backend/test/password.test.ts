import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import {
  createVerifiedUser,
  login,
  newDevice,
  post,
  STRONG_PASSWORD,
  type Tokens,
} from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

const NEW_PASSWORD = 'Deadlift-Tempo-2027?';

describe('password recovery and change', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  it('answers identically whether the email exists or not', async () => {
    const email = uniqueEmail();
    await createVerifiedUser(ctx, email);
    const known = await post(ctx, '/v1/auth/password/forgot', { email });
    const unknownEmail = uniqueEmail();
    const unknown = await post(ctx, '/v1/auth/password/forgot', { email: unknownEmail });
    expect(known.statusCode).toBe(202);
    expect(known.body).toBe(unknown.body);
    expect(ctx.mailer.to(unknownEmail)).toHaveLength(0);
  });

  it('resets with the code, ends every session and confirms by email', async () => {
    const email = uniqueEmail();
    const session = await createVerifiedUser(ctx, email);
    await post(ctx, '/v1/auth/password/forgot', { email });
    const code = ctx.mailer.lastCode(email);
    const reset = await post(ctx, '/v1/auth/password/reset', {
      email,
      code,
      newPassword: NEW_PASSWORD,
    });
    expect(reset.statusCode).toBe(202);
    expect(
      (await post(ctx, '/v1/auth/refresh', { refreshToken: session.refreshToken })).statusCode,
    ).toBe(401);
    expect((await login(ctx, email, newDevice(), STRONG_PASSWORD)).statusCode).toBe(401);
    expect((await login(ctx, email, newDevice(), NEW_PASSWORD)).statusCode).toBe(200);
    expect(ctx.mailer.to(email).some((m) => m.subject === 'Tu contraseña fue cambiada')).toBe(true);
    const stored = await ctx.db
      .selectFrom('email_outbox')
      .select('payload')
      .where('to_email', '=', email)
      .where('template', '=', 'reset_code')
      .executeTakeFirstOrThrow();
    expect(stored.payload).toEqual({});
  });

  it('change requires the current password and keeps only the current device', async () => {
    const email = uniqueEmail();
    const phone = await createVerifiedUser(ctx, email);
    const tablet = (await login(ctx, email, newDevice('Tablet'))).json<Tokens>();
    const change = (current: string) =>
      post(
        ctx,
        '/v1/auth/password/change',
        { currentPassword: current, newPassword: NEW_PASSWORD },
        undefined,
        phone.accessToken,
      );
    expect((await change('Wrong-Password-123')).statusCode).toBe(401);
    expect((await change(STRONG_PASSWORD)).statusCode).toBe(202);
    expect(
      (await post(ctx, '/v1/auth/refresh', { refreshToken: tablet.refreshToken })).statusCode,
    ).toBe(401);
    expect(
      (await post(ctx, '/v1/auth/refresh', { refreshToken: phone.refreshToken })).statusCode,
    ).toBe(200);
  });
});
