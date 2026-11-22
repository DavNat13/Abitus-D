import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createVerifiedUser, type Tokens } from './support/auth-flow.js';
import { createTestApp, type TestContext, uniqueEmail } from './support/test-app.js';

interface CatalogBody {
  version: number;
  disciplines: { slug: string; names: Record<string, string> }[];
  muscleGroups: unknown[];
  exercises: {
    slug: string;
    source: string;
    license: string;
    translations: Record<string, { name: string; execution: string[] }>;
    muscles: { role: string }[];
  }[];
}

describe('catalog', () => {
  let ctx: TestContext;
  let user: Tokens;
  beforeAll(async () => {
    ctx = await createTestApp();
    user = await createVerifiedUser(ctx, uniqueEmail());
  });
  afterAll(() => ctx.close());

  const get = (url: string, headers: Record<string, string> = {}) =>
    ctx.app.inject({
      method: 'GET',
      url,
      headers: { authorization: `Bearer ${user.accessToken}`, ...headers },
    });

  it('requires a session', async () => {
    const response = await ctx.app.inject({ method: 'GET', url: '/v1/catalog' });
    expect(response.statusCode).toBe(401);
  });

  it('returns the full catalog in Spanish and English with source and license', async () => {
    const response = await get('/v1/catalog');
    expect(response.statusCode).toBe(200);
    const body = response.json<CatalogBody>();
    expect(body.version).toBeGreaterThan(0);
    expect(body.disciplines).toHaveLength(12);
    expect(body.muscleGroups).toHaveLength(21);
    expect(body.exercises.length).toBeGreaterThan(200);
    for (const e of body.exercises) {
      expect(e.source.length).toBeGreaterThan(0);
      expect(e.license.length).toBeGreaterThan(0);
      expect(e.translations.es?.name.length).toBeGreaterThan(0);
      expect(e.translations.en?.name.length).toBeGreaterThan(0);
      expect(e.muscles.some((m) => m.role === 'primary')).toBe(true);
    }
    const squat = body.exercises.find((e) => e.slug === 'barbell_back_squat');
    expect(squat?.translations.es?.name).toBe('Sentadilla trasera con barra');
  });

  it('answers 304 when the ETag matches and nothing changed', async () => {
    const first = await get('/v1/catalog');
    const etag = first.headers.etag;
    expect(typeof etag).toBe('string');
    const second = await get('/v1/catalog', { 'if-none-match': String(etag) });
    expect(second.statusCode).toBe(304);
  });

  it('returns no exercises when the client is already at the current version', async () => {
    const { version } = (await get('/v1/catalog')).json<CatalogBody>();
    const response = await get(`/v1/catalog?since=${String(version)}`);
    expect(response.json<CatalogBody>().exercises).toHaveLength(0);
  });

  it('rejects unknown query parameters', async () => {
    expect((await get('/v1/catalog?since=0&drop=1')).statusCode).toBe(400);
  });
});
