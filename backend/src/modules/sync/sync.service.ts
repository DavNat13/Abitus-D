// Push and pull of user data (07-BACKEND.md). Last write wins per row by updatedAt; ties keep
// the server copy; client clocks more than 5 minutes ahead are clamped to the server time.
import type { Db } from '../../shared/database.js';
import { ExerciseSyncRepository } from './exercise-sync.repository.js';
import type { OwnExerciseRow, PushChange } from './sync.schemas.js';

const MAX_CLOCK_SKEW_MS = 5 * 60 * 1000;

export type RejectReason = 'forbidden' | 'invalid_reference';

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

  constructor(db: Db) {
    this.exercises = new ExerciseSyncRepository(db);
  }

  async push(userId: string, changes: PushChange[]): Promise<PushResult> {
    const now = new Date();
    const refs = await this.exercises.referenceIds();
    const accepted: string[] = [];
    const rejected: PushResult['rejected'] = [];
    for (const change of changes) {
      const id = change.row.id;
      const stored = await this.exercises.ownership(id);
      if (stored && stored.ownerUserId !== userId) {
        rejected.push({ id, reason: 'forbidden' });
        continue;
      }
      const updatedAt = clamp(change.row.updatedAt, now);
      accepted.push(id);
      if (stored && stored.updatedAt.getTime() >= updatedAt.getTime()) continue;
      if (change.op === 'delete') {
        if (stored) await this.exercises.softDelete(userId, id, updatedAt);
        continue;
      }
      if (!this.validReferences(change.row, refs)) {
        accepted.pop();
        rejected.push({ id, reason: 'invalid_reference' });
        continue;
      }
      await this.exercises.upsert(userId, change.row, updatedAt);
    }
    return { accepted, rejected, serverTime: now.toISOString() };
  }

  async pull(userId: string, cursor: number, limit: number) {
    const data = await this.exercises.changesAfter(userId, cursor, limit + 1);
    const hasMore = data.rows.length > limit;
    const rows = data.rows.slice(0, limit);
    const changes = rows.map((r) => {
      const translation = data.translations.find((t) => t.exercise_id === r.id);
      return {
        table: 'exercises' as const,
        op: r.deleted_at === null ? ('upsert' as const) : ('delete' as const),
        serverSeq: Number(r.server_seq),
        row: {
          id: r.id,
          disciplineId: r.discipline_id,
          metricType: r.metric_type,
          movementPattern: r.movement_pattern,
          difficulty: r.difficulty,
          isUnilateral: r.is_unilateral,
          usesBodyweight: r.uses_bodyweight,
          defaultRestSeconds: r.default_rest_seconds,
          locale: translation?.locale ?? 'es',
          name: translation?.name ?? '',
          notes: translation?.summary ?? '',
          muscles: data.muscles
            .filter((m) => m.exercise_id === r.id)
            .map((m) => ({ muscleGroupId: m.muscle_group_id, role: m.role })),
          equipmentIds: data.equipment
            .filter((q) => q.exercise_id === r.id)
            .map((q) => q.equipment_id),
          updatedAt: r.updated_at.toISOString(),
        },
      };
    });
    const last = changes.at(-1);
    return { changes, hasMore, nextCursor: last ? last.serverSeq : cursor, minValidCursor: 0 };
  }

  private validReferences(
    row: OwnExerciseRow,
    refs: Awaited<ReturnType<ExerciseSyncRepository['referenceIds']>>,
  ): boolean {
    return (
      refs.disciplines.has(row.disciplineId) &&
      row.muscles.every((m) => refs.muscles.has(m.muscleGroupId)) &&
      row.equipmentIds.every((q) => refs.equipment.has(q))
    );
  }
}
