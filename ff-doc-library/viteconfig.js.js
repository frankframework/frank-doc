import { defineConfig } from 'vite';

// https://vite.dev/config/
export default defineConfig({
  plugins: [],
  build: {
    outDir: 'dist/ff-doc',
    minify: true,
    lib: {
      name: 'ff-doc',
      fileName: 'ff-doc',
      formats: ['es'],
    },
  },
});
