# daily-tasks

日々のタスクを GitHub Issue（1 日 1 Issue）で管理しています。このリポジトリでは、その Issue の「持ち越し」（旧「繰り越し」「負債」）を集計し、グラフとして GitHub Pages に公開します。

- 公開ページ: <https://kenichiroarai.github.io/daily-tasks-git/>
- データ: [`docs/data/summary.json`](docs/data/summary.json)（日別の集計）、[`docs/data/issues/`](docs/data/issues/)（Issue ごとの解析結果）

## 画面でできること

| 切り替え | 選択肢 |
| --- | --- |
| 表示 | 日別推移（全体／項目ごと）、月ごと（全体／項目ごと）、項目ごと、持ち越し元の月ごと |
| 指標 | 件数（残）、残り時間（分）、残り時間（時間） |
| 集計 | 平均、合計、最大、最小、期間末の値（月ごと・項目ごと・持ち越し元の月ごとで使う） |
| 対象 | すべて、未チェックのみ、チェック済みのみ |
| 期間・項目 | 開始日と終了日（全期間、直近 30 日、直近 90 日）、項目の選択 |

画面には、最新の残（件数・残り時間）、期間平均、最大、最小のカードと、項目ごとの集計表（最新・平均・最大・最小・合計）も表示します。

URL パラメータで初期表示を指定できます（例: `?view=monthlyItem&metric=hours&agg=avg&target=unchecked`）。値は `view`（daily / dailyItem / monthly / monthlyItem / item / originMonth）、`metric`（count / minutes / hours）、`agg`（avg / sum / max / min / last）、`target`（all / unchecked / checked）です。

## 集計のルール

- 日付は Issue タイトルの `YYYY年MM月DD日` を使い、その日の残として扱います。
- 対象セクションは `## 負債`（#175〜#229）、`## 繰り越し`（#230〜#350）、`## 持ち越し`（#351〜）です。
- 未チェック（`- [ ]`）とチェック済み（`- [x]`）の両方を数えます。
- 残り時間は括弧内の「残り時間：N分」「N分」「残りN分」「先行分残りN分」から取ります（小数も可）。
- 時間表記がない行は [`config/default-minutes.json`](config/default-minutes.json) の項目ごとの標準時間で補完します。
- 本文の「残：N」は検証に使います。解析した件数と合わない場合は、実行ログに警告を出します。
- 「負債」が初めて出てきた Issue（#175、2026-03-24）以降を集計します。

## 構成

- 収集ツール（Java 25 / Maven）: `src/main/java/io/github/kenichiroarai/dailytasks/`
  - GitHub REST API で全 Issue を取得し、解析して `docs/data/` に JSON を出力します。
- 画面（Next.js + TypeScript + Recharts）: [`frontend/`](frontend/)
  - 静的エクスポート（`frontend/out`）を GitHub Pages に公開します。
  - ビルド時に `docs/data/` を `frontend/public/data/` にコピーして使います。`docs/` には収集ツールが出力するデータだけを置きます。
  - 設計書: [`frontend/docs/design.md`](frontend/docs/design.md)（全体構成、データ、画面、モジュール、品質・運用。各章は `frontend/docs/design/` にあります）
- 自動更新: [`.github/workflows/update-carryover.yml`](.github/workflows/update-carryover.yml)
  - 毎日 06:00（JST）、手動実行、Issue の作成・編集・クローズ・再オープン、`main` への push で動きます。
  - JSON の差分を commit し、画面をビルドしてから GitHub Pages にデプロイします。

## ローカルでの実行

収集ツールには Java 25 と Maven、画面には Node.js（20.9 以降）が必要です。

```bash
# テスト（JaCoCo 行・分岐 100% チェック）と実行可能 jar の作成
mvn clean package

# 収集（差分モード）: 最新 10 件の Issue は毎回解析し直し、それより前は保存済みの JSON と更新日時が同じなら解析しない
java -jar target/daily-tasks-0.1.0.jar

# 収集（全件モード）: #1 から最新まで解析し直す
java -jar target/daily-tasks-0.1.0.jar --full

# 画面の確認（http://localhost:3000/）
cd frontend
npm install
npm run dev

# 画面の lint・型チェック・テスト・ビルド（frontend/ で実行）
npm run lint
npm run typecheck
npm test
npm run build
```

- 環境変数 `GITHUB_TOKEN` を設定すると GitHub API の認証に使います（未設定でも公開リポジトリなら動きます）。
- 解析ルールや標準時間を変えたときは、全件モードで JSON を作り直してください。

## 初回のセットアップ（GitHub Pages）

リポジトリの Settings → Pages → Build and deployment の Source を「GitHub Actions」にしてください。その後、Actions の「持ち越しの集計と公開」を手動実行すると公開されます。

## Eclipse

「ファイル」→「インポート」→「Maven」→「既存 Maven プロジェクト」でリポジトリのルートを選びます。Java 25 に対応した Eclipse（2025-09 以降）と JDK 25 が必要です。
