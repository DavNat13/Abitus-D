// Entry point for local runs and for Vercel (zero-config Fastify detects src/server.ts).
import { buildApp } from './app/build-app.js';
import { loadConfig } from './app/config.js';
import { closePool, getPool } from './shared/database-pool.js';

const config = loadConfig();
const databaseUrl = config.databaseUrl;

const app = buildApp({
  logLevel: config.logLevel,
  ...(databaseUrl === undefined
    ? {}
    : {
        databaseProbe: async () => {
          await getPool(databaseUrl).query('select 1');
        },
      }),
});

app.addHook('onClose', closePool);

try {
  await app.listen({ port: config.port, host: '0.0.0.0' });
} catch (error) {
  app.log.error(error);
  process.exit(1);
}
