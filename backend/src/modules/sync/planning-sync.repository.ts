// Generic persistence of the flat synced tables. Table and column names come only from
// planning-tables.ts; values are always bound parameters (golden rule 18).
import { sql } from 'kysely';
import type { Db } from '../../shared/database.js';
import { type PlanningTable, toColumns } from './planning-tables.js';

export interface StoredRow {
  readonly userId: string;
  readonly updatedAt: Date;
}

export class PlanningSyncRepository {
  constructor(private readonly db: Db) {}

  async stored(table: PlanningTable, id: string): Promise<StoredRow | undefined> {
    const result = await sql<{ user_id: string; updated_at: Date }>`
      select user_id, updated_at from ${sql.table(table)} where id = ${id}`.execute(this.db);
    const row = result.rows[0];
    return row && { userId: row.user_id, updatedAt: row.updated_at };
  }

  async upsert(
    table: PlanningTable,
    userId: string,
    row: Record<string, unknown> & { id: string },
    updatedAt: Date,
  ): Promise<void> {
    const columns: Record<string, unknown> = {
      ...toColumns(row),
      updated_at: updatedAt,
      deleted_at: null,
    };
    const names = Object.keys(columns);
    const insertNames = sql.join([sql.id('id'), sql.id('user_id'), ...names.map((n) => sql.id(n))]);
    const values = sql.join([
      sql`${row.id}`,
      sql`${userId}`,
      ...names.map((n) => sql`${columns[n]}`),
    ]);
    const updates = sql.join(names.map((n) => sql`${sql.id(n)} = excluded.${sql.id(n)}`));
    await sql`
      insert into ${sql.table(table)} (${insertNames}) values (${values})
      on conflict (id) do update set ${updates}
      where ${sql.table(table)}.user_id = ${userId}`.execute(this.db);
  }

  async softDelete(
    table: PlanningTable,
    userId: string,
    id: string,
    updatedAt: Date,
  ): Promise<void> {
    await sql`
      update ${sql.table(table)} set deleted_at = ${updatedAt}, updated_at = ${updatedAt}
      where id = ${id} and user_id = ${userId}`.execute(this.db);
  }

  async changesAfter(table: PlanningTable, userId: string, cursor: number, limit: number) {
    const result = await sql<
      Record<string, unknown> & { server_seq: string; deleted_at: Date | null }
    >`
      select * from ${sql.table(table)}
      where user_id = ${userId} and server_seq > ${String(cursor)}
      order by server_seq limit ${limit}`.execute(this.db);
    return result.rows;
  }
}
