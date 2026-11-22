// Keyed hash of the client IP: used for limits and audit without storing the address.
import type { FastifyRequest } from 'fastify';
import { keyedHash } from './crypto/secure-hash.js';

export function clientIpHash(request: FastifyRequest, pepper: string): string {
  return keyedHash(pepper, `ip:${request.ip}`);
}
