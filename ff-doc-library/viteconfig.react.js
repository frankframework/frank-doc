import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist/ff-doc/react',
    minify: true,
    lib: {
      name: 'ff-doc',
      fileName: 'ff-doc',
      formats: ['es'],
    },
  },
});
