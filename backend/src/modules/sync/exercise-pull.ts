// Pull rows of own exercises in the wire format of the sync protocol.
import type { ExerciseSyncRepository } from './exercise-sync.repository.js';

export interface WireChange {
  readonly table: string;
  readonly op: 'upsert' | 'delete';
  readonly serverSeq: number;
  readonly row: Record<string, unknown>;
}

export async function exerciseChanges(
  repo: ExerciseSyncRepository,
  userId: string,
  cursor: number,
  limit: number,
): Promise<WireChange[]> {
  const data = await repo.changesAfter(userId, cursor, limit);
  return data.rows.map((r) => {
    const translation = data.translations.find((t) => t.exercise_id === r.id);
    return {
      table: 'exercises',
      op: r.deleted_at === null ? 'upsert' : 'delete',
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
}
