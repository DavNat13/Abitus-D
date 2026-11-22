import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, login, newDevice, post, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

describe('sessions and refresh rotation', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  const me = (token: string) =>
    ctx.app.inject({ method: 'GET', url: '/v1/me', headers: { authorization: `Bearer ${token}` } });

  it('rotates the refresh token on each use', async () => {
    const first = await createVerifiedUser(ctx, uniqueEmail());
    const refreshed = await post(ctx, '/v1/auth/refresh', { refreshToken: first.refreshToken });
    expect(refreshed.statusCode).toBe(200);
    const second = refreshed.json<Tokens>();
    expect(second.refreshToken).not.toBe(first.refreshToken);
    expect((await me(second.accessToken)).statusCode).toBe(200);
  });

  it('reusing a rotated token revokes the whole family and warns by email', async () => {
    const email = uniqueEmail();
    const first = await createVerifiedUser(ctx, email);
    const second = (
      await post(ctx, '/v1/auth/refresh', { refreshToken: first.refreshToken })
    ).json<Tokens>();
    const reuse = await post(ctx, '/v1/auth/refresh', { refreshToken: first.refreshToken });
    expect(reuse.json()).toMatchObject({ code: 'AUTH_REFRESH_INVALID' });
    const afterReuse = await post(ctx, '/v1/auth/refresh', { refreshToken: second.refreshToken });
    expect(afterReuse.statusCode).toBe(401);
    expect(
      ctx.mailer
        .to(email)
        .some((m) => m.subject === 'Cerramos la sesión en todos tus dispositivos'),
    ).toBe(true);
  });

  it('logout ends the session of the device immediately', async () => {
    const tokens = await createVerifiedUser(ctx, uniqueEmail());
    const out = await post(ctx, '/v1/auth/logout', {}, undefined, tokens.accessToken);
    expect(out.statusCode).toBe(202);
    expect((await me(tokens.accessToken)).statusCode).toBe(401);
    expect(
      (await post(ctx, '/v1/auth/refresh', { refreshToken: tokens.refreshToken })).statusCode,
    ).toBe(401);
  });

  it('logout-all ends every device', async () => {
    const email = uniqueEmail();
    const phone = await createVerifiedUser(ctx, email);
    const tablet = (await login(ctx, email, newDevice('Tablet'))).json<Tokens>();
    await post(ctx, '/v1/auth/logout-all', {}, undefined, phone.accessToken);
    expect((await me(tablet.accessToken)).statusCode).toBe(401);
    expect(
      (await post(ctx, '/v1/auth/refresh', { refreshToken: tablet.refreshToken })).statusCode,
    ).toBe(401);
  });

  it('rejects missing, malformed and tampered access tokens', async () => {
    const tokens = await createVerifiedUser(ctx, uniqueEmail());
    expect((await ctx.app.inject({ method: 'GET', url: '/v1/me' })).statusCode).toBe(401);
    expect((await me('not-a-jwt')).statusCode).toBe(401);
    const parts = tokens.accessToken.split('.');
    const forged = `${parts[0] ?? ''}.${Buffer.from('{"sub":"x","did":"y"}').toString('base64url')}.${parts[2] ?? ''}`;
    expect((await me(forged)).statusCode).toBe(401);
  });
});
