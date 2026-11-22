// Queries on users, preferences and consents. Always parameterized through Kysely.
import type { Db } from '../../shared/database.js';
import { newId } from '../../shared/ids.js';

export interface UserRow {
  readonly id: string;
  readonly email: string;
  readonly passwordHash: string;
  readonly locale: 'es' | 'en';
  readonly verified: boolean;
}

export interface PendingUserInput {
  readonly email: string;
  readonly passwordHash: string;
  readonly locale: 'es' | 'en';
  readonly termsVersion: string;
  readonly privacyVersion: string;
}

function toLocale(value: string): 'es' | 'en' {
  return value === 'en' ? 'en' : 'es';
}

export class UserRepository {
  constructor(private readonly db: Db) {}

  async findByEmail(email: string): Promise<UserRow | undefined> {
    return this.findBy('email', email);
  }

  async findById(id: string): Promise<UserRow | undefined> {
    return this.findBy('id', id);
  }

  /** Creates or refreshes an unverified account together with its consents. */
  async savePending(input: PendingUserInput, existingId?: string): Promise<string> {
    const userId = existingId ?? newId();
    await this.db.transaction().execute(async (trx) => {
      if (existingId === undefined) {
        await trx
          .insertInto('users')
          .values({
            id: userId,
            email: input.email,
            password_hash: input.passwordHash,
            locale: input.locale,
          })
          .execute();
        await trx
          .insertInto('user_preferences')
          .values({ user_id: userId, locale: input.locale })
          .execute();
      } else {
        await trx
          .updateTable('users')
          .set({ password_hash: input.passwordHash, locale: input.locale, updated_at: new Date() })
          .where('id', '=', userId)
          .where('email_verified_at', 'is', null)
          .execute();
      }
      await trx
        .insertInto('consents')
        .values([
          { id: newId(), user_id: userId, document: 'terms', version: input.termsVersion },
          { id: newId(), user_id: userId, document: 'privacy', version: input.privacyVersion },
          { id: newId(), user_id: userId, document: 'age', version: '18+' },
        ])
        .execute();
    });
    return userId;
  }

  async markVerified(userId: string): Promise<void> {
    await this.db
      .updateTable('users')
      .set({ email_verified_at: new Date(), updated_at: new Date() })
      .where('id', '=', userId)
      .where('email_verified_at', 'is', null)
      .execute();
  }

  async updatePassword(userId: string, passwordHash: string): Promise<void> {
    await this.db
      .updateTable('users')
      .set({ password_hash: passwordHash, updated_at: new Date() })
      .where('id', '=', userId)
      .execute();
  }

  private async findBy(column: 'email' | 'id', value: string): Promise<UserRow | undefined> {
    const row = await this.db
      .selectFrom('users')
      .select(['id', 'email', 'password_hash', 'locale', 'email_verified_at'])
      .where(column, '=', value)
      .where('deleted_at', 'is', null)
      .executeTakeFirst();
    if (row === undefined) return undefined;
    return {
      id: row.id,
      email: row.email,
      passwordHash: row.password_hash,
      locale: toLocale(row.locale),
      verified: row.email_verified_at !== null,
    };
  }
}
