// Synced advanced planning tables (10f Fase 4b): blocks, substitutions and muscle targets.
// Joined into the generic sync by planning-tables.ts; only these columns can be written.
import { z } from 'zod';

const updatedAt = z.iso.datetime({ offset: true });

export const periodizationSchemas = {
  program_blocks: z.strictObject({
    id: z.uuid(),
    programId: z.uuid(),
    position: z.number().int().min(0).max(20),
    name: z.string().max(60),
    goal: z.enum(['general', 'strength', 'hypertrophy', 'endurance', 'skill']),
    phase: z.enum(['accumulation', 'intensification', 'deload', 'test']),
    weeks: z.number().int().min(1).max(8),
    progression: z.enum(['none', 'double', 'percent', 'rpe']),
    step: z.number().min(0).max(50).nullable(),
    updatedAt,
  }),
  block_substitutions: z.strictObject({
    id: z.uuid(),
    blockId: z.uuid(),
    fromExerciseId: z.uuid(),
    toExerciseId: z.uuid(),
    updatedAt,
  }),
  muscle_targets: z
    .strictObject({
      id: z.uuid(),
      muscleGroupId: z.number().int().min(1).max(100),
      minSets: z.number().int().min(0).max(60),
      maxSets: z.number().int().min(0).max(60),
      updatedAt,
    })
    .refine((row) => row.minSets <= row.maxSets, { message: 'minSets must not exceed maxSets' }),
} as const;

/** numeric columns come back from pg as strings and are sent as numbers. */
export const PERIODIZATION_NUMERIC_COLUMNS = ['step'];
