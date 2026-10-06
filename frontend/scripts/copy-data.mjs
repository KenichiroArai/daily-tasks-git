// docs/data（Java の収集ツールの出力）を public/data にコピーする
import { cp, rm, stat } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const frontendDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const sourceDir = path.resolve(frontendDir, '..', 'docs', 'data');
const targetDir = path.resolve(frontendDir, 'public', 'data');

try {
  await stat(sourceDir);
} catch {
  console.error(`データのディレクトリが見つかりません: ${sourceDir}`);
  process.exit(1);
}

await rm(targetDir, { recursive: true, force: true });
await cp(sourceDir, targetDir, { recursive: true });
console.log(`データをコピーしました: ${sourceDir} -> ${targetDir}`);
