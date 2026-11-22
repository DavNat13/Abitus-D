// Runtime configuration read from environment variables (see ../../.env.example).
export interface AppConfig {
  readonly port: number;
  readonly logLevel: string;
  readonly databaseUrl: string | undefined;
}

const DEFAULT_PORT = 3000;

export function loadConfig(env: NodeJS.ProcessEnv = process.env): AppConfig {
  const port = Number.parseInt(env.PORT ?? '', 10);
  return {
    port: Number.isInteger(port) && port > 0 ? port : DEFAULT_PORT,
    logLevel: env.LOG_LEVEL ?? 'info',
    databaseUrl: env.DATABASE_URL === '' ? undefined : env.DATABASE_URL,
  };
}
