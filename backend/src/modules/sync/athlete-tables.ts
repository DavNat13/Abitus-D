import { z } from 'zod';

const optionalInt = (min: number, max: number) => z.number().int().min(min).max(max).nullable();
const goals = z.enum(['general', 'strength', 'hypertrophy', 'endurance', 'skill']).nullable();

export const athleteSchemas = {
  athlete_profiles: z
    .strictObject({
      id: z.uuid(),
      birthYear: optionalInt(1900, 2100),
      sex: z.enum(['female', 'male', 'undisclosed']).nullable(),
      heightM: z.number().min(0.5).max(2.8).nullable(),
      weightKg: z.number().min(20).max(500).nullable(),
      trainingYears: optionalInt(0, 100),
      level: z.enum(['beginner', 'intermediate', 'advanced']).nullable(),
      disciplines: z.string().max(200),
      goal: goals,
      secondaryGoal: goals,
      daysPerWeek: optionalInt(1, 7),
      sessionDurationS: optionalInt(300, 14400),
      place: z.enum(['gym', 'home', 'park', 'other']).nullable(),
      equipment: z.string().max(500),
      healthConsent: z.boolean(),
      healthLimited: z.boolean().nullable(),
      injuries: z.string().max(1000),
      pushUps: optionalInt(0, 1000),
      pullUps: optionalInt(0, 1000),
      plankDurationS: optionalInt(0, 86400),
      onboardingDone: z.boolean(),
      updatedAt: z.iso.datetime({ offset: true }),
    })
    .refine((r) => r.healthConsent || (r.healthLimited === null && r.injuries === '')),
} as const;

export const ATHLETE_NUMERIC_COLUMNS = ['height_m', 'weight_kg'];
