// Sync endpoints: POST /v1/sync/push and GET /v1/sync/pull. Require a session.
import type { FastifyInstance } from 'fastify';
import type { ZodTypeProvider } from 'fastify-type-provider-zod';
import type { AppDeps } from '../../app/app-deps.js';
import { authenticate, requireAuth } from '../auth/authenticate.js';
import { pullQuery, pushBody } from './sync.schemas.js';
import { SyncService } from './sync.service.js';

const SYNC_LIMIT = { action: 'sync_user', max: 120, windowSeconds: 60 } as const;

export function registerSyncRoutes(server: FastifyInstance, deps: AppDeps): void {
  const app = server.withTypeProvider<ZodTypeProvider>();
  const sync = new SyncService(deps.db);
  const preHandler = authenticate(deps);

  app.post('/v1/sync/push', { preHandler, schema: { body: pushBody } }, async (req) => {
    const userId = requireAuth(req).userId;
    await deps.limiter.consume(SYNC_LIMIT, userId);
    return sync.push(userId, req.body.changes);
  });

  app.get('/v1/sync/pull', { preHandler, schema: { querystring: pullQuery } }, async (req) => {
    const userId = requireAuth(req).userId;
    await deps.limiter.consume(SYNC_LIMIT, userId);
    return sync.pull(userId, req.query.cursor, req.query.limit);
  });
}
