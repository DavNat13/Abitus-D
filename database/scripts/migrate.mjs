// Applies database/migrations/NNN_description.sql in order, once each, inside a transaction.
// Applied migrations are immutable: a changed checksum aborts the run.
// Usage: npm run migrate | npm run migrate:status  (DATABASE_URL from the environment)
import { createHash } from 'node:crypto';
import { readdir, readFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import pg from 'pg';

const MIGRATIONS_DIR = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '..',
  'migrations',
);
const FILE_PATTERN = /^\d{3}_[a-z0-9_]+\.sql$/;
const LOCK_KEY = 72310417;

async function loadMigrations() {
  const names = (await readdir(MIGRATIONS_DIR)).filter((name) => FILE_PATTERN.test(name)).sort();
  return Promise.all(
    names.map(async (name) => {
      const sql = await readFile(path.join(MIGRATIONS_DIR, name), 'utf8');
      return { name, sql, checksum: createHash('sha256').update(sql).digest('hex') };
    }),
  );
}

async function main() {
  const databaseUrl = process.env.DATABASE_URL;
  if (!databaseUrl) throw new Error('DATABASE_URL is not set');
  const statusOnly = process.argv.includes('--status');
  const client = new pg.Client({ connectionString: databaseUrl });
  await client.connect();
  try {
    await client.query('select pg_advisory_lock($1)', [LOCK_KEY]);
    await client.query(`create table if not exists schema_migrations (
      name text primary key,
      checksum text not null,
      applied_at timestamptz not null default now())`);
    const { rows } = await client.query('select name, checksum from schema_migrations');
    const applied = new Map(rows.map((row) => [row.name, row.checksum]));
    for (const migration of await loadMigrations()) {
      const previous = applied.get(migration.name);
      if (previous !== undefined) {
        if (previous !== migration.checksum)
          throw new Error(`${migration.name} changed after being applied`);
        console.log(`applied  ${migration.name}`);
        continue;
      }
      if (statusOnly) {
        console.log(`pending  ${migration.name}`);
        continue;
      }
      await client.query('begin');
      try {
        await client.query(migration.sql);
        await client.query('insert into schema_migrations (name, checksum) values ($1, $2)', [
          migration.name,
          migration.checksum,
        ]);
        await client.query('commit');
        console.log(`applying ${migration.name} ... done`);
      } catch (error) {
        await client.query('rollback');
        throw error;
      }
    }
  } finally {
    await client.query('select pg_advisory_unlock($1)', [LOCK_KEY]).catch(() => undefined);
    await client.end();
  }
}

main().catch((error) => {
  console.error(`migrate: ${error.message}`);
  process.exit(1);
});
