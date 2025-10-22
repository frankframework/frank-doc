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
    tsconfigPath: 'scripts/tsconfig.es-lib.json',
    fileName: `${baseFileName}.mjs`,
    target,
  },
  {
    name: 'react',
    entry: 'projects/ff-doc/src/react-api.ts',
    outDir: 'dist/ff-doc/react',
    tsconfigPath: 'scripts/tsconfig.react-lib.json',
    fileName: `${baseFileName}.mjs`,
    target,
    additionalExternal: ['react'],
  },
];

function createFesm2022Config(options) {
  const entry = pathFromRoot(options.entry);
  // eslint-disable-next-line unicorn/prevent-abbreviations
  const outDir = pathFromRoot(options.outDir);
  const tsconfigPath = pathFromRoot(options.tsconfigPath);
  const fileName = options.fileName;
  const target = options.target ?? 'es2022';
  const additionalExternal = options.additionalExternal ?? [];

  return defineConfig({
    build: {
      lib: {
        entry,
        formats: ['es'],
      },
      outDir,
      target,
      sourcemap: true,
      emptyOutDir: true,
      minify: false,
      assetsInlineLimit: 0,
      rollupOptions: {
        external: additionalExternal,
        output: {
          entryFileNames: fileName,
        },
      },
    },
    plugins: [
      dts({
        tsconfigPath,
        rollupTypes: true,
      }),
    ],
  });
}

async function run() {
  for (const spec of entries) {
    console.log(`\nBuilding "${spec.name}" → ${spec.outDir}`);

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

function pathFromRoot(relativePath) {
  return path.resolve(process.cwd(), relativePath);
}

try {
  await run();
} catch (error) {
  console.error(error);
}
