// Builds the Fastify instance with all modules. No listening here, so tests can inject.
import Fastify, { type FastifyInstance } from 'fastify';
import { registerHealthRoutes } from '../modules/health/health.routes.js';
import type { DatabaseProbe } from '../modules/health/health.service.js';

const BODY_LIMIT_BYTES = 1024 * 1024;

// Sensitive fields are never logged (golden rule 20).
const REDACTED_PATHS = [
  'req.headers.authorization',
  'req.headers.cookie',
  '*.password',
  '*.code',
  '*.token',
  '*.refreshToken',
  '*.email',
];

export interface BuildAppOptions {
  readonly logLevel?: string;
  readonly databaseProbe?: DatabaseProbe;
}

export function buildApp(options: BuildAppOptions = {}): FastifyInstance {
  const app = Fastify({
    logger: { level: options.logLevel ?? 'info', redact: REDACTED_PATHS },
    bodyLimit: BODY_LIMIT_BYTES,
  });
  registerHealthRoutes(app, options.databaseProbe);
  return app;
}
