// Push and pull of user data (07-BACKEND.md). Last write wins per row by updatedAt; ties keep
// the server copy; client clocks more than 5 minutes ahead are clamped to the server time.
import type { Db } from '../../shared/database.js';
import { exerciseChanges, type WireChange } from './exercise-pull.js';
import { ExerciseSyncRepository } from './exercise-sync.repository.js';
import { fromColumns, PLANNING_TABLES, type PlanningTable } from './planning-tables.js';
import { PlanningSyncRepository } from './planning-sync.repository.js';
import type { OwnExerciseRow, PushChange } from './sync.schemas.js';

const MAX_CLOCK_SKEW_MS = 5 * 60 * 1000;

export type RejectReason = 'forbidden' | 'invalid_reference';
type Outcome = 'accepted' | RejectReason;

export interface PushResult {
  readonly accepted: string[];
  readonly rejected: { id: string; reason: RejectReason }[];
  readonly serverTime: string;
}

function clamp(clientTime: string, now: Date): Date {
  const time = new Date(clientTime);
  return time.getTime() > now.getTime() + MAX_CLOCK_SKEW_MS ? now : time;
}

export class SyncService {
  private readonly exercises: ExerciseSyncRepository;
  private readonly planning: PlanningSyncRepository;

  constructor(db: Db) {
    this.exercises = new ExerciseSyncRepository(db);
    this.planning = new PlanningSyncRepository(db);
  }

  async push(userId: string, changes: PushChange[]): Promise<PushResult> {
    const now = new Date();
    const result: PushResult = { accepted: [], rejected: [], serverTime: now.toISOString() };
    let refs: Awaited<ReturnType<ExerciseSyncRepository['referenceIds']>> | undefined;
    for (const change of changes) {
      const updatedAt = clamp(change.row.updatedAt, now);
      let outcome: Outcome;
      if (change.table === 'exercises') {
        refs ??= await this.exercises.referenceIds();
        outcome = await this.pushExercise(userId, change, updatedAt, refs);
      } else {
        outcome = await this.pushPlanning(userId, change.table, change, updatedAt);
      }
      if (outcome === 'accepted') result.accepted.push(change.row.id);
      else result.rejected.push({ id: change.row.id, reason: outcome });
    }
    return result;
  }

  async pull(userId: string, cursor: number, limit: number) {
    const lists = await Promise.all([
      exerciseChanges(this.exercises, userId, cursor, limit + 1),
      ...PLANNING_TABLES.map(async (table): Promise<WireChange[]> => {
        const rows = await this.planning.changesAfter(table, userId, cursor, limit + 1);
        return rows.map((r) => ({
          table,
          op: r.deleted_at === null ? 'upsert' : 'delete',
          serverSeq: Number(r.server_seq),
          row: fromColumns(table, r),
        }));
      }),
    ]);
    const merged = lists.flat().sort((a, b) => a.serverSeq - b.serverSeq);
    const changes = merged.slice(0, limit);
    const last = changes.at(-1);
    return {
      changes,
      hasMore: merged.length > limit,
      nextCursor: last ? last.serverSeq : cursor,
      minValidCursor: 0,
    };
  }

  private async pushExercise(
    userId: string,
    change: PushChange,
    updatedAt: Date,
    refs: Awaited<ReturnType<ExerciseSyncRepository['referenceIds']>>,
  ): Promise<Outcome> {
    const id = change.row.id;
    const stored = await this.exercises.ownership(id);
    if (stored && stored.ownerUserId !== userId) return 'forbidden';
    if (stored && stored.updatedAt.getTime() >= updatedAt.getTime()) return 'accepted';
    if (change.op === 'delete') {
      if (stored) await this.exercises.softDelete(userId, id, updatedAt);
      return 'accepted';
    }
    const row = change.row as OwnExerciseRow;
    const valid =
      refs.disciplines.has(row.disciplineId) &&
      row.muscles.every((m) => refs.muscles.has(m.muscleGroupId)) &&
      row.equipmentIds.every((q) => refs.equipment.has(q));
    if (!valid) return 'invalid_reference';
    await this.exercises.upsert(userId, row, updatedAt);
    return 'accepted';
  }

  private async pushPlanning(
    userId: string,
    table: PlanningTable,
    change: PushChange,
    updatedAt: Date,
  ): Promise<Outcome> {
    const id = change.row.id;
    const stored = await this.planning.stored(table, id);
    if (table === 'athlete_profiles' && id !== userId) return 'forbidden';
    if (stored && stored.userId !== userId) return 'forbidden';
    if (stored && stored.updatedAt.getTime() >= updatedAt.getTime()) return 'accepted';
    if (change.op === 'delete') {
      if (stored) await this.planning.softDelete(table, userId, id, updatedAt);
      return 'accepted';
    }
    await this.planning.upsert(table, userId, change.row, updatedAt);
    return 'accepted';
  }
}
