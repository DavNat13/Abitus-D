import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createTestApp, type TestContext } from './support/test-app.js';

describe('health endpoints', () => {
  let ctx: TestContext;
  beforeAll(async () => {
    ctx = await createTestApp();
  });
  afterAll(() => ctx.close());

  it('GET /health answers ok', async () => {
    const response = await ctx.app.inject({ method: 'GET', url: '/health' });
    expect(response.statusCode).toBe(200);
    expect(response.json()).toEqual({ status: 'ok' });
  });

  it('GET /ready checks the database', async () => {
    const response = await ctx.app.inject({ method: 'GET', url: '/ready' });
    expect(response.statusCode).toBe(200);
    expect(response.json()).toEqual({ status: 'ready', database: 'up' });
  });

  it('unknown routes answer problem+json', async () => {
    const response = await ctx.app.inject({ method: 'GET', url: '/nope' });
    expect(response.statusCode).toBe(404);
    expect(response.headers['content-type']).toContain('application/problem+json');
    expect(response.json()).toEqual({ status: 404, code: 'NOT_FOUND' });
  });

  it('sends security headers', async () => {
    const response = await ctx.app.inject({ method: 'GET', url: '/health' });
    expect(response.headers['strict-transport-security']).toContain('max-age=31536000');
    expect(response.headers['x-content-type-options']).toBe('nosniff');
    expect(response.headers['access-control-allow-origin']).toBeUndefined();
  });
});
