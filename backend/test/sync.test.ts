import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

interface PullBody {
  changes: { op: string; serverSeq: number; row: { id: string; name?: string } }[];
  hasMore: boolean;
  nextCursor: number;
}

function ownExercise(id: string, name: string, updatedAt: string) {
  return {
    table: 'exercises',
    op: 'upsert',
    row: {
      id,
      disciplineId: 3,
      metricType: 'weight_reps',
      movementPattern: 'push',
      difficulty: 2,
      isUnilateral: false,
      usesBodyweight: false,
      defaultRestSeconds: 90,
      locale: 'es',
      name,
      notes: '',
      muscles: [{ muscleGroupId: 1, role: 'primary' }],
      equipmentIds: [3],
      updatedAt,
    },
  };
}

describe('sync of own exercises', () => {
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
  const pull = async (token: string, cursor = 0) =>
    (
      await ctx.app.inject({
        method: 'GET',
        url: `/v1/sync/pull?cursor=${String(cursor)}`,
        headers: { authorization: `Bearer ${token}` },
      })
    ).json<PullBody>();

  it('a pushed exercise comes back on pull, and pushing it again does not duplicate it', async () => {
    const id = randomUUID();
    const change = ownExercise(id, 'Press con banda', '2026-11-20T10:00:00Z');
    expect((await push(alice.accessToken, [change])).json()).toMatchObject({ accepted: [id] });
    expect((await push(alice.accessToken, [change])).statusCode).toBe(200);
    const body = await pull(alice.accessToken);
    expect(body.changes.filter((c) => c.row.id === id)).toHaveLength(1);
  });

  it('the newest write wins and older writes are ignored', async () => {
    const id = randomUUID();
    await push(alice.accessToken, [ownExercise(id, 'Nombre nuevo', '2026-11-21T10:00:00Z')]);
    await push(alice.accessToken, [ownExercise(id, 'Nombre viejo', '2026-11-19T10:00:00Z')]);
    const row = (await pull(alice.accessToken)).changes.find((c) => c.row.id === id);
    expect(row?.row.name).toBe('Nombre nuevo');
  });

  it('a delete arrives as a tombstone after the cursor', async () => {
    const id = randomUUID();
    await push(alice.accessToken, [ownExercise(id, 'Temporal', '2026-11-21T10:00:00Z')]);
    const cursor = (await pull(alice.accessToken)).nextCursor;
    await push(alice.accessToken, [
      { table: 'exercises', op: 'delete', row: { id, updatedAt: '2026-11-21T11:00:00Z' } },
    ]);
    const after = await pull(alice.accessToken, cursor);
    expect(after.changes.map((c) => [c.op, c.row.id])).toEqual([['delete', id]]);
  });

  it("a user can neither see nor change another user's exercises", async () => {
    const id = randomUUID();
    await push(alice.accessToken, [ownExercise(id, 'De Alice', '2026-11-21T10:00:00Z')]);
    const attack = await push(bob.accessToken, [ownExercise(id, 'Robado', '2026-11-22T10:00:00Z')]);
    expect(attack.json()).toMatchObject({ rejected: [{ id, reason: 'forbidden' }] });
    expect((await pull(bob.accessToken)).changes.some((c) => c.row.id === id)).toBe(false);
  });

  it('a catalog exercise id cannot be overwritten', async () => {
    const catalog = await ctx.db
      .selectFrom('exercises')
      .select('id')
      .where('owner_user_id', 'is', null)
      .executeTakeFirstOrThrow();
    const response = await push(alice.accessToken, [
      ownExercise(catalog.id, 'X', '2026-11-22T10:00:00Z'),
    ]);
    expect(response.json()).toMatchObject({ rejected: [{ reason: 'forbidden' }] });
  });

  it('rejects unknown references and oversized batches', async () => {
    const bad = ownExercise(randomUUID(), 'Malo', '2026-11-21T10:00:00Z');
    bad.row.muscles = [{ muscleGroupId: 999, role: 'primary' }];
    expect((await push(alice.accessToken, [bad])).json()).toMatchObject({
      rejected: [{ reason: 'invalid_reference' }],
    });
    const many = Array.from({ length: 201 }, () =>
      ownExercise(randomUUID(), 'N', '2026-11-21T10:00:00Z'),
    );
    expect((await push(alice.accessToken, many)).statusCode).toBe(400);
  });
});
