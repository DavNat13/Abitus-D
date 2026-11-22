// Entry point for local runs and for Vercel (zero-config Fastify detects src/server.ts).
import { createDeps } from './app/app-deps.js';
import { buildApp } from './app/build-app.js';
import { loadConfig } from './app/config.js';
import { createSmtpSender } from './modules/mail/mail-sender.js';
import { createDatabase } from './shared/database.js';

const config = loadConfig();
const db = createDatabase(config.databaseUrl);
const app = await buildApp(createDeps(config, db, createSmtpSender(config.smtp)));

app.addHook('onClose', async () => {
  await db.destroy();
});

try {
  await app.listen({ port: config.port, host: '0.0.0.0' });
} catch (error) {
  app.log.error(error);
  process.exit(1);
}
