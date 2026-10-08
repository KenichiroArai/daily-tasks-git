# AGENTS.md — daily-tasks（フロントエンド）

`frontend/` 配下（Next.js + TypeScript の画面）の作業ガイド。
リポジトリ直下の [AGENTS.md](../AGENTS.md)（プロジェクト全体と Java の収集ツール）とあわせて守る。

## 構成ルール

Java 側の「機能パッケージ + 層」に合わせ、`frontend/src/` も機能（feature）単位で構成する。

```text
frontend/
  docs/                      # フロントエンドの設計書（design.md と design/ 配下の各章）
  scripts/copy-data.mjs      # docs/data を public/data にコピー（dev / build の前に自動実行）
  out/                       # 静的エクスポートの出力（Pages に公開。Git で管理しない）
  src/
    app/                     # ルーティング専用（layout.tsx / page.tsx / not-found.tsx）。ロジックを置かない
    features/
      carryover/             # 機能ごとのフォルダ
        index.ts             # 機能の公開窓口。機能の外からはここだけを import する
        api/                 # データ取得（fetchSummary など）
        model/               # 型と zod スキーマ、表示条件の型
        constants/           # ラベル、色などの定数
        lib/                 # 純粋関数（集計、グラフ用データの作成など）
        hooks/               # 状態管理やデータ読み込みのフック
        components/          # 機能の画面部品
        testing/             # テスト用のデータ（fixtures）
    shared/
      components/ui/         # Panel、Select、Button、Card、DateRange、Swatch などの汎用部品
      components/layout/     # Header、Footer、PageContainer
      lib/                   # assetPath（basePath 付きの URL）、数値の書式化など
      hooks/                 # 汎用フック（useAsync など）
      types/                 # 汎用の型（LoadState など）
    config/site.ts           # サイト名、リポジトリの URL など
    styles/globals.css       # CSS 変数と要素の共通スタイル
```

- 依存の向きは `app` → `features` → `shared` の一方向だけにする。`shared/`・`config/` から `features/` を import しない
- 機能の内部（`@/features/<機能名>/xxx`）を機能の外から import しない。`@/features/<機能名>` の `index.ts` を経由する。同じ機能の中では相対パスを使う
- 上の 2 つは `eslint.config.mjs` の `no-restricted-imports` でチェックする
- 機能の外のファイルは `@/`（`src/` を指す）のパスエイリアスで import する
- コンポーネントは 1 フォルダにまとめる（`Xxx.tsx`、`Xxx.module.css`、`Xxx.test.tsx`、`index.ts`）。スタイルは CSS Modules にし、全体に効くものだけ `styles/globals.css` に置く
- 計算ロジックは `lib/` の純粋関数にしてコンポーネントから切り離し、Vitest でテストする。コンポーネントは Testing Library でテストする。テストファイルは対象と同じフォルダに `*.test.ts(x)` で置く
- `summary.json` は `model/` の zod スキーマで実行時に検証し、形式が想定と違う場合は画面にエラーを表示する
- データ（`docs/data/`）を変更する処理は Java 側だけで行い、フロントエンドは読み取りだけにする
- 静的エクスポートのため、サーバー機能（API Routes、`cookies()` などの動的機能、画像最適化）は使わない
- `public/` のファイルを参照するときは `assetPath()` で basePath（Pages では `/daily-tasks-git`）を付ける
- 新しい画面は `features/<機能名>/` を同じ構成で作り、`app/<ルート>/page.tsx` から呼び出す
- `node_modules/`、`.next/`、`out/`、`public/data/`（`docs/data/` のコピー）は Git で管理しない

## 設計書

- フロントエンド部分の設計書は `frontend/docs/` に Markdown で記載する。入口は `frontend/docs/design.md`、各章は `frontend/docs/design/` に置く
- 設計書は `frontend/src/` の現状の実装を正として記述する
- `frontend/` のコード（`src/`、`scripts/`、設定ファイル）を変更する場合は、設計書をすべて見直し、実装と食い違う箇所を更新する
- 新しい機能や画面を追加した場合は、設計書に章または節を追加し、`design.md` の目次も更新する
- 設計書の整形は Prettier の対象にする（`npm run format:check` で確認する）

## TypeScript のコーディングルール

### 関数の戻り値

リポジトリ直下の AGENTS.md の「メソッドの戻り値」を TypeScript にも適用する。

- 対象: `function` 宣言と、本体が `{ }` のアロー関数（`lib/`・`hooks/`・`api/`・reducer・テスト用の関数、`scripts/` を含む）
  - 関数の戻り値は変数 `result` で定義する
  - 関数の戻り値の変数は先頭で宣言する。ただし、初期値を決められず 1 回だけ代入する場合（例: 例外を投げる検証のあとに値が決まる場合）は、ESLint の `prefer-const` に合わせ、値が決まった位置で `const result = ...;` と宣言する
  - return 文は `return result;` に統一する
  - ガード節（早期リターン）では `result` の初期値を返す
  - `switch` の分岐では `result` に代入して `break` し、最後に `return result;` で返す
- 対象外（そのままでよい）
  - 式だけを返す短いアロー関数（`.map((x) => ...)`、`onChange={() => ...}`、`useMemo(() => ...)` / `useCallback(() => ...)` の中身）
  - React コンポーネントが JSX を返す `return (...)`。表示しない場合の `return null;` や、読み込み中・エラー表示の早期 `return <Panel ... />` も対象外
  - `useEffect` の後片付け関数を返す `return () => { ... };`
- コンポーネントの `return` には描画に必要な最低限だけを書く。値の計算（`lib/` の関数の呼び出し、書式化など）は `return` の前で変数にしておく。一覧の描画のための `.map(...)`、条件による表示の切り替え、イベントハンドラーは JSX の中に書いてよい
- カスタムフック（JSX を返さない関数）は対象にする

```ts
// 望ましくない形式:
export function statValue(stat: Stat | undefined, metric: Metric): number {
  if (!stat) {
    return 0;
  }
  return metric === 'count' ? stat.count : stat.minutes;
}

// 望ましい形式:
export function statValue(stat: Stat | undefined, metric: Metric): number {
  let result = 0;
  if (!stat) {
    return result;
  }
  result = metric === 'count' ? stat.count : stat.minutes;
  return result;
}
```

```tsx
// 望ましくない形式:
return <CarryoverChart title={buildChartTitle(options)} spec={spec} />;

// 望ましい形式:
const chartTitle = buildChartTitle(options);
return <CarryoverChart title={chartTitle} spec={spec} />;
```

## ビルド・テスト

```bash
# frontend/ で実行:
npm install
npm run dev           # 開発サーバー（http://localhost:3000/）
npm run lint          # ESLint（依存の向きのチェックを含む）
npm run typecheck     # TypeScript の型チェック
npm test              # Vitest
npm run build         # 静的エクスポート（frontend/out）
npm run format:check  # Prettier の整形の確認
```

## 変更時のチェックリスト

- [ ] `npm run lint` / `npm run typecheck` / `npm test` / `npm run build` が通り、`npm run dev` で表示と切り替えを確認
- [ ] 構成ルール（機能単位の構成、依存の向き、コンポーネントのフォルダ構成）の順守
- [ ] TypeScript のコーディングルール（戻り値 `result`、コンポーネントの `return` は描画だけ）の順守
- [ ] `frontend/docs/` の設計書をすべて見直して更新

## やってはいけないこと

- `shared/` から `features/` に依存したり、機能の内部を機能の外から直接 import したりすること
- `docs/data/` の JSON をフロントエンドから変更すること（読み取りだけにする）
- サーバー機能（API Routes、動的機能、画像最適化）を使うこと
- コンポーネントの `return` の中で値の計算（`lib/` の関数の呼び出し、書式化など）を行うこと
