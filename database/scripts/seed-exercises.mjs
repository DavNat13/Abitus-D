// Upserts catalog exercises. Only changed records are written; they all take the new
// catalog version. Exercises removed from the seeds are soft deleted, never erased.
import { contentHash, uuidV5 } from './seed-validation.mjs';

const CONTRIBUTION = { primary: 1, secondary: 0.5 };

export async function loadExercises(client, exercises, refs, version) {
  const existing = new Map(
    (
      await client.query(
        'select slug, content_hash, deleted_at from exercises where owner_user_id is null',
      )
    ).rows.map((r) => [r.slug, r]),
  );
  let changed = 0;
  for (const e of exercises) {
    const hash = contentHash(e);
    const current = existing.get(e.slug);
    if (current && current.content_hash === hash && current.deleted_at === null) continue;
    await writeExercise(client, e, refs, version, hash);
    changed += 1;
  }
  const seedSlugs = new Set(exercises.map((e) => e.slug));
  for (const slug of existing.keys()) {
    if (seedSlugs.has(slug) || existing.get(slug).deleted_at !== null) continue;
    await client.query(
      'update exercises set deleted_at = now(), updated_at = now(), catalog_version = $2 where slug = $1 and owner_user_id is null',
      [slug, version],
    );
    changed += 1;
  }
  await writeRelations(client, exercises);
  return changed;
}

async function writeExercise(client, e, refs, version, hash) {
  const id = uuidV5(`exercise:${e.slug}`);
  await client.query(
    `insert into exercises (id, slug, discipline_id, metric_type, movement_pattern, difficulty, is_unilateral,
       uses_bodyweight, default_rest_seconds, source, license, catalog_version, content_hash)
     values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13)
     on conflict (id) do update set discipline_id = $3, metric_type = $4, movement_pattern = $5, difficulty = $6,
       is_unilateral = $7, uses_bodyweight = $8, default_rest_seconds = $9, source = $10, license = $11,
       catalog_version = $12, content_hash = $13, updated_at = now(), deleted_at = null`,
    [
      id,
      e.slug,
      refs.disciplines.get(e.discipline),
      e.metric,
      e.pattern,
      e.difficulty,
      e.unilateral,
      e.bodyweight,
      e.rest,
      e.source,
      e.license,
      version,
      hash,
    ],
  );
  await client.query('delete from exercise_translations where exercise_id = $1', [id]);
  for (const locale of ['en', 'es']) {
    const t = e[locale];
    if (!t) continue;
    await client.query(
      `insert into exercise_translations (exercise_id, locale, name, summary, setup, execution, cues,
         common_mistakes, safety_notes) values ($1, $2, $3, $4, $5, $6, $7, $8, $9)`,
      [id, locale, t.name, t.summary, t.setup, t.execution, t.cues, t.mistakes, t.safety],
    );
  }
  await client.query('delete from exercise_muscles where exercise_id = $1', [id]);
  for (const role of ['primary', 'secondary']) {
    for (const slug of e.muscles[role]) {
      await client.query(
        'insert into exercise_muscles (exercise_id, muscle_group_id, role, contribution) values ($1, $2, $3, $4)',
        [id, refs.muscles.get(slug), role, CONTRIBUTION[role]],
      );
    }
  }
  await client.query('delete from exercise_equipment where exercise_id = $1', [id]);
  for (const slug of e.equipment) {
    await client.query(
      'insert into exercise_equipment (exercise_id, equipment_id) values ($1, $2)',
      [id, refs.equipment.get(slug)],
    );
  }
}

/** Relations are rebuilt whole: cheap, and they may point to exercises of any file. */
async function writeRelations(client, exercises) {
  await client.query(
    'delete from exercise_relations where exercise_id in (select id from exercises where owner_user_id is null)',
  );
  const slugs = new Set(exercises.map((e) => e.slug));
  for (const e of exercises) {
    for (const r of e.relations) {
      if (!slugs.has(r.slug)) throw new Error(`${e.slug}: relation to unknown exercise ${r.slug}`);
      await client.query(
        'insert into exercise_relations (exercise_id, related_exercise_id, relation_type) values ($1, $2, $3)',
        [uuidV5(`exercise:${e.slug}`), uuidV5(`exercise:${r.slug}`), r.type],
      );
    }
  }
}
