// Public health endpoints: GET /health (liveness) and GET /ready (database reachable).
import type { FastifyInstance } from 'fastify';
import { checkReadiness, type DatabaseProbe } from './health.service.js';

const HTTP_SERVICE_UNAVAILABLE = 503;

export function registerHealthRoutes(app: FastifyInstance, probe: DatabaseProbe | undefined): void {
  app.get('/health', () => ({ status: 'ok' }));

  app.get('/ready', async (_request, reply) => {
    const result = await checkReadiness(probe);
    if (!result.ready) reply.code(HTTP_SERVICE_UNAVAILABLE);
    return { status: result.ready ? 'ready' : 'not_ready', database: result.database };
  });
}
