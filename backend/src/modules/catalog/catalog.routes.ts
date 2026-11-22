// GET /v1/catalog?since=N: incremental catalog download with ETag (07-BACKEND.md).
import type { FastifyInstance } from 'fastify';
import type { ZodTypeProvider } from 'fastify-type-provider-zod';
import { z } from 'zod';
import type { AppDeps } from '../../app/app-deps.js';
import { authenticate } from '../auth/authenticate.js';
import { CatalogService } from './catalog.service.js';

const querySchema = z.strictObject({
  since: z.coerce.number().int().min(0).max(1_000_000).default(0),
});

export function registerCatalogRoutes(server: FastifyInstance, deps: AppDeps): void {
  const app = server.withTypeProvider<ZodTypeProvider>();
  const catalog = new CatalogService(deps.db);
  const preHandler = authenticate(deps);

  app.get(
    '/v1/catalog',
    { preHandler, schema: { querystring: querySchema } },
    async (req, reply) => {
      const version = await catalog.version();
      const since = Math.min(req.query.since, version);
      const etag = `"catalog-${String(version)}-${String(since)}"`;
      void reply.header('etag', etag).header('cache-control', 'private, no-cache');
      if (req.headers['if-none-match'] === etag) return reply.code(304).send();
      return catalog.build(since, version);
    },
  );
}
