import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

interface Change {
  table: string;
  op: string;
  row: Record<string, unknown>;
}

const T = '2026-11-22T10:00:00Z';

describe('sync of planning tables', () => {
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

  const routine = (id: string, name: string, updatedAt = T) => ({
    table: 'routines',
    op: 'upsert',
    row: { id, name, notes: '', folder: 'Fuerza', colorKey: '', archived: false, updatedAt },
  });

  it('a planned week comes back on pull with numbers and dates intact', async () => {
    const routineId = randomUUID();
    const exerciseId = randomUUID();
    const response = await push(alice.accessToken, [
      routine(routineId, 'Torso A'),
      {
        table: 'routine_exercises',
        op: 'upsert',
        row: {
          id: randomUUID(),
          routineId,
          exerciseId,
          position: 0,
          groupId: 'A',
          targetSets: 3,
          targetRepsMin: 8,
          targetRepsMax: 12,
          targetLoadKg: 62.5,
          targetRpe: 8,
          targetRir: null,
          targetDurationS: null,
          targetDistanceM: null,
          restSeconds: 120,
          notes: '',
          updatedAt: T,
        },
      },
      {
        table: 'scheduled_workouts',
        op: 'upsert',
        row: {
          id: randomUUID(),
          date: '2026-11-24',
          routineId,
          programDayId: null,
          status: 'planned',
          sessionId: null,
          rescheduledFrom: null,
          updatedAt: T,
        },
      },
    ]);
    expect(response.json<{ accepted: string[] }>().accepted).toHaveLength(3);
    const changes = await pull(alice.accessToken);
    const planned = changes.find((c) => c.table === 'routine_exercises')?.row;
    expect(planned).toMatchObject({ targetLoadKg: 62.5, targetRepsMax: 12, groupId: 'A' });
    const scheduled = changes.find((c) => c.table === 'scheduled_workouts')?.row;
    expect(scheduled).toMatchObject({ date: '2026-11-24', routineId });
  });

  it('newest write wins and deletes arrive as tombstones', async () => {
    const id = randomUUID();
    await push(alice.accessToken, [routine(id, 'Nueva', '2026-11-22T12:00:00Z')]);
    await push(alice.accessToken, [routine(id, 'Vieja', '2026-11-22T11:00:00Z')]);
    expect((await pull(alice.accessToken)).find((c) => c.row.id === id)?.row.name).toBe('Nueva');
    await push(alice.accessToken, [
      { table: 'routines', op: 'delete', row: { id, updatedAt: '2026-11-22T13:00:00Z' } },
    ]);
    expect((await pull(alice.accessToken)).find((c) => c.row.id === id)?.op).toBe('delete');
  });

  it("another user's rows can be neither overwritten nor read", async () => {
    const id = randomUUID();
    await push(alice.accessToken, [routine(id, 'De Alice')]);
    const attack = await push(bob.accessToken, [routine(id, 'Robada', '2026-11-23T10:00:00Z')]);
    expect(attack.json()).toMatchObject({ rejected: [{ id, reason: 'forbidden' }] });
    expect((await pull(bob.accessToken)).some((c) => c.row.id === id)).toBe(false);
  });

  it('rejects unknown tables and out-of-range values', async () => {
    const bad = routine(randomUUID(), 'X');
    expect((await push(alice.accessToken, [{ ...bad, table: 'users' }])).statusCode).toBe(400);
    const tooMany = {
      table: 'programs',
      op: 'upsert',
      row: {
        id: randomUUID(),
        name: 'P',
        startDate: null,
        weeksCount: 99,
        isActive: false,
        notes: '',
        updatedAt: T,
      },
    };
    expect((await push(alice.accessToken, [tooMany])).statusCode).toBe(400);
  });
});
