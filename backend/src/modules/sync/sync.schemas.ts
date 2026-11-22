// Closed schemas of the sync protocol (07-BACKEND.md, Sincronización). Batches of up to 200.
import { z } from 'zod';
import { PLANNING_TABLES, planningSchemas } from './planning-tables.js';

export const MAX_BATCH = 200;
export const MAX_PULL = 500;

const metricType = z.enum([
  'weight_reps',
  'bodyweight_reps',
  'weighted_bodyweight_reps',
  'duration',
  'distance_duration',
  'swim_laps',
  'plyo',
]);
const movementPattern = z.enum([
  'push',
  'pull',
  'squat',
  'hinge',
  'lunge',
  'carry',
  'core',
  'jump',
  'locomotion',
  'swim',
  'mobility',
]);

export const ownExerciseRow = z.strictObject({
  id: z.uuid(),
  disciplineId: z.number().int().min(1).max(1000),
  metricType,
  movementPattern,
  difficulty: z.number().int().min(1).max(5),
  isUnilateral: z.boolean(),
  usesBodyweight: z.boolean(),
  defaultRestSeconds: z.number().int().min(0).max(1800),
  locale: z.enum(['es', 'en']),
  name: z.string().trim().min(1).max(120),
  notes: z.string().max(500),
  muscles: z
    .array(
      z.strictObject({
        muscleGroupId: z.number().int().min(1).max(1000),
        role: z.enum(['primary', 'secondary']),
      }),
    )
    .max(12),
  equipmentIds: z.array(z.number().int().min(1).max(1000)).max(12),
  updatedAt: z.iso.datetime({ offset: true }),
});

const exerciseUpsert = z.strictObject({
  table: z.literal('exercises'),
  op: z.literal('upsert'),
  row: ownExerciseRow,
});

const deleteChange = z.strictObject({
  table: z.enum(['exercises', ...PLANNING_TABLES]),
  op: z.literal('delete'),
  row: z.strictObject({ id: z.uuid(), updatedAt: z.iso.datetime({ offset: true }) }),
});

const planningUpserts = PLANNING_TABLES.map((table) =>
  z.strictObject({ table: z.literal(table), op: z.literal('upsert'), row: planningSchemas[table] }),
);

export const pushBody = z.strictObject({
  changes: z
    .array(z.union([exerciseUpsert, deleteChange, ...planningUpserts]))
    .min(1)
    .max(MAX_BATCH),
});

export const pullQuery = z.strictObject({
  cursor: z.coerce.number().int().min(0).default(0),
  limit: z.coerce.number().int().min(1).max(MAX_PULL).default(MAX_PULL),
});

export type OwnExerciseRow = z.infer<typeof ownExerciseRow>;
export type PushChange = z.infer<typeof pushBody>['changes'][number];
