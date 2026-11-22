// Persistence of exercises owned by users. Every query is filtered by the owner id.
import type { Db } from '../../shared/database.js';
import type { OwnExerciseRow } from './sync.schemas.js';

const CONTRIBUTION = { primary: 1, secondary: 0.5 } as const;

export interface StoredOwnership {
  readonly ownerUserId: string | null;
  readonly updatedAt: Date;
}

export class ExerciseSyncRepository {
  constructor(private readonly db: Db) {}

  async ownership(id: string): Promise<StoredOwnership | undefined> {
    const row = await this.db
      .selectFrom('exercises')
      .select(['owner_user_id', 'updated_at'])
      .where('id', '=', id)
      .executeTakeFirst();
    return row && { ownerUserId: row.owner_user_id, updatedAt: row.updated_at };
  }

  async referenceIds() {
    const [disciplines, muscles, equipment] = await Promise.all([
      this.db.selectFrom('disciplines').select('id').execute(),
      this.db.selectFrom('muscle_groups').select('id').execute(),
      this.db.selectFrom('equipment').select('id').execute(),
    ]);
    return {
      disciplines: new Set(disciplines.map((r) => r.id)),
      muscles: new Set(muscles.map((r) => r.id)),
      equipment: new Set(equipment.map((r) => r.id)),
    };
  }

  async upsert(userId: string, row: OwnExerciseRow, updatedAt: Date): Promise<void> {
    await this.db.transaction().execute(async (trx) => {
      const values = {
        discipline_id: row.disciplineId,
        metric_type: row.metricType,
        movement_pattern: row.movementPattern,
        difficulty: row.difficulty,
        is_unilateral: row.isUnilateral,
        uses_bodyweight: row.usesBodyweight,
        default_rest_seconds: row.defaultRestSeconds,
        updated_at: updatedAt,
        deleted_at: null,
      };
      await trx
        .insertInto('exercises')
        .values({ id: row.id, owner_user_id: userId, ...values })
        .onConflict((oc) =>
          oc.column('id').doUpdateSet(values).where('exercises.owner_user_id', '=', userId),
        )
        .execute();
      await trx.deleteFrom('exercise_translations').where('exercise_id', '=', row.id).execute();
      await trx
        .insertInto('exercise_translations')
        .values({ exercise_id: row.id, locale: row.locale, name: row.name, summary: row.notes })
        .execute();
      await trx.deleteFrom('exercise_muscles').where('exercise_id', '=', row.id).execute();
      if (row.muscles.length > 0) {
        await trx
          .insertInto('exercise_muscles')
          .values(
            row.muscles.map((m) => ({
              exercise_id: row.id,
              muscle_group_id: m.muscleGroupId,
              role: m.role,
              contribution: CONTRIBUTION[m.role],
            })),
          )
          .execute();
      }
      await trx.deleteFrom('exercise_equipment').where('exercise_id', '=', row.id).execute();
      if (row.equipmentIds.length > 0) {
        await trx
          .insertInto('exercise_equipment')
          .values(
            row.equipmentIds.map((equipmentId) => ({
              exercise_id: row.id,
              equipment_id: equipmentId,
            })),
          )
          .execute();
      }
    });
  }

  async softDelete(userId: string, id: string, updatedAt: Date): Promise<void> {
    await this.db
      .updateTable('exercises')
      .set({ deleted_at: updatedAt, updated_at: updatedAt })
      .where('id', '=', id)
      .where('owner_user_id', '=', userId)
      .execute();
  }

  /** Own exercises changed after the cursor, oldest first, including tombstones. */
  async changesAfter(userId: string, cursor: number, limit: number) {
    const rows = await this.db
      .selectFrom('exercises')
      .selectAll()
      .where('owner_user_id', '=', userId)
      .where('server_seq', '>', String(cursor))
      .orderBy('server_seq')
      .limit(limit)
      .execute();
    const ids = rows.map((r) => r.id);
    if (ids.length === 0) return { rows, translations: [], muscles: [], equipment: [] };
    const [translations, muscles, equipment] = await Promise.all([
      this.db
        .selectFrom('exercise_translations')
        .selectAll()
        .where('exercise_id', 'in', ids)
        .execute(),
      this.db.selectFrom('exercise_muscles').selectAll().where('exercise_id', 'in', ids).execute(),
      this.db
        .selectFrom('exercise_equipment')
        .selectAll()
        .where('exercise_id', 'in', ids)
        .execute(),
    ]);
    return { rows, translations, muscles, equipment };
  }
}
