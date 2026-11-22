// Idempotent catalog seed loader (08c-DATABASE-SEMILLAS.md). Validates every record,
// writes only what changed and raises catalog_version once per run with changes.
// Usage: npm run seed   (DATABASE_URL from the environment; run after npm run migrate)
import pg from 'pg';
import { readAllExercises } from './seed-files.mjs';
import { loadExercises } from './seed-exercises.mjs';
import { loadReference } from './seed-reference.mjs';
import { validateExercise } from './seed-validation.mjs';

async function main() {
  const databaseUrl = process.env.DATABASE_URL;
  if (!databaseUrl) throw new Error('DATABASE_URL is not set');
  const client = new pg.Client({ connectionString: databaseUrl });
  await client.connect();
  try {
    await client.query('begin');
    await client.query('select version from catalog_state where id = 1 for update');
    const refs = await loadReference(client);
    const exercises = await readAllExercises();
    const slugs = new Set();
    const refSets = {
      disciplines: new Set(refs.disciplines.keys()),
      muscles: new Set(refs.muscles.keys()),
      equipment: new Set(refs.equipment.keys()),
    };
    for (const e of exercises) {
      validateExercise(e, refSets);
      if (slugs.has(e.slug)) throw new Error(`duplicate slug: ${e.slug}`);
      slugs.add(e.slug);
    }
    const { rows } = await client.query('select version from catalog_state where id = 1');
    const next = rows[0].version + 1;
    const changed = await loadExercises(client, exercises, refs, next);
    if (changed > 0) {
      await client.query('update catalog_state set version = $1, updated_at = now() where id = 1', [
        next,
      ]);
    }
    await client.query('commit');
    const translated = exercises.filter((e) => e.es !== null).length;
    console.log(
      `seed: ${String(exercises.length)} exercises (${String(translated)} with Spanish), ` +
        `${String(changed)} changed, catalog version ${String(changed > 0 ? next : next - 1)}`,
    );
  } catch (error) {
    await client.query('rollback');
    throw error;
  } finally {
    await client.end();
  }
}

main().catch((error) => {
  console.error(`seed: ${error.message}`);
  process.exit(1);
});
