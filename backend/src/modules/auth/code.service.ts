// Six-digit codes: stored as HMAC, valid 10 minutes, 5 attempts, 60 s between sends,
// daily cap per email. Only the latest code of an email and purpose is valid.
import { sql } from 'kysely';
import type { Db } from '../../shared/database.js';
import type { CodePurpose } from '../../shared/database-types.js';
import { hashesEqual, keyedHash, newNumericCode } from '../../shared/crypto/secure-hash.js';
import { AppError } from '../../shared/errors.js';
import { newId } from '../../shared/ids.js';

const CODE_TTL_MS = 10 * 60 * 1000;
const MAX_ATTEMPTS = 5;
const RESEND_COOLDOWN_SECONDS = 60;
const DAILY_CAP = 10;

export class CodeService {
  constructor(
    private readonly db: Db,
    private readonly pepper: string,
  ) {}

  /** Creates a new code. Throws AUTH_RESEND_TOO_SOON or RATE_LIMITED when not allowed. */
  async issue(email: string, purpose: CodePurpose, ipHash: string): Promise<string> {
    const stats = await this.db
      .selectFrom('verification_codes')
      .select((eb) => [
        eb.fn.countAll<string>().as('today'),
        eb.fn
          .count<string>('id')
          .filterWhere(
            'created_at',
            '>',
            sql<Date>`now() - make_interval(secs => ${RESEND_COOLDOWN_SECONDS})`,
          )
          .as('recent'),
      ])
      .where('email', '=', email)
      .where('purpose', '=', purpose)
      .where('created_at', '>', sql<Date>`now() - interval '24 hours'`)
      .executeTakeFirstOrThrow();
    if (Number(stats.recent) > 0) throw new AppError('AUTH_RESEND_TOO_SOON');
    if (Number(stats.today) >= DAILY_CAP) throw new AppError('RATE_LIMITED');
    const code = newNumericCode();
    await this.db
      .insertInto('verification_codes')
      .values({
        id: newId(),
        email,
        purpose,
        code_hash: this.hash(email, purpose, code),
        expires_at: new Date(Date.now() + CODE_TTL_MS),
        ip_hash: ipHash,
      })
      .execute();
    return code;
  }

  /** Consumes the code or throws AUTH_INVALID_CODE / AUTH_CODE_EXPIRED. */
  async consume(email: string, purpose: CodePurpose, code: string): Promise<void> {
    const row = await this.db
      .selectFrom('verification_codes')
      .select(['id', 'code_hash', 'attempts', 'expires_at', 'consumed_at'])
      .where('email', '=', email)
      .where('purpose', '=', purpose)
      .orderBy('created_at', 'desc')
      .limit(1)
      .executeTakeFirst();
    if (row === undefined || row.consumed_at !== null) throw new AppError('AUTH_INVALID_CODE');
    if (row.expires_at.getTime() < Date.now() || row.attempts >= MAX_ATTEMPTS) {
      throw new AppError('AUTH_CODE_EXPIRED');
    }
    if (!hashesEqual(row.code_hash, this.hash(email, purpose, code))) {
      await this.db
        .updateTable('verification_codes')
        .set((eb) => ({ attempts: eb('attempts', '+', 1) }))
        .where('id', '=', row.id)
        .execute();
      throw new AppError('AUTH_INVALID_CODE');
    }
    const result = await this.db
      .updateTable('verification_codes')
      .set({ consumed_at: new Date() })
      .where('id', '=', row.id)
      .where('consumed_at', 'is', null)
      .executeTakeFirst();
    if (result.numUpdatedRows !== 1n) throw new AppError('AUTH_INVALID_CODE');
  }

  private hash(email: string, purpose: CodePurpose, code: string): string {
    return keyedHash(this.pepper, `${purpose}:${email}:${code}`);
  }
}
