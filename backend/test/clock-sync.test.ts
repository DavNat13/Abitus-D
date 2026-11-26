import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';
import type { PushResult } from '../src/modules/sync/sync.service.js';

describe('favorites and timer profiles sync', () => {
  let ctx: TestContext;
  let user: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    user = await createVerifiedUser(ctx, uniqueEmail('clock'));
  });
  afterAll(() => ctx.close());

  const push = (table: string, row: object) =>
    ctx.app.inject({
      method: 'POST',
      url: '/v1/sync/push',
      headers: { authorization: `Bearer ${user.accessToken}` },
      payload: { changes: [{ table, op: 'upsert', row }] },
    });
  const at = '2026-11-26T00:00:00.000Z';
  const spec = JSON.stringify({
    phases: [
      { kind: 'work', durationS: 20 },
      { kind: 'rest', durationS: 10 },
    ],
    rounds: 8,
  });

  it('accepts a favorite and a valid timer profile', async () => {
    const fav = { id: randomUUID(), exerciseId: randomUUID(), updatedAt: at };
    expect((await push('exercise_favorites', fav)).json<PushResult>().accepted).toContain(fav.id);
    const profile = { id: randomUUID(), name: 'Tabata', mode: 'tabata', spec, updatedAt: at };
    expect((await push('timer_profiles', profile)).json<PushResult>().accepted).toContain(
      profile.id,
    );
  });

  it('rejects malformed timer specs', async () => {
    for (const bad of [
      '{"phases":[],"rounds":0}',
      'not json',
      JSON.stringify({ phases: [{ kind: 'nap', durationS: 5 }], rounds: 1 }),
    ]) {
      const row = { id: randomUUID(), name: 'X', mode: 'custom', spec: bad, updatedAt: at };
      expect((await push('timer_profiles', row)).statusCode).toBe(400);
    }
  });
});
