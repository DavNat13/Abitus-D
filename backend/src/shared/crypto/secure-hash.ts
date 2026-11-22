// Keyed hashes (HMAC-SHA-256) for codes, refresh tokens, IPs and rate limit keys.
// Each purpose uses its own pepper so one leaked secret does not expose the others.
import { createHmac, randomBytes, randomInt, timingSafeEqual } from 'node:crypto';

const REFRESH_TOKEN_BYTES = 32;
const CODE_DIGITS = 6;

export function keyedHash(pepper: string, value: string): string {
  return createHmac('sha256', pepper).update(value).digest('base64url');
}

export function hashesEqual(a: string, b: string): boolean {
  const left = Buffer.from(a);
  const right = Buffer.from(b);
  return left.length === right.length && timingSafeEqual(left, right);
}

/** Opaque 256-bit refresh token. Only its keyed hash is stored. */
export function newRefreshToken(): string {
  return randomBytes(REFRESH_TOKEN_BYTES).toString('base64url');
}

/** Six-digit numeric code from a cryptographic source. */
export function newNumericCode(): string {
  return randomInt(0, 10 ** CODE_DIGITS)
    .toString()
    .padStart(CODE_DIGITS, '0');
}
