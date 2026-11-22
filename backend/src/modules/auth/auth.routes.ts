// Public auth endpoints (07-BACKEND.md). Thin routes: validation here, logic in services.
import type { FastifyInstance } from 'fastify';
import type { ZodTypeProvider } from 'fastify-type-provider-zod';
import type { AppDeps } from '../../app/app-deps.js';
import { clientIpHash } from '../../shared/client-ip.js';
import { withMinimumDuration } from '../../shared/min-duration.js';
import { AUTH_LIMITS, NON_ENUMERATING_RESPONSE_MS } from './auth-limits.js';
import {
  acceptedResponse,
  emailOnlyBody,
  loginBody,
  refreshBody,
  registerBody,
  resetPasswordBody,
  tokensResponse,
  verifyEmailBody,
} from './auth.schemas.js';
import { LoginService } from './login.service.js';
import { PasswordService } from './password.service.js';
import { RegistrationService } from './registration.service.js';
import { SessionService } from './session.service.js';

const ACCEPTED = { status: 'accepted' } as const;

export function registerAuthRoutes(server: FastifyInstance, deps: AppDeps): void {
  const app = server.withTypeProvider<ZodTypeProvider>();
  const registration = new RegistrationService(deps);
  const login = new LoginService(deps);
  const sessions = new SessionService(deps);
  const passwords = new PasswordService(deps);
  const ipPepper = deps.config.secrets.ipHashPepper;
  const accepted = { 202: acceptedResponse };
  const tokens = { 200: tokensResponse };

  app.post(
    '/v1/auth/register',
    { schema: { body: registerBody, response: accepted } },
    async (req, reply) => {
      await withMinimumDuration(NON_ENUMERATING_RESPONSE_MS, () =>
        registration.register(req.body, clientIpHash(req, ipPepper)),
      );
      return reply.code(202).send(ACCEPTED);
    },
  );

  app.post(
    '/v1/auth/verify-email',
    { schema: { body: verifyEmailBody, response: tokens } },
    (req) =>
      registration.verifyEmail(
        req.body.email,
        req.body.code,
        req.body.device,
        clientIpHash(req, ipPepper),
      ),
  );

  app.post(
    '/v1/auth/resend-code',
    { schema: { body: emailOnlyBody, response: accepted } },
    async (req, reply) => {
      await withMinimumDuration(NON_ENUMERATING_RESPONSE_MS, () =>
        registration.resendCode(req.body.email, clientIpHash(req, ipPepper)),
      );
      return reply.code(202).send(ACCEPTED);
    },
  );

  app.post('/v1/auth/login', { schema: { body: loginBody, response: tokens } }, (req) =>
    login.login(req.body.email, req.body.password, req.body.device, clientIpHash(req, ipPepper)),
  );

  app.post('/v1/auth/refresh', { schema: { body: refreshBody, response: tokens } }, async (req) => {
    await deps.limiter.consume(AUTH_LIMITS.refreshPerIp, clientIpHash(req, ipPepper));
    return sessions.refresh(req.body.refreshToken);
  });

  app.post(
    '/v1/auth/password/forgot',
    { schema: { body: emailOnlyBody, response: accepted } },
    async (req, reply) => {
      await withMinimumDuration(NON_ENUMERATING_RESPONSE_MS, () =>
        passwords.forgot(req.body.email, clientIpHash(req, ipPepper)),
      );
      return reply.code(202).send(ACCEPTED);
    },
  );

  app.post(
    '/v1/auth/password/reset',
    { schema: { body: resetPasswordBody, response: accepted } },
    async (req, reply) => {
      await passwords.reset(
        req.body.email,
        req.body.code,
        req.body.newPassword,
        clientIpHash(req, ipPepper),
      );
      return reply.code(202).send(ACCEPTED);
    },
  );
}
