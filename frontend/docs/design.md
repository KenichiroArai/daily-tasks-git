# フロントエンド設計書

持ち越しの推移を表示する画面（`frontend/`）の設計書です。
内容は `frontend/src/` の現状の実装を正として記述しています。実装を変更した場合は、関係する章もあわせて更新してください。

## 目的

GitHub Issue（1 日 1 Issue）で管理している日々のタスクのうち、「持ち越し」（旧「繰り越し」「負債」）の件数と残り時間の推移を、グラフと表で確認できるようにします。

- 表示するデータは、Java の収集ツールが出力する `docs/data/summary.json`（日別集計）だけです
- 画面はデータを読み取るだけで、変更はしません
- GitHub Pages に静的サイトとして公開します

## 対象範囲

| 対象                           | 説明                                                     |
| ------------------------------ | -------------------------------------------------------- |
| `frontend/src/`                | 画面のソースコード（ルーティング、機能、共通部品、設定） |
| `frontend/scripts/`            | データのコピー（`docs/data` -> `public/data`）           |
| `frontend/` 直下の設定ファイル | Next.js、TypeScript、ESLint、Prettier、Vitest の設定     |

Java の収集ツール（Issue の取得・解析・集計）は対象外です。リポジトリ直下の [AGENTS.md](../../AGENTS.md) と [README.md](../../README.md) を参照してください。

## 技術スタック

| 分類           | 採用技術                                                        |
| -------------- | --------------------------------------------------------------- |
| フレームワーク | Next.js 16（App Router、`output: 'export'` の静的エクスポート） |
| 言語・UI       | TypeScript 5、React 19                                          |
| グラフ         | Recharts 3                                                      |
| データの検証   | zod 4                                                           |
| スタイル       | CSS Modules、`styles/globals.css`（CSS 変数）                   |
| テスト         | Vitest 5、Testing Library、jsdom                                |
| 静的解析・整形 | ESLint 9（`eslint-config-next`）、Prettier 3                    |
| 実行環境       | Node.js 20.9 以降（CI は 22）                                   |
| 公開           | GitHub Pages（GitHub Actions でデプロイ）                       |

## 設計書の構成

| 章                                         | 内容                                                               |
| ------------------------------------------ | ------------------------------------------------------------------ |
| [01. 全体構成](design/01-architecture.md)  | ディレクトリ構成、依存の向き、静的エクスポートと basePath          |
| [02. データ設計](design/02-data.md)        | データの流れ、`summary.json` のスキーマ、検証とエラー表示          |
| [03. 画面設計](design/03-screen.md)        | 画面構成、表示条件（表示・指標・集計・対象・期間）、URL クエリ     |
| [04. モジュール設計](design/04-modules.md) | 状態管理、フック、`lib/` の純粋関数、コンポーネント、定数          |
| [05. 品質・運用](design/05-quality.md)     | テスト、静的解析、npm スクリプト、ビルドとデプロイ、機能の追加手順 |

## 全体像

```mermaid
flowchart LR
  issues["GitHub Issue"] --> collector["Java 収集ツール"]
  collector --> summaryJson["docs/data/summary.json"]
  summaryJson --> copyData["scripts/copy-data.mjs"]
  copyData --> publicData["frontend/public/data/"]
  publicData --> build["next build（静的エクスポート）"]
  build --> out["frontend/out/"]
  out --> pages["GitHub Pages"]
  pages --> browser["ブラウザ"]
  browser -->|"fetch summary.json"| pages
```

## 関連ドキュメント

- [AGENTS.md](../../AGENTS.md): リポジトリ全体の作業ガイド（フロントエンドの構成ルールを含む）
- [README.md](../../README.md): 使い方とセットアップ
