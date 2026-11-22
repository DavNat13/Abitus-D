// Argon2id with OWASP minimum parameters (06b): m=19456 KiB, t=2, p=1.
import { hash, verify } from '@node-rs/argon2';

const ARGON2ID = 2;
const OPTIONS = { algorithm: ARGON2ID, memoryCost: 19456, timeCost: 2, parallelism: 1 } as const;

// Hash of a random string, used to spend the same time when an account does not exist.
const DUMMY_PASSWORD = 'dummy-password-for-constant-time-checks';
let dummyHash: Promise<string> | undefined;

export function hashPassword(password: string): Promise<string> {
  return hash(password, OPTIONS);
}

export async function verifyPassword(passwordHash: string, password: string): Promise<boolean> {
  try {
    return await verify(passwordHash, password);
  } catch {
    return false;
  }
}

/** Runs a full verification against a fixed hash and always returns false. */
export async function burnPasswordCheck(password: string): Promise<false> {
  dummyHash ??= hashPassword(DUMMY_PASSWORD);
  await verifyPassword(await dummyHash, password);
  return false;
}
