// Kysely over a small pg pool. Every query is parameterized by Kysely (golden rule 18).
import { Kysely, PostgresDialect } from 'kysely';
import pg from 'pg';
import type { Database } from './database-types.js';

// DATE columns stay as YYYY-MM-DD strings: no time zone shifts for calendar days.

pg.types.setTypeParser(pg.types.builtins.DATE, (value) => value);

const POOL_MAX_CONNECTIONS = 3;
const CONNECT_TIMEOUT_MS = 5000;
const STATEMENT_TIMEOUT_MS = 10000;

export type Db = Kysely<Database>;

export function createDatabase(databaseUrl: string): Db {
  const pool = new pg.Pool({
    connectionString: databaseUrl,
    max: POOL_MAX_CONNECTIONS,
    connectionTimeoutMillis: CONNECT_TIMEOUT_MS,
    statement_timeout: STATEMENT_TIMEOUT_MS,
  });
  return new Kysely<Database>({ dialect: new PostgresDialect({ pool }) });
}
