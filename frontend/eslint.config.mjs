import { defineConfig, globalIgnores } from 'eslint/config';
import nextVitals from 'eslint-config-next/core-web-vitals';
import nextTs from 'eslint-config-next/typescript';

// 依存の向きは app -> features -> shared の一方向だけを許可する
const noAppImport = {
  group: ['@/app', '@/app/*'],
  message: 'app/ はルーティング専用です。app/ 以外から import しないでください。',
};
const noFeatureImport = {
  group: ['@/features', '@/features/*'],
  message: 'shared/・config/ から features/ に依存しないでください。',
};
const noFeatureInternalImport = {
  group: ['@/features/*/*'],
  message:
    '機能の内部を直接 import せず、features/<機能名>/index.ts を経由してください（同じ機能の中では相対パスを使います）。',
};

export default defineConfig([
  ...nextVitals,
  ...nextTs,
  {
    files: ['src/app/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': ['error', { patterns: [noFeatureInternalImport] }],
    },
  },
  {
    files: ['src/features/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': ['error', { patterns: [noAppImport, noFeatureInternalImport] }],
    },
  },
  {
    files: ['src/shared/**/*.{ts,tsx}', 'src/config/**/*.{ts,tsx}'],
    rules: {
      'no-restricted-imports': ['error', { patterns: [noAppImport, noFeatureImport] }],
    },
  },
  globalIgnores(['.next/**', 'out/**', 'public/data/**', 'coverage/**', 'next-env.d.ts']),
]);
