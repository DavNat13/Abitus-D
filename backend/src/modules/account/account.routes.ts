// Account endpoints of Phase 1: profile, devices and consents. All require a session.
import type { FastifyInstance } from 'fastify';
import type { ZodTypeProvider } from 'fastify-type-provider-zod';
import { z } from 'zod';
import type { AppDeps } from '../../app/app-deps.js';
import { writeAudit } from '../../shared/audit-log.js';
import { AppError } from '../../shared/errors.js';
import { authenticate, requireAuth } from '../auth/authenticate.js';
import { SessionRepository } from '../auth/session.repository.js';
import { AccountRepository } from './account.repository.js';

const profileResponse = z.object({
  id: z.uuid(),
  email: z.string(),
  displayName: z.string().nullable(),
  locale: z.string(),
});

const deviceResponse = z.object({
  id: z.uuid(),
  name: z.string(),
  platform: z.string(),
  appVersion: z.string(),
  firstSeenAt: z.string(),
  lastSeenAt: z.string(),
  current: z.boolean(),
});

const consentBody = z.strictObject({
  document: z.enum(['privacy', 'terms']),
  version: z.string().min(1).max(20),
});

export function registerAccountRoutes(server: FastifyInstance, deps: AppDeps): void {
  const app = server.withTypeProvider<ZodTypeProvider>();
  const accounts = new AccountRepository(deps.db);
  const sessions = new SessionRepository(deps.db);
  const preHandler = authenticate(deps);

  app.get('/v1/me', { preHandler, schema: { response: { 200: profileResponse } } }, async (req) => {
    const row = await accounts.profile(requireAuth(req).userId);
    if (row === undefined) throw new AppError('AUTH_UNAUTHORIZED');
    return { id: row.id, email: row.email, displayName: row.display_name, locale: row.locale };
  });

  app.get(
    '/v1/me/devices',
    { preHandler, schema: { response: { 200: z.array(deviceResponse) } } },
    async (req) => {
      const auth = requireAuth(req);
      const devices = await accounts.activeDevices(auth.userId);
      return devices.map((device) => ({ ...device, current: device.id === auth.deviceId }));
    },
  );

  app.delete(
    '/v1/me/devices/:id',
    { preHandler, schema: { params: z.strictObject({ id: z.uuid() }) } },
    async (req, reply) => {
      const auth = requireAuth(req);
      if (!(await accounts.deviceBelongsTo(auth.userId, req.params.id)))
        throw new AppError('NOT_FOUND');
      await sessions.revokeDevices(auth.userId, { only: req.params.id });
      await writeAudit(deps.db, {
        userId: auth.userId,
        event: 'device_revoked',
        deviceId: req.params.id,
      });
      return reply.code(204).send();
    },
  );

  app.post('/v1/me/consents', { preHandler, schema: { body: consentBody } }, async (req, reply) => {
    await accounts.addConsent(requireAuth(req).userId, req.body.document, req.body.version);
    return reply.code(204).send();
  });
}
