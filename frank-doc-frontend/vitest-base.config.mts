import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    server: {
      deps: {
        // Resolve Solar's extensionless/directory imports through Vite, including
        // imports from angular-components that would otherwise use native Node ESM.
        inline: ['@solar-icons/angular', '@frankframework/angular-components'],
      },
    },
  },
});
