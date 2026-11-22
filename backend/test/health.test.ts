import { afterAll, describe, expect, it } from 'vitest';
import { buildApp } from '../src/app/build-app.js';
import { closePool, getPool } from '../src/shared/database-pool.js';

describe('health endpoints', () => {
  it('GET /health answers ok without a database', async () => {
    const app = buildApp({ logLevel: 'silent' });
    const response = await app.inject({ method: 'GET', url: '/health' });
    expect(response.statusCode).toBe(200);
    expect(response.json()).toEqual({ status: 'ok' });
  });

  it('GET /ready is 503 when the database is not configured', async () => {
    const app = buildApp({ logLevel: 'silent' });
    const response = await app.inject({ method: 'GET', url: '/ready' });
    expect(response.statusCode).toBe(503);
    expect(response.json()).toEqual({ status: 'not_ready', database: 'not_configured' });
  });

  it('GET /ready is 503 when the database probe fails', async () => {
    const app = buildApp({
      logLevel: 'silent',
      databaseProbe: () => Promise.reject(new Error('unreachable')),
    });
    const response = await app.inject({ method: 'GET', url: '/ready' });
    expect(response.statusCode).toBe(503);
    expect(response.json()).toEqual({ status: 'not_ready', database: 'down' });
  });
});

const databaseUrl = process.env.DATABASE_URL;

describe.skipIf(databaseUrl === undefined || databaseUrl === '')(
  'readiness with PostgreSQL',
  () => {
    afterAll(closePool);

    it('GET /ready is 200 against a real database', async () => {
      const url = databaseUrl ?? '';
      const app = buildApp({
        logLevel: 'silent',
        databaseProbe: async () => {
          await getPool(url).query('select 1');
        },
      });
      const response = await app.inject({ method: 'GET', url: '/ready' });
      expect(response.statusCode).toBe(200);
      expect(response.json()).toEqual({ status: 'ready', database: 'up' });
    });
  },
);
