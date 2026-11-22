// Short-lived JWT access tokens (HS256): sub = user id, did = device id.
import { jwtVerify, SignJWT } from 'jose';
import { newId } from '../../shared/ids.js';

export const ACCESS_TOKEN_TTL_SECONDS = 15 * 60;
const ISSUER = 'abitus-d';
const AUDIENCE = 'abitus-d-app';

export interface AccessClaims {
  readonly userId: string;
  readonly deviceId: string;
}

function key(secret: string): Uint8Array {
  return new TextEncoder().encode(secret);
}

export function signAccessToken(secret: string, claims: AccessClaims): Promise<string> {
  return new SignJWT({ did: claims.deviceId })
    .setProtectedHeader({ alg: 'HS256' })
    .setSubject(claims.userId)
    .setJti(newId())
    .setIssuer(ISSUER)
    .setAudience(AUDIENCE)
    .setIssuedAt()
    .setExpirationTime(`${String(ACCESS_TOKEN_TTL_SECONDS)}s`)
    .sign(key(secret));
}

/** Returns the claims, or undefined for any invalid, expired or tampered token. */
export async function verifyAccessToken(
  secret: string,
  token: string,
): Promise<AccessClaims | undefined> {
  try {
    const { payload } = await jwtVerify(token, key(secret), {
      algorithms: ['HS256'],
      issuer: ISSUER,
      audience: AUDIENCE,
    });
    if (typeof payload.sub !== 'string' || typeof payload.did !== 'string') return undefined;
    return { userId: payload.sub, deviceId: payload.did };
  } catch {
    return undefined;
  }
}
