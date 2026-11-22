// Runtime configuration validated at startup (see ../../../.env.example).
// The server refuses to start if a required variable is missing or weak.
import { z } from 'zod';

const MIN_SECRET_LENGTH = 32;
const secret = z.string().min(MIN_SECRET_LENGTH);

const envSchema = z.object({
  NODE_ENV: z.enum(['development', 'test', 'production']).default('development'),
  PORT: z.coerce.number().int().positive().default(3000),
  LOG_LEVEL: z.enum(['fatal', 'error', 'warn', 'info', 'debug', 'trace', 'silent']).default('info'),
  DATABASE_URL: z.string().min(1),
  JWT_ACCESS_SECRET: secret,
  REFRESH_TOKEN_PEPPER: secret,
  CODE_PEPPER: secret,
  IP_HASH_PEPPER: secret,
  JOBS_SECRET: secret,
  SMTP_HOST: z.string().min(1),
  SMTP_PORT: z.coerce.number().int().positive(),
  SMTP_USER: z.string().default(''),
  SMTP_PASSWORD: z.string().default(''),
  MAIL_FROM: z.string().min(3),
  MAIL_DAILY_CAP: z.coerce.number().int().positive().default(300),
});

export interface AppConfig {
  readonly env: 'development' | 'test' | 'production';
  readonly port: number;
  readonly logLevel: string;
  readonly databaseUrl: string;
  readonly secrets: {
    readonly jwtAccess: string;
    readonly refreshTokenPepper: string;
    readonly codePepper: string;
    readonly ipHashPepper: string;
    readonly jobs: string;
  };
  readonly smtp: {
    readonly host: string;
    readonly port: number;
    readonly user: string;
    readonly password: string;
    readonly from: string;
    readonly dailyCap: number;
  };
}

export function loadConfig(env: NodeJS.ProcessEnv = process.env): AppConfig {
  const parsed = envSchema.safeParse(env);
  if (!parsed.success) {
    const names = parsed.error.issues.map((issue) => issue.path.join('.')).join(', ');
    throw new Error(`Invalid or missing environment variables: ${names}`);
  }
  const e = parsed.data;
  return {
    env: e.NODE_ENV,
    port: e.PORT,
    logLevel: e.LOG_LEVEL,
    databaseUrl: e.DATABASE_URL,
    secrets: {
      jwtAccess: e.JWT_ACCESS_SECRET,
      refreshTokenPepper: e.REFRESH_TOKEN_PEPPER,
      codePepper: e.CODE_PEPPER,
      ipHashPepper: e.IP_HASH_PEPPER,
      jobs: e.JOBS_SECRET,
    },
    smtp: {
      host: e.SMTP_HOST,
      port: e.SMTP_PORT,
      user: e.SMTP_USER,
      password: e.SMTP_PASSWORD,
      from: e.MAIL_FROM,
      dailyCap: e.MAIL_DAILY_CAP,
    },
  };
}
