// Synced workout logging tables (08b, 10f Fase 4): closed row schemas in camelCase.
// Joined into the generic sync by planning-tables.ts; only these columns can be written.
import { z } from 'zod';

const instant = z.iso.datetime({ offset: true });
const optionalInt = (min: number, max: number) => z.number().int().min(min).max(max).nullable();
const optionalNum = (min: number, max: number) => z.number().min(min).max(max).nullable();

export const sessionSchemas = {
  workout_sessions: z.strictObject({
    id: z.uuid(),
    routineId: z.uuid().nullable(),
    scheduledWorkoutId: z.uuid().nullable(),
    title: z.string().trim().min(1).max(80),
    startedAt: instant,
    endedAt: instant.nullable(),
    notes: z.string().max(2000),
    sessionRpe: optionalNum(1, 10),
    sleepQuality: optionalInt(1, 5),
    energy: optionalInt(1, 5),
    bodyweightKg: optionalNum(20, 400),
    updatedAt: instant,
  }),
  session_exercises: z.strictObject({
    id: z.uuid(),
    sessionId: z.uuid(),
    exerciseId: z.uuid(),
    position: z.number().int().min(0).max(200),
    groupId: z.string().max(40),
    notes: z.string().max(500),
    discomfort: z.string().max(40),
    updatedAt: instant,
  }),
  sets: z.strictObject({
    id: z.uuid(),
    sessionExerciseId: z.uuid(),
    exerciseId: z.uuid(),
    performedAt: instant,
    setIndex: z.number().int().min(0).max(100),
    setType: z.enum(['normal', 'warmup', 'drop', 'amrap', 'failure', 'rest_pause']),
    side: z.enum(['both', 'left', 'right']),
    reps: optionalInt(0, 1000),
    loadKg: optionalNum(0, 2000),
    addedLoadKg: optionalNum(-500, 500),
    durationS: optionalInt(0, 86400),
    distanceM: optionalNum(0, 1_000_000),
    rpe: optionalNum(1, 10),
    rir: optionalInt(0, 10),
    restS: optionalInt(0, 3600),
    tempo: z.string().max(12),
    completedAt: instant.nullable(),
    notes: z.string().max(500),
    updatedAt: instant,
  }),
} as const;

/** numeric columns come back from pg as strings and are sent as numbers. */
export const SESSION_NUMERIC_COLUMNS = [
  'session_rpe',
  'bodyweight_kg',
  'load_kg',
  'added_load_kg',
  'distance_m',
  'rpe',
];

/** timestamptz columns other than updated_at, sent as ISO strings. */
export const SESSION_INSTANT_COLUMNS = new Set([
  'started_at',
  'ended_at',
  'performed_at',
  'completed_at',
]);
