/* eslint-disable unicorn/prevent-abbreviations */
import { promises as fs } from 'node:fs';
import path from 'node:path';

const srcPath = path.resolve(process.cwd(), 'dist/ff-doc/angular');
const destPath = path.resolve(process.cwd(), 'dist/ff-doc');
const files = ['.npmignore', 'README.md', 'package.json'];

async function run() {
  const copyPromises = files.map(async (file) => {
    const src = path.join(srcPath, file);
    const dest = path.join(destPath, file);

    try {
      await fs.copyFile(src, dest);
      await fs.unlink(src);
      return { file, ok: true };
    } catch {
      return { file, ok: false };
    }
  });

  const results = await Promise.all(copyPromises);
  const failed = results.filter((result) => !result.ok);
  if (failed.length > 0) {
    throw new Error(
      `\nCompleted with errors. Failed to copy ${failed.length} file(s): ${failed.map((f) => f.file).join(', ')}`,
    );
  }

  console.log('\nAll files copied successfully.');
}

try {
  await run();
} catch (error) {
  console.error(error);
}
