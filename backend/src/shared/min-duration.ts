// Pads a response to a minimum duration so that timing does not reveal whether an
// account exists (registration, resend and password recovery).
export async function withMinimumDuration<T>(
  minimumMs: number,
  work: () => Promise<T>,
): Promise<T> {
  const started = performance.now();
  try {
    return await work();
  } finally {
    const remaining = minimumMs - (performance.now() - started);
    if (remaining > 0) await new Promise((resolve) => setTimeout(resolve, remaining));
  }
}
