// Builds the catalog payload: reference tables in full plus exercises changed since a version.
// Every locale is included so the app can switch language without a new download.
import type { Db } from '../../shared/database.js';
import { CatalogRepository } from './catalog.repository.js';

type Texts = Record<string, Record<string, unknown>>;

function groupBy<T>(rows: T[], key: (row: T) => string): Map<string, T[]> {
  const map = new Map<string, T[]>();
  for (const row of rows) map.set(key(row), [...(map.get(key(row)) ?? []), row]);
  return map;
}

function names(rows: { locale: string; name: string }[]): Record<string, string> {
  return Object.fromEntries(rows.map((r) => [r.locale, r.name]));
}

export class CatalogService {
  private readonly repo: CatalogRepository;

  constructor(db: Db) {
    this.repo = new CatalogRepository(db);
  }

  version(): Promise<number> {
    return this.repo.version();
  }

  async build(since: number, version: number) {
    const ref = await this.repo.reference();
    const exercises = await this.repo.exercisesSince(since);
    const details = await this.repo.details(exercises.map((e) => e.id));
    const texts = groupBy(details.translations, (t) => t.exercise_id);
    const muscles = groupBy(details.muscles, (m) => m.exercise_id);
    const equipment = groupBy(details.equipment, (q) => q.exercise_id);
    const relations = groupBy(details.relations, (r) => r.exercise_id);
    const disciplineNames = groupBy(ref.disciplineNames, (n) => String(n.discipline_id));
    const muscleNames = groupBy(ref.muscleNames, (n) => String(n.muscle_group_id));
    const equipmentNames = groupBy(ref.equipmentNames, (n) => String(n.equipment_id));
    return {
      version,
      since,
      disciplines: ref.disciplines.map((d) => ({
        id: d.id,
        slug: d.slug,
        sortOrder: d.sort_order,
        iconKey: d.icon_key,
        names: names(disciplineNames.get(String(d.id)) ?? []),
        descriptions: Object.fromEntries(
          (disciplineNames.get(String(d.id)) ?? []).map((n) => [n.locale, n.description]),
        ),
      })),
      muscleGroups: ref.muscles.map((m) => ({
        id: m.id,
        slug: m.slug,
        bodyRegion: m.body_region,
        view: m.view,
        meshKey: m.mesh_key,
        svgKey: m.svg_key,
        sortOrder: m.sort_order,
        names: names(muscleNames.get(String(m.id)) ?? []),
      })),
      equipment: ref.equipment.map((q) => ({
        id: q.id,
        slug: q.slug,
        names: names(equipmentNames.get(String(q.id)) ?? []),
      })),
      exercises: exercises.map((e) => {
        const translations: Texts = {};
        for (const t of texts.get(e.id) ?? []) {
          translations[t.locale] = {
            name: t.name,
            summary: t.summary,
            setup: t.setup,
            execution: t.execution,
            cues: t.cues,
            commonMistakes: t.common_mistakes,
            safetyNotes: t.safety_notes,
          };
        }
        return {
          id: e.id,
          slug: e.slug,
          disciplineId: e.discipline_id,
          metricType: e.metric_type,
          movementPattern: e.movement_pattern,
          difficulty: e.difficulty,
          isUnilateral: e.is_unilateral,
          usesBodyweight: e.uses_bodyweight,
          defaultRestSeconds: e.default_rest_seconds,
          source: e.source,
          license: e.license,
          deleted: e.deleted_at !== null,
          translations,
          muscles: (muscles.get(e.id) ?? []).map((m) => ({
            muscleGroupId: m.muscle_group_id,
            role: m.role,
            contribution: Number(m.contribution),
          })),
          equipmentIds: (equipment.get(e.id) ?? []).map((q) => q.equipment_id),
          relations: (relations.get(e.id) ?? []).map((r) => ({
            exerciseId: r.related_exercise_id,
            type: r.relation_type,
          })),
        };
      }),
    };
  }
}
