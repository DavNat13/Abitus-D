// Scheduled jobs (14-INFRAESTRUCTURA.md), invoked by an external scheduler with
// "Authorization: Bearer <JOBS_SECRET>". Not public; idempotent.
import type { FastifyInstance, FastifyRequest } from 'fastify';
import { sql } from 'kysely';
import type { AppDeps } from '../../app/app-deps.js';
import { hashesEqual } from '../../shared/crypto/secure-hash.js';
import { AppError } from '../../shared/errors.js';

const MAIL_BATCH = 50;

function assertJobSecret(request: FastifyRequest, secret: string): void {
  const header = request.headers.authorization ?? '';
  if (!hashesEqual(header, `Bearer ${secret}`)) throw new AppError('AUTH_UNAUTHORIZED');
}

export function registerJobRoutes(app: FastifyInstance, deps: AppDeps): void {
  const secret = deps.config.secrets.jobs;

  app.post('/internal/jobs/process-mail-outbox', async (req) => {
    assertJobSecret(req, secret);
    return { sent: await deps.outbox.processPending(MAIL_BATCH) };
  });

  app.post('/internal/jobs/cleanup', async (req) => {
    assertJobSecret(req, secret);
    const db = deps.db;
    await db
      .deleteFrom('verification_codes')
      .where('expires_at', '<', sql<Date>`now() - interval '1 day'`)
      .execute();
    await db
      .deleteFrom('rate_limit_events')
      .where('created_at', '<', sql<Date>`now() - interval '1 day'`)
      .execute();
    await db
      .deleteFrom('refresh_tokens')
      .where('expires_at', '<', sql<Date>`now() - interval '1 day'`)
      .execute();
    await db
      .updateTable('email_outbox')
      .set({ payload: '{}' })
      .where('template', 'in', ['verification_code', 'reset_code'])
      .where('created_at', '<', sql<Date>`now() - interval '15 minutes'`)
      .execute();
    return { status: 'ok' };
  });
}
