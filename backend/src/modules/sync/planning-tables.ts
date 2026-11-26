// Synced planning and session tables (08b): closed row schemas and their column mapping.
// Wire rows use camelCase; database columns use snake_case. Only these tables and
// columns can ever be written by the generic sync (identifiers are never user input).
import { z } from 'zod';
import { ATHLETE_NUMERIC_COLUMNS, athleteSchemas } from './athlete-tables.js';
import { clockSchemas } from './clock-tables.js';
import { PROGRESS_NUMERIC_COLUMNS, progressSchemas } from './progress-tables.js';

import { PERIODIZATION_NUMERIC_COLUMNS, periodizationSchemas } from './periodization-tables.js';
import {
  SESSION_INSTANT_COLUMNS,
  SESSION_NUMERIC_COLUMNS,
  sessionSchemas,
} from './session-tables.js';

const isoDate = z.iso.date();
const updatedAt = z.iso.datetime({ offset: true });
const optionalInt = (min: number, max: number) => z.number().int().min(min).max(max).nullable();
const optionalNum = (min: number, max: number) => z.number().min(min).max(max).nullable();

export const planningSchemas = {
  routines: z.strictObject({
    id: z.uuid(),
    name: z.string().trim().min(1).max(80),
    notes: z.string().max(1000),
    folder: z.string().max(60),
    colorKey: z.string().max(20),
    archived: z.boolean(),
    updatedAt,
  }),
  routine_exercises: z.strictObject({
    id: z.uuid(),
    routineId: z.uuid(),
    exerciseId: z.uuid(),
    position: z.number().int().min(0).max(200),
    groupId: z.string().max(40),
    targetSets: optionalInt(1, 50),
    targetRepsMin: optionalInt(0, 1000),
    targetRepsMax: optionalInt(0, 1000),
    targetLoadKg: optionalNum(0, 2000),
    targetRpe: optionalNum(1, 10),
    targetRir: optionalInt(0, 10),
    targetDurationS: optionalInt(0, 86400),
    targetDistanceM: optionalNum(0, 1_000_000),
    restSeconds: optionalInt(0, 3600),
    notes: z.string().max(500),
    updatedAt,
  }),
  programs: z.strictObject({
    id: z.uuid(),
    name: z.string().trim().min(1).max(80),
    startDate: isoDate.nullable(),
    weeksCount: z.number().int().min(1).max(52),
    isActive: z.boolean(),
    notes: z.string().max(1000),
    updatedAt,
  }),
  program_days: z.strictObject({
    id: z.uuid(),
    programId: z.uuid(),
    weekIndex: z.number().int().min(0).max(51),
    dayIndex: z.number().int().min(0).max(6),
    routineId: z.uuid().nullable(),
    isRest: z.boolean(),
    activeRest: z.boolean(),
    updatedAt,
  }),
  scheduled_workouts: z.strictObject({
    id: z.uuid(),
    date: isoDate,
    routineId: z.uuid().nullable(),
    programDayId: z.uuid().nullable(),
    status: z.enum(['planned', 'done', 'skipped']),
    sessionId: z.uuid().nullable(),
    rescheduledFrom: isoDate.nullable(),
    plannedTime: z
      .string()
      .regex(/^([01][0-9]|2[0-3]):[0-5][0-9]$/)
      .nullable()
      .default(null),
    updatedAt,
  }),
  ...sessionSchemas,
  ...periodizationSchemas,
  ...athleteSchemas,
  ...clockSchemas,
  ...progressSchemas,
} as const;

export type PlanningTable = keyof typeof planningSchemas;
export const PLANNING_TABLES = Object.keys(planningSchemas) as PlanningTable[];

/** numeric columns come back from pg as strings and are sent as numbers. */
const NUMERIC_COLUMNS = new Set([
  'target_load_kg',
  'target_rpe',
  'target_distance_m',
  ...SESSION_NUMERIC_COLUMNS,
  ...PERIODIZATION_NUMERIC_COLUMNS,
  ...ATHLETE_NUMERIC_COLUMNS,
  ...PROGRESS_NUMERIC_COLUMNS,
]);

export const toSnake = (key: string): string => key.replace(/[A-Z]/g, (c) => `_${c.toLowerCase()}`);
const toCamel = (key: string): string =>
  key.replace(/_([a-z])/g, (_m, c: string) => c.toUpperCase());

/** Wire row without id and updatedAt, as database columns. */
export function toColumns(row: Record<string, unknown>): Record<string, unknown> {
  const out: Record<string, unknown> = {};
  for (const [key, value] of Object.entries(row)) {
    if (key !== 'id' && key !== 'updatedAt') out[toSnake(key)] = value;
  }
  return out;
}

/** Database row to wire row, keeping only the columns of the table schema. */
export function fromColumns(table: PlanningTable, row: Record<string, unknown>) {
  const keys = Object.keys(planningSchemas[table].shape);
  const out: Record<string, unknown> = {};
  for (const key of keys) {
    const column = key === 'updatedAt' ? 'updated_at' : toSnake(key);
    const value = row[column];
    if (key === 'updatedAt') out[key] = (value as Date).toISOString();
    else if (SESSION_INSTANT_COLUMNS.has(column))
      out[key] = value === null ? null : (value as Date).toISOString();
    else if (NUMERIC_COLUMNS.has(column) && value !== null) out[key] = Number(value);
    else out[toCamel(column)] = value;
  }
  return out;
}
