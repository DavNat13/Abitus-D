// Recreates a clean test database and applies every migration from database/ (17: migrations
// on a clean database). Needs the local PostgreSQL from docker compose.
import { execFileSync } from 'node:child_process';
import path from 'node:path';
import pg from 'pg';

const TEST_DB = 'abitusd_test';

export function testDatabaseUrl(): string {
  const base = process.env.DATABASE_URL;
  if (base === undefined || base === '')
    throw new Error('DATABASE_URL is required for backend tests');
  const url = new URL(base);
  url.pathname = `/${TEST_DB}`;
  return url.toString();
}

export default async function setup(): Promise<void> {
  const admin = new pg.Client({ connectionString: process.env.DATABASE_URL });
  await admin.connect();
  await admin.query(`drop database if exists ${TEST_DB} with (force)`);
  await admin.query(`create database ${TEST_DB}`);
  await admin.end();
  const scripts = path.resolve(import.meta.dirname, '..', '..', '..', 'database', 'scripts');
  for (const script of ['migrate.mjs', 'seed.mjs']) {
    execFileSync(process.execPath, [path.join(scripts, script)], {
      env: { ...process.env, DATABASE_URL: testDatabaseUrl() },
      stdio: 'ignore',
    });
  }
}
