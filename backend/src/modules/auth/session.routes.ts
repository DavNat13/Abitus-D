// Authenticated session endpoints: logout, logout-all and password change.
import type { FastifyInstance } from 'fastify';
import type { ZodTypeProvider } from 'fastify-type-provider-zod';
import type { AppDeps } from '../../app/app-deps.js';
import { authenticate, requireAuth } from './authenticate.js';
import { acceptedResponse, changePasswordBody } from './auth.schemas.js';
import { PasswordService } from './password.service.js';
import { SessionService } from './session.service.js';

const ACCEPTED = { status: 'accepted' } as const;

export function registerSessionRoutes(server: FastifyInstance, deps: AppDeps): void {
  const app = server.withTypeProvider<ZodTypeProvider>();
  const sessions = new SessionService(deps);
  const passwords = new PasswordService(deps);
  const preHandler = authenticate(deps);
  const accepted = { 202: acceptedResponse };

  app.post(
    '/v1/auth/logout',
    { preHandler, schema: { response: accepted } },
    async (req, reply) => {
      const auth = requireAuth(req);
      await sessions.logout(auth.userId, auth.deviceId);
      return reply.code(202).send(ACCEPTED);
    },
  );

  app.post(
    '/v1/auth/logout-all',
    { preHandler, schema: { response: accepted } },
    async (req, reply) => {
      await sessions.logoutAll(requireAuth(req).userId);
      return reply.code(202).send(ACCEPTED);
    },
  );

  app.post(
    '/v1/auth/password/change',
    { preHandler, schema: { body: changePasswordBody, response: accepted } },
    async (req, reply) => {
      const auth = requireAuth(req);
      await passwords.change(
        auth.userId,
        auth.deviceId,
        req.body.currentPassword,
        req.body.newPassword,
      );
      return reply.code(202).send(ACCEPTED);
    },
  );
}
