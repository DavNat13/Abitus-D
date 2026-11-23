import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

interface Change {
  table: string;
  op: string;
  row: Record<string, unknown>;
}

const T = '2026-11-23T10:00:00Z';

describe('sync of workout sessions', () => {
  let ctx: TestContext;
  let alice: Tokens;
  let bob: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    alice = await createVerifiedUser(ctx, uniqueEmail('a'));
    bob = await createVerifiedUser(ctx, uniqueEmail('b'));
  });
  afterAll(() => ctx.close());

  const push = (token: string, changes: object[]) =>
    ctx.app.inject({
      method: 'POST',
      url: '/v1/sync/push',
      headers: { authorization: `Bearer ${token}` },
      payload: { changes },
    });
  const pull = async (token: string) =>
    (
      await ctx.app.inject({
        method: 'GET',
        url: '/v1/sync/pull?cursor=0',
        headers: { authorization: `Bearer ${token}` },
      })
    ).json<{ changes: Change[] }>().changes;

  const session = (id: string) => ({
    table: 'workout_sessions',
    op: 'upsert',
    row: {
      id,
      routineId: null,
      scheduledWorkoutId: null,
      title: 'Torso A',
      startedAt: '2026-11-23T09:00:00Z',
      endedAt: '2026-11-23T10:00:00Z',
      notes: '',
      sessionRpe: 8.5,
      sleepQuality: 4,
      energy: 3,
      bodyweightKg: 78.4,
      updatedAt: T,
    },
  });
  const set = (id: string, sessionExerciseId: string, exerciseId: string, extra: object = {}) => ({
    table: 'sets',
    op: 'upsert',
    row: {
      id,
      sessionExerciseId,
      exerciseId,
      performedAt: '2026-11-23T09:10:00Z',
      setIndex: 0,
      setType: 'normal',
      side: 'both',
      reps: 8,
      loadKg: 82.5,
      addedLoadKg: null,
      durationS: null,
      distanceM: null,
      rpe: 8,
      rir: 2,
      restS: 120,
      tempo: '3-1-1-0',
      completedAt: '2026-11-23T09:10:30Z',
      notes: '',
      updatedAt: T,
      ...extra,
    },
  });

  it('a logged session comes back with numbers and instants intact, only to its owner', async () => {
    const sessionId = randomUUID();
    const sessionExerciseId = randomUUID();
    const exerciseId = randomUUID();
    const setId = randomUUID();
    const response = await push(alice.accessToken, [
      session(sessionId),
      {
        table: 'session_exercises',
        op: 'upsert',
        row: {
          id: sessionExerciseId,
          sessionId,
          exerciseId,
          position: 0,
          groupId: '',
          notes: '',
          discomfort: 'shoulder_right',
          updatedAt: T,
        },
      },
      set(setId, sessionExerciseId, exerciseId),
    ]);
    expect(response.statusCode).toBe(200);
    const changes = await pull(alice.accessToken);
    const s = changes.find((c) => c.row.id === sessionId)?.row;
    expect(s).toMatchObject({
      sessionRpe: 8.5,
      bodyweightKg: 78.4,
      startedAt: '2026-11-23T09:00:00.000Z',
    });
    const row = changes.find((c) => c.row.id === setId)?.row;
    expect(row).toMatchObject({
      loadKg: 82.5,
      reps: 8,
      tempo: '3-1-1-0',
      completedAt: '2026-11-23T09:10:30.000Z',
    });
    expect((await pull(bob.accessToken)).some((c) => c.row.id === setId)).toBe(false);
  });

  it('pushing the same set twice keeps one row (no duplicates when a batch is retried)', async () => {
    const setId = randomUUID();
    const ids = [randomUUID(), randomUUID()] as const;
    await push(alice.accessToken, [set(setId, ids[0], ids[1])]);
    await push(alice.accessToken, [
      set(setId, ids[0], ids[1], { reps: 9, updatedAt: '2026-11-23T10:05:00Z' }),
    ]);
    const rows = (await pull(alice.accessToken)).filter((c) => c.row.id === setId);
    expect(rows).toHaveLength(1);
    expect(rows[0]?.row.reps).toBe(9);
  });

  it('rejects an unknown set type', async () => {
    const response = await push(alice.accessToken, [
      set(randomUUID(), randomUUID(), randomUUID(), { setType: 'mega' }),
    ]);
    expect(response.statusCode).toBe(400);
  });
});
