import type { NextConfig } from 'next';

// GitHub Pages のプロジェクトサイトでは /<リポジトリ名> 配下で配信されるため、ワークフローから渡す
const basePath = process.env.PAGES_BASE_PATH ?? '';

const nextConfig: NextConfig = {
  output: 'export',
  basePath,
  trailingSlash: true,
  // エージェント向けのルールはリポジトリ直下の AGENTS.md にまとめる
  agentRules: false,
  images: {
    unoptimized: true,
  },
  env: {
    NEXT_PUBLIC_BASE_PATH: basePath,
  },
};

export default nextConfig;
