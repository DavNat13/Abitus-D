// preHandler that requires a valid Bearer access token of a device that still has a
// session, and exposes request.auth. Revoked devices lose access immediately.
import type { FastifyRequest } from 'fastify';
import type { AppDeps } from '../../app/app-deps.js';
import { AppError } from '../../shared/errors.js';
import { type AccessClaims, verifyAccessToken } from './access-token.js';
import { DeviceRepository } from './device.repository.js';

declare module 'fastify' {
  interface FastifyRequest {
    auth?: AccessClaims;
  }
}

const BEARER_PREFIX = 'Bearer ';

export function authenticate(deps: AppDeps) {
  const devices = new DeviceRepository(deps.db);
  const secret = deps.config.secrets.jwtAccess;
  return async (request: FastifyRequest): Promise<void> => {
    const header = request.headers.authorization;
    if (header?.startsWith(BEARER_PREFIX) !== true) throw new AppError('AUTH_UNAUTHORIZED');
    const claims = await verifyAccessToken(secret, header.slice(BEARER_PREFIX.length));
    if (claims === undefined || !(await devices.isDeviceActive(claims.userId, claims.deviceId))) {
      throw new AppError('AUTH_UNAUTHORIZED');
    }
    request.auth = claims;
  };
}

/** Claims of an authenticated request. Only valid after authenticate() ran. */
export function requireAuth(request: FastifyRequest): AccessClaims {
  if (request.auth === undefined) throw new AppError('AUTH_UNAUTHORIZED');
  return request.auth;
}
