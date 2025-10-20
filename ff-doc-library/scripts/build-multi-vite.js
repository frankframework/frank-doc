import { build } from 'vite';
import path from 'node:path';
import fs from 'node:fs';
import { defineConfig } from 'vite';
import dts from 'vite-plugin-dts';

const baseFileName = 'frankframework-ff-doc';
const target = 'es2022';
const entries = [
  {
    name: 'es',
    entry: 'projects/ff-doc/src/es-api.ts',
    outDir: 'dist/ff-doc',
    fileName: `${baseFileName}.mjs`,
    target,
  },
  {
    name: 'react',
    entry: 'projects/ff-doc/src/react-api.ts',
    outDir: 'dist/ff-doc/react',
    fileName: `${baseFileName}.mjs`,
    target,
    // ensure react is external
    additionalExternal: ['react'],
  },
];

function createFesm2022Config(options) {
  const entry = path.resolve(process.cwd(), options.entry);
  const outDir = path.resolve(process.cwd(), options.outDir);
  const fileName = options.fileName;
  const target = options.target ?? 'es2022';
  const additionalExternal = options.additionalExternal ?? [];

  return defineConfig({
    build: {
      lib: {
        entry,
        formats: ['es'],
        // Vite's fileName function is sometimes used; we still enforce entryFileNames below
        // fileName: () => fileName,
        // name: undefined,
      },
      outDir,
      target,
      sourcemap: true,
      emptyOutDir: true,
      minify: false,
      assetsInlineLimit: 0,
      rollupOptions: {
        // externalize peer deps (via plugin) and any additional externals requested
        external: additionalExternal,
        output: {
          // enforce .mjs naming for entries and chunks
          entryFileNames: fileName,
          // chunkFileNames: `chunk-[name]-[hash].mjs`,
          // assetFileNames: `assets/[name]-[hash][extname]`,
          // keep default manualChunks to allow code-splitting if your entry uses dynamic imports;
          // for a strictly flat file you can set preserveModules: false (default) and avoid dynamic imports.
        },
      },
    },
    plugins: [
      // this plugin marks package.json peerDependencies as external
      // peerDepsExternal,
      dts(
        {
          entryRoot: entry,
        },
        /*{
          beforeWriteFile: (filePath, content) => ({
            filePath: filePath.replace('path/to/file.d.ts', 'index.d.ts'),
            content,
          }),
        }*/
      ),
    ],
    /*resolve: {
      alias: {
        '@': path.resolve(process.cwd(), 'src'),
      },
    },*/
    // For React TSX entry, Vite/esbuild will handle JSX automatically if you have correct tsconfig settings.
  });
}

async function run() {
  for (const spec of entries) {
    console.log(`\nBuilding "${spec.name}" → ${spec.outDir}`);
    // verify entry exists
    const entryPath = path.resolve(process.cwd(), spec.entry);
    if (!fs.existsSync(entryPath)) {
      throw new Error(
        `Entry file missing: ${spec.entry}\nCreate ${spec.entry} that exports your public API before building.`,
      );
    }

    const cfg = createFesm2022Config(spec);

    try {
      await build(cfg);
      console.log(`Built "${spec.name}" → ${spec.outDir}/${spec.fileName}`);
    } catch (error) {
      console.error(`Build failed for "${spec.name}":`, error);
      return;
    }
  }

  console.log('\nAll builds completed.');
}

try {
  await run();
} catch (error) {
  console.error(error);
}
