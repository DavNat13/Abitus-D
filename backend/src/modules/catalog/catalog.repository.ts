// Catalog reads. One query per table and grouping in memory (no N+1 queries).
import type { Db } from '../../shared/database.js';

export class CatalogRepository {
  constructor(private readonly db: Db) {}

  async version(): Promise<number> {
    const row = await this.db
      .selectFrom('catalog_state')
      .select('version')
      .where('id', '=', 1)
      .executeTakeFirstOrThrow();
    return row.version;
  }

  async reference() {
    const [disciplines, disciplineNames, muscles, muscleNames, equipment, equipmentNames] =
      await Promise.all([
        this.db.selectFrom('disciplines').selectAll().orderBy('sort_order').execute(),
        this.db.selectFrom('discipline_translations').selectAll().execute(),
        this.db.selectFrom('muscle_groups').selectAll().orderBy('sort_order').execute(),
        this.db.selectFrom('muscle_group_translations').selectAll().execute(),
        this.db.selectFrom('equipment').selectAll().orderBy('id').execute(),
        this.db.selectFrom('equipment_translations').selectAll().execute(),
      ]);
    return { disciplines, disciplineNames, muscles, muscleNames, equipment, equipmentNames };
  }

  /** Catalog exercises changed after `since`; with since = 0, only the live ones. */
  async exercisesSince(since: number) {
    let query = this.db
      .selectFrom('exercises')
      .select([
        'id',
        'slug',
        'discipline_id',
        'metric_type',
        'movement_pattern',
        'difficulty',
        'is_unilateral',
        'uses_bodyweight',
        'default_rest_seconds',
        'source',
        'license',
        'deleted_at',
      ])
      .where('owner_user_id', 'is', null)
      .where('catalog_version', '>', since)
      .orderBy('slug');
    if (since === 0) query = query.where('deleted_at', 'is', null);
    return query.execute();
  }

  async details(ids: string[]) {
    if (ids.length === 0) return { translations: [], muscles: [], equipment: [], relations: [] };
    const [translations, muscles, equipment, relations] = await Promise.all([
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
      this.db
        .selectFrom('exercise_relations')
        .selectAll()
        .where('exercise_id', 'in', ids)
        .execute(),
    ]);
    return { translations, muscles, equipment, relations };
  }
}
