// Password rules (07-BACKEND.md): at least 10 characters and not a well-known password.
import { AppError } from '../../shared/errors.js';
import { COMMON_PASSWORDS } from './common-passwords.js';

const MIN_LENGTH = 10;

export function assertStrongPassword(password: string, email: string): void {
  const normalized = password.trim().toLowerCase();
  const localPart = email.split('@')[0] ?? '';
  const graphemes = Array.from(new Intl.Segmenter().segment(password)).length;
  const tooShort = graphemes < MIN_LENGTH;
  const common = COMMON_PASSWORDS.has(normalized);
  const repetitive = /^(.)\1+$/.test(normalized);
  const containsEmail = localPart.length >= 4 && normalized.includes(localPart.toLowerCase());
  if (tooShort || common || repetitive || containsEmail) throw new AppError('AUTH_WEAK_PASSWORD');
}
