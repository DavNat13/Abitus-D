import { randomUUID } from 'node:crypto';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

interface Change {
  table: string;
  row: Record<string, unknown>;
}

const T = '2026-11-24T10:00:00Z';

describe('sync of advanced planning (blocks, substitutions, targets, planned time)', () => {
  let ctx: TestContext;
  let user: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    user = await createVerifiedUser(ctx, uniqueEmail('p'));
  });
  afterAll(() => ctx.close());

  const push = (changes: object[]) =>
    ctx.app.inject({
      method: 'POST',
      url: '/v1/sync/push',
      headers: { authorization: `Bearer ${user.accessToken}` },
      payload: { changes },
    });
  const pull = async () =>
    (
      await ctx.app.inject({
        method: 'GET',
        url: '/v1/sync/pull?cursor=0',
        headers: { authorization: `Bearer ${user.accessToken}` },
      })
    ).json<{ changes: Change[] }>().changes;

  it('a mesocycle with a deload block and its extras comes back intact', async () => {
    const programId = randomUUID();
    const blockId = randomUUID();
    const deloadId = randomUUID();
    const scheduledId = randomUUID();
    const block = (
      id: string,
      position: number,
      phase: string,
      weeks: number,
      step: number | null,
    ) => ({
      table: 'program_blocks',
      op: 'upsert',
      row: {
        id,
        programId,
        position,
        name: 'Bloque',
        goal: 'strength',
        phase,
        weeks,
        progression: 'double',
        step,
        updatedAt: T,
      },
    });
    const response = await push([
      block(blockId, 0, 'accumulation', 3, 2.5),
      block(deloadId, 1, 'deload', 1, null),
      {
        table: 'block_substitutions',
        op: 'upsert',
        row: {
          id: randomUUID(),
          blockId,
          fromExerciseId: randomUUID(),
          toExerciseId: randomUUID(),
          updatedAt: T,
        },
      },
      {
        table: 'muscle_targets',
        op: 'upsert',
        row: { id: randomUUID(), muscleGroupId: 1, minSets: 10, maxSets: 20, updatedAt: T },
      },
      {
        table: 'scheduled_workouts',
        op: 'upsert',
        row: {
          id: scheduledId,
          date: '2026-11-25',
          routineId: randomUUID(),
          programDayId: null,
          status: 'planned',
          sessionId: null,
          rescheduledFrom: null,
          plannedTime: '07:30',
          updatedAt: T,
        },
      },
    ]);
    expect(response.statusCode).toBe(200);
    const changes = await pull();
    expect(changes.find((c) => c.row.id === blockId)?.row).toMatchObject({
      step: 2.5,
      weeks: 3,
      phase: 'accumulation',
    });
    expect(changes.find((c) => c.row.id === deloadId)?.row).toMatchObject({
      step: null,
      phase: 'deload',
    });
    expect(changes.find((c) => c.row.id === scheduledId)?.row).toMatchObject({
      plannedTime: '07:30',
    });
    expect(changes.some((c) => c.table === 'muscle_targets' && c.row.minSets === 10)).toBe(true);
  });

  it('rejects a target whose minimum is above its maximum and an invalid time', async () => {
    const target = { id: randomUUID(), muscleGroupId: 2, minSets: 15, maxSets: 5, updatedAt: T };
    expect((await push([{ table: 'muscle_targets', op: 'upsert', row: target }])).statusCode).toBe(
      400,
    );
    const scheduled = {
      id: randomUUID(),
      date: '2026-11-25',
      routineId: null,
      programDayId: null,
      status: 'planned',
      sessionId: null,
      rescheduledFrom: null,
      plannedTime: '25:00',
      updatedAt: T,
    };
    expect(
      (await push([{ table: 'scheduled_workouts', op: 'upsert', row: scheduled }])).statusCode,
    ).toBe(400);
  });
});
