// Builds the Fastify instance with all modules. No listening here, so tests can inject.
import helmet from '@fastify/helmet';
import rateLimit from '@fastify/rate-limit';
import Fastify, { type FastifyInstance } from 'fastify';
import { serializerCompiler, validatorCompiler } from 'fastify-type-provider-zod';
import { sql } from 'kysely';
import { registerAccountRoutes } from '../modules/account/account.routes.js';
import { registerAuthRoutes } from '../modules/auth/auth.routes.js';
import { registerSessionRoutes } from '../modules/auth/session.routes.js';
import { registerHealthRoutes } from '../modules/health/health.routes.js';
import { registerCatalogRoutes } from '../modules/catalog/catalog.routes.js';
import { registerJobRoutes } from '../modules/jobs/jobs.routes.js';
import { registerSyncRoutes } from '../modules/sync/sync.routes.js';
import type { AppDeps } from './app-deps.js';
import { registerErrorHandler } from './error-handler.js';

const BODY_LIMIT_BYTES = 1024 * 1024;
const HSTS_MAX_AGE_SECONDS = 31536000;
// Coarse per-instance IP limit; precise limits live in PostgreSQL (rate-limiter.ts).
const GLOBAL_LIMIT_PER_MINUTE = 300;

// Sensitive fields are never logged (golden rule 20).
const REDACTED_PATHS = [
  'req.headers.authorization',
  'req.headers.cookie',
  '*.password',
  '*.newPassword',
  '*.currentPassword',
  '*.code',
  '*.refreshToken',
  '*.accessToken',
  '*.email',
];

export async function buildApp(deps: AppDeps): Promise<FastifyInstance> {
  const app = Fastify({
    logger: { level: deps.config.logLevel, redact: REDACTED_PATHS },
    bodyLimit: BODY_LIMIT_BYTES,
    trustProxy: true,
  });
  app.setValidatorCompiler(validatorCompiler);
  app.setSerializerCompiler(serializerCompiler);
  registerErrorHandler(app);

  // CORS stays closed: no CORS plugin is registered, the native app does not need it.
  await app.register(helmet, { hsts: { maxAge: HSTS_MAX_AGE_SECONDS, includeSubDomains: true } });
  await app.register(rateLimit, { max: GLOBAL_LIMIT_PER_MINUTE, timeWindow: '1 minute' });

  registerHealthRoutes(app, async () => {
    await sql`select 1`.execute(deps.db);
  });
  registerAuthRoutes(app, deps);
  registerSessionRoutes(app, deps);
  registerAccountRoutes(app, deps);
  registerCatalogRoutes(app, deps);
  registerSyncRoutes(app, deps);
  registerJobRoutes(app, deps);
  return app;
}
