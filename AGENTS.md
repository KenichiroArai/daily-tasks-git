# AGENTS.md — daily-tasks（基軸）

AI コーディングエージェント向けの作業ガイド。
Cursor / Codex / Claude Code など複数ツールで共通利用する。

本ドキュメントは、daily-tasks の基軸となる本リポジトリ（Issue、ワークフロー、設定ファイル、出力データ）のルールをまとめる。
プログラムの作業では、それぞれのリポジトリの `AGENTS.md` もあわせて守る。

## プロジェクト概要

- **役割**: GitHub Issue（1 日 1 Issue）で管理している日々のタスクのうち、「持ち越し」（旧「繰り越し」「負債」）を集計し、グラフとして GitHub Pages に公開する
- **構成**: 本リポジトリを基軸とし、プログラム部分（KMG シリーズの第 2 弾）を別のリポジトリに分けている

| リポジトリ                                                                                | 役割                                                                                            |
| ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| daily-tasks-git（本リポジトリ）                                                           | 基軸。日々のタスクの Issue、ワークフロー、設定ファイル（`config/`）、出力データ（`docs/data/`） |
| [gr002_daily-tasks](https://github.com/KenichiroArai/gr002_daily-tasks)                   | バックエンド。収集ツール（Java。Issue 取得 → 解析 → `docs/data/` に JSON 出力）                 |
| [mk002_daily-tasks_web-next](https://github.com/KenichiroArai/mk002_daily-tasks_web-next) | フロントエンド。画面（Next.js + TypeScript の静的エクスポート）                                 |
| [kb002_daily-tasks_doc](https://github.com/KenichiroArai/kb002_daily-tasks_doc)           | 設計書と横断的なドキュメント（全体構成、Issue 解析の仕様、共通のコーディングルール）            |

- ローカルでは、4 つのリポジトリを同じフォルダの下に兄弟ディレクトリとして置く。収集ツールと画面は、兄弟の本リポジトリを相対パスで参照する

## ディレクトリ構成

```text
config/
  default-minutes.json       # 時間表記なしの行を補完する項目ごとの標準時間（収集ツールが読む）
docs/                        # 収集ツールが出力するデータだけを置く（ソースコードは置かない）
  data/
    issues/NNNN.json         # Issue ごとの解析結果（4 桁ゼロ埋め）
    summary.json             # 画面用の日別集計
.github/
  ISSUE_TEMPLATE/            # 日々のタスク Issue のテンプレート
  workflows/
    update-carryover.yml     # 収集と Pages デプロイのワークフロー
```

## 収集と公開（ワークフロー）

[.github/workflows/update-carryover.yml](.github/workflows/update-carryover.yml) が収集から公開までを行う。

- トリガー: 毎日 06:00（JST）のスケジュールと、手動実行（`full` 入力で全件モード）。Issue の編集ごとにコミットされないよう、Issue イベントでは動かさない
- 本リポジトリ、gr002_daily-tasks、mk002_daily-tasks_web-next を、ローカルと同じく兄弟ディレクトリとしてチェックアウトする（いずれも public のため、チェックアウト用の追加トークンは不要）
- gr002_daily-tasks を `mvn -B -ntp package` でビルド（テストを含む）し、本リポジトリをカレントにして jar を実行する。依存の kmg-core と kmg-fund は GitHub Packages にあるため、ビルド前に `~/.m2/settings.xml` へサーバ `github-kmg-core` と `github-kmg-fund` の認証を書き、`permissions.packages: read` の `GITHUB_TOKEN` を使う。両パッケージの設定で、本リポジトリに Actions からの読み取りを許可する
- `docs/data/` に差分があれば本リポジトリにコミットしてプッシュする
- mk002_daily-tasks_web-next をビルド（lint・型チェック・テストを含む）し、`out/` を本リポジトリの GitHub Pages にデプロイする。basePath は `actions/configure-pages` の出力を環境変数 `PAGES_BASE_PATH` で渡す
- GitHub Pages の Source は「GitHub Actions」にする
- GitHub API のトークンは環境変数 `GITHUB_TOKEN` から取得する
- gr002・mk002 のリポジトリ名、jar のファイル名、画面の出力先（`out/`）を変えた場合は、ワークフローもあわせて更新する

## 設定ファイルとデータ

- `config/default-minutes.json` は、Issue の行に時間表記がない場合に補完する項目ごとの標準時間（分）。項目を追加・変更したら、全件モードで JSON を作り直す
- `docs/data/` の JSON は収集ツールで生成し、Git で管理する。手で編集しない
- JSON の形式や Issue 解析の仕様は kb002_daily-tasks_doc の [docs/carryover-spec.md](https://github.com/KenichiroArai/kb002_daily-tasks_doc/blob/main/docs/carryover-spec.md) を参照する

## ローカルでの実行

```bash
# 収集（gr002_daily-tasks でビルドした jar を本リポジトリで実行する）
cd ../gr002_daily-tasks
mvn clean package
cd ../daily-tasks-git
java -jar ../gr002_daily-tasks/target/gr002_daily-tasks-0.1.0.jar         # 差分モード
java -jar ../gr002_daily-tasks/target/gr002_daily-tasks-0.1.0.jar --full  # 全件モード

# 画面の確認（本リポジトリの docs/data を使う）
cd ../mk002_daily-tasks_web-next
npm install
npm run dev
```

## 作業ガイド

| 作業                         | 参照するガイド                                                                                                             |
| ---------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| 収集ツール（Java）           | [gr002_daily-tasks の AGENTS.md](https://github.com/KenichiroArai/gr002_daily-tasks/blob/main/AGENTS.md)                   |
| 画面（Next.js + TypeScript） | [mk002_daily-tasks_web-next の AGENTS.md](https://github.com/KenichiroArai/mk002_daily-tasks_web-next/blob/main/AGENTS.md) |
| 設計書・横断的なドキュメント | [kb002_daily-tasks_doc の AGENTS.md](https://github.com/KenichiroArai/kb002_daily-tasks_doc/blob/main/AGENTS.md)           |

## 変更時のチェックリスト

- [ ] ワークフローを変更した場合、チェックアウトするリポジトリ・パス、jar のファイル名、Pages の成果物のパスが gr002・mk002 と一致すること
- [ ] `config/default-minutes.json` を変更した場合、全件モードで JSON を作り直したこと
- [ ] リポジトリの構成やワークフローを変更した場合、kb002_daily-tasks_doc の `docs/overview.md` を更新したこと
- [ ] README の更新

## やってはいけないこと

- 本リポジトリにプログラムのソースコード（Java、画面）や設計書を置くこと（gr002・mk002・kb002 に置く）
- `docs/data/` の JSON を手で編集すること（必ず収集ツールで生成する）
- `docs/` にソースコード（HTML / JS / CSS など）を置くこと
- シークレット（`GITHUB_TOKEN` など）をコード・ログ・JSON・ワークフローのログに出すこと
- Issue イベントでワークフローを動かすこと（Issue の編集ごとにコミットされるため）

## 参考リンク

- README: `./README.md`
- 全体構成: `https://github.com/KenichiroArai/kb002_daily-tasks_doc/blob/main/docs/overview.md`
- GitHub REST API（Issues）: `https://docs.github.com/rest/issues/issues`

## 順守

以上の内容を順守し、タスクを遂行してください。
