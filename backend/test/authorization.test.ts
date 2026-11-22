// Access to another user's resources must always fail (golden rule 21).
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, login, newDevice, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

describe('authorization between users', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  const call = (method: 'GET' | 'DELETE', url: string, token: string) =>
    ctx.app.inject({ method, url, headers: { authorization: `Bearer ${token}` } });

  it('a user only sees their own profile and devices', async () => {
    const emailA = uniqueEmail('a');
    const alice = await createVerifiedUser(ctx, emailA);
    const bob = await createVerifiedUser(ctx, uniqueEmail('b'));
    const profile = await call('GET', '/v1/me', alice.accessToken);
    expect(profile.json()).toMatchObject({ id: alice.userId, email: emailA });
    const devices = await call('GET', '/v1/me/devices', bob.accessToken);
    const ids = devices.json<{ id: string }[]>().map((d) => d.id);
    expect(ids).toEqual([bob.deviceId]);
  });

  it("a user cannot revoke another user's device", async () => {
    const alice = await createVerifiedUser(ctx, uniqueEmail('a'));
    const bob = await createVerifiedUser(ctx, uniqueEmail('b'));
    const attempt = await call('DELETE', `/v1/me/devices/${alice.deviceId}`, bob.accessToken);
    expect(attempt.statusCode).toBe(404);
    expect((await call('GET', '/v1/me', alice.accessToken)).statusCode).toBe(200);
  });

  it('a user can revoke their own other device', async () => {
    const email = uniqueEmail();
    const phone = await createVerifiedUser(ctx, email);
    const tablet = (await login(ctx, email, newDevice('Tablet'))).json<Tokens>();
    expect(
      (await call('DELETE', `/v1/me/devices/${tablet.deviceId}`, phone.accessToken)).statusCode,
    ).toBe(204);
    expect((await call('GET', '/v1/me', tablet.accessToken)).statusCode).toBe(401);
  });

  it('scheduled jobs require the job secret', async () => {
    const anonymous = await ctx.app.inject({ method: 'POST', url: '/internal/jobs/cleanup' });
    expect(anonymous.statusCode).toBe(401);
    const user = await createVerifiedUser(ctx, uniqueEmail());
    const withUserToken = await ctx.app.inject({
      method: 'POST',
      url: '/internal/jobs/cleanup',
      headers: { authorization: `Bearer ${user.accessToken}` },
    });
    expect(withUserToken.statusCode).toBe(401);
    const allowed = await ctx.app.inject({
      method: 'POST',
      url: '/internal/jobs/cleanup',
      headers: { authorization: `Bearer ${ctx.config.secrets.jobs}` },
    });
    expect(allowed.statusCode).toBe(200);
  });
});
