import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';
import type { PushResult } from '../src/modules/sync/sync.service.js';

interface PullResult {
  changes: { table: string; row: Record<string, unknown>; op: string }[];
}

describe('athlete profile sync', () => {
  let ctx: TestContext;
  let user: Tokens;
  let other: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    user = await createVerifiedUser(ctx, uniqueEmail('athlete'));
    other = await createVerifiedUser(ctx, uniqueEmail('other'));
  });
  afterAll(() => ctx.close());

  const profile = () => ({
    id: user.userId,
    birthYear: 1990,
    sex: null,
    heightM: 1.75,
    weightKg: 72.5,
    trainingYears: 2,
    level: 'intermediate',
    disciplines: 'strength',
    goal: 'strength',
    secondaryGoal: null,
    daysPerWeek: 3,
    sessionDurationS: 3600,
    place: 'gym',
    equipment: 'barbell',
    healthConsent: false,
    healthLimited: null,
    injuries: '',
    pushUps: 20,
    pullUps: null,
    plankDurationS: 60,
    onboardingDone: true,
    updatedAt: '2026-11-25T00:00:00.000Z',
  });
  const push = (row: object, token = user.accessToken, op = 'upsert') =>
    ctx.app.inject({
      method: 'POST',
      url: '/v1/sync/push',
      headers: { authorization: `Bearer ${token}` },
      payload: { changes: [{ table: 'athlete_profiles', op, row }] },
    });
  const pull = (token = user.accessToken) =>
    ctx.app.inject({
      method: 'GET',
      url: '/v1/sync/pull?cursor=0',
      headers: { authorization: `Bearer ${token}` },
    });

  it('round trips SI measurements and optional answers', async () => {
    expect((await push(profile())).json<PushResult>().accepted).toContain(user.userId);
    const rows = (await pull()).json<PullResult>().changes;
    expect(rows.find((r) => r.table === 'athlete_profiles')?.row).toMatchObject(profile());
  });

  it('rejects health data without consent and invalid values', async () => {
    for (const invalid of [
      { healthLimited: true },
      { injuries: 'shoulder' },
      { heightM: 175 },
      { daysPerWeek: 8 },
    ]) {
      expect((await push({ ...profile(), ...invalid })).statusCode).toBe(400);
    }
  });

  it('does not expose or overwrite another account profile', async () => {
    expect((await pull(other.accessToken)).json<PullResult>().changes).toEqual([]);
    expect((await push(profile(), other.accessToken)).json<PushResult>().rejected).toEqual([
      { id: user.userId, reason: 'forbidden' },
    ]);
  });

  it('synchronizes deletion', async () => {
    const row = { id: user.userId, updatedAt: '2026-11-25T00:01:00Z' };
    expect((await push(row, user.accessToken, 'delete')).json<PushResult>().accepted).toContain(
      user.userId,
    );
    expect(
      (await pull()).json<PullResult>().changes.find((r) => r.table === 'athlete_profiles')?.op,
    ).toBe('delete');
  });
});
