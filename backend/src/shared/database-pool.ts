// Lazily created PostgreSQL pool. Small on purpose: serverless instances are short-lived.
import pg from 'pg';

const POOL_MAX_CONNECTIONS = 3;
const CONNECT_TIMEOUT_MS = 5000;

let pool: pg.Pool | undefined;

export function getPool(databaseUrl: string): pg.Pool {
  pool ??= new pg.Pool({
    connectionString: databaseUrl,
    max: POOL_MAX_CONNECTIONS,
    connectionTimeoutMillis: CONNECT_TIMEOUT_MS,
  });
  return pool;
}

export async function closePool(): Promise<void> {
  if (pool === undefined) return;
  const current = pool;
  pool = undefined;
  await current.end();
}
