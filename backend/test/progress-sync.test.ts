import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';
import type { PushResult } from '../src/modules/sync/sync.service.js';

interface PullResult {
  changes: { table: string; row: Record<string, unknown> }[];
}

describe('body measurements sync', () => {
  let ctx: TestContext;
  let user: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    user = await createVerifiedUser(ctx, uniqueEmail('measure'));
  });
  afterAll(() => ctx.close());

  const auth = () => ({ authorization: `Bearer ${user.accessToken}` });
  const push = (row: object) =>
    ctx.app.inject({
      method: 'POST',
      url: '/v1/sync/push',
      headers: auth(),
      payload: { changes: [{ table: 'body_measurements', op: 'upsert', row }] },
    });
  const row = () => ({
    id: randomUUID(),
    measuredOn: '2026-11-26',
    weightKg: 74.3,
    waistM: 0.82,
    chestM: null,
    hipM: null,
    armM: 0.355,
    thighM: null,
    note: '',
    updatedAt: '2026-11-26T08:00:00.000Z',
  });

  it('round trips SI measurements and a plain date', async () => {
    const m = row();
    expect((await push(m)).json<PushResult>().accepted).toContain(m.id);
    const pulled = await ctx.app.inject({
      method: 'GET',
      url: '/v1/sync/pull?cursor=0',
      headers: auth(),
    });
    expect(
      pulled.json<PullResult>().changes.find((c) => c.table === 'body_measurements')?.row,
    ).toMatchObject(m);
  });

  it('rejects centimeters sent as meters and impossible weights', async () => {
    for (const bad of [{ waistM: 82 }, { weightKg: 5 }, { measuredOn: '26/11/2026' }]) {
      expect((await push({ ...row(), ...bad })).statusCode).toBe(400);
    }
  });
});
