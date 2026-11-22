import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    include: ['test/**/*.test.ts'],
    environment: 'node',
    globalSetup: ['test/support/global-setup.ts'],
    testTimeout: 30000,
    hookTimeout: 30000,
  },
});
