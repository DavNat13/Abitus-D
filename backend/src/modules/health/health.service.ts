// Liveness and readiness checks. Readiness depends on the database answering.
export type DatabaseProbe = () => Promise<void>;

export interface ReadinessResult {
  readonly ready: boolean;
  readonly database: 'up' | 'down' | 'not_configured';
}

export async function checkReadiness(probe: DatabaseProbe | undefined): Promise<ReadinessResult> {
  if (probe === undefined) return { ready: false, database: 'not_configured' };
  try {
    await probe();
    return { ready: true, database: 'up' };
  } catch {
    return { ready: false, database: 'down' };
  }
}
