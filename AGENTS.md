# AGENTS.md — daily-tasks

AI コーディングエージェント向けの作業ガイド。
Cursor / Codex / Claude Code など複数ツールで共通利用する。

本ドキュメントはプロジェクト全体の共通事項と、Java の収集ツールのルールをまとめる。
`frontend/` 配下（画面）の作業では、[frontend/AGENTS.md](frontend/AGENTS.md) もあわせて守る。

## プロジェクト概要

- **役割**: GitHub Issue（1 日 1 Issue）で管理している日々のタスクのうち、「持ち越し」（旧「繰り越し」「負債」）を集計し、グラフとして GitHub Pages に公開する
- **構成**: Java の収集ツール（Issue 取得 → 解析 → `docs/data/` に JSON 出力、Maven プロジェクト）と、`frontend/` 配下の画面（Next.js + TypeScript の静的エクスポート）を 1 つのリポジトリで管理する
- **GAV**: `io.github.kenichiroarai` : `daily-tasks`
- **ルートパッケージ**: `io.github.kenichiroarai.dailytasks`
- **対象リポジトリ**: `https://github.com/KenichiroArai/daily-tasks-git/`（Issue #1 から最新まで）

## 技術スタック

- 言語: Java 25（Temurin）
- フレームワーク: Spring Boot 4.x（`spring-boot-starter-parent`。DI・設定ファイルのバインド・メッセージ・ログ。Web サーバーは起動しない CLI アプリ）
- ビルド / パッケージ管理: Maven（spring-boot-maven-plugin で実行可能 jar を作成）
- ライブラリ: Jackson 3（JSON。パッケージは `tools.jackson.*`、アノテーションは `com.fasterxml.jackson.annotation`）、SLF4J + Logback（ログ）
- HTTP: 標準の `java.net.http.HttpClient`（GitHub REST API）
- フロントエンド: Next.js（App Router、`output: 'export'` の静的エクスポート）+ TypeScript + React + Recharts、zod（JSON の検証）、CSS Modules。Node.js 20.9 以降（CI は 22）
- 公開: GitHub Pages（GitHub Actions でデプロイ）
- テスト: JUnit 5 / JaCoCo（行・分岐 100%）/ ArchUnit（層間のルールの検査）。フロントエンドは Vitest + Testing Library、ESLint、Prettier
- 外部の社内基盤ライブラリ（kmg-core / kmg-fund など）には依存しない

## ディレクトリ構成

```text
pom.xml
src/main/java/io/github/kenichiroarai/dailytasks/
  DailyTasksApplication.java # 起動クラス（Spring Boot を起動するだけ）
  carryover/                 # 機能パッケージ（持ち越しの収集・集計）
    presentation/            # CLI などの入出力層
      command/               # コマンドのインタフェース（CommandLineRunner を継承）
      command/impl/          # コマンドの実装（run で引数の解析、設定値の作成）
      config/                # 設定ファイルの値（@ConfigurationProperties の CarryoverProperties）
      model/                 # 引数を解析したオプション（CarryoverOptions）、application に引き継ぐ設定（CarryoverSettings）
      service/               # application に求めるサービス（CarryoverService。実装は application）
    application/             # ユースケースや業務ロジック層
      service/impl/          # presentation のサービスの実装（収集・解析・集計の流れ）
      logic/                 # domain に求めるロジック（データの取得・保存 CarryoverIssueLogic、解析 CarryoverParseLogic、集計 CarryoverAggregateLogic。実装は domain）
      model/                 # 業務モデル（Issue、持ち越し項目、日別集計、標準時間、domain に引き継ぐ設定 CarryoverSource）
      types/                 # 列挙型（残り時間の取得元 MinutesSource）
    domain/                  # 共通ロジック層
      logic/                 # application のモデルと domain の DTO の変換（CarryoverDtoConvertLogic）
      logic/impl/            # application のロジックの実装（データの取得・保存、Issue 本文の解析ルール、日別の集計）
      repository/api/        # repository に求める外部 API（GitHubIssueApi。実装は repository/api）
      repository/dao/        # repository に求める DAO（JSON ファイル CarryoverDataDao、標準時間 DefaultMinutesDao。実装は repository/dao）
      dto/                   # repository と受け渡す DTO（解析結果、集計、GitHub の Issue、接続設定 GitHubSettingsDto）
    infrastructure/          # 業務を知らない汎用ユーティリティ
      logic/                 # メッセージの取得のインタフェース（MessageLogic）
      logic/impl/            # メッセージの取得の実装（Spring の MessageSource を使う MessageLogicImpl）
    repository/              # データアクセス層
      api/github/            # 外部 API の実装（GitHub REST API からの Issue 取得 GitHubIssueApiImpl）
      dao/carryoverdata/     # DAO の実装（持ち越しの JSON ファイルの読み書き CarryoverDataDaoImpl）
      dao/defaultminutes/    # DAO の実装（標準時間の設定ファイルの読み込み DefaultMinutesDaoImpl）
      dto/json/              # JSON ファイルの形（Jackson のアノテーションを持つのはここだけ）
      logic/                 # domain の DTO と JSON の形の変換（CarryoverJsonConvertLogic）
src/main/resources/
  application.properties     # 設定値（carryover.*: 対象リポジトリ、トークン、出力先、標準時間のファイル、最新の件数。spring.*: Spring Boot の設定）
  messages.properties        # 文字列（使い方、ログ・例外のメッセージ）
  logback-spring.xml
src/test/java/io/github/kenichiroarai/dailytasks/  # main と同じ構成
  testutil/                  # テスト用のユーティリティ（ログ取得、リフレクション、メッセージの取得の生成など）
src/test/resources/          # テスト用のリソース
config/
  default-minutes.json       # 時間表記なしの行を補完する項目ごとの標準時間
docs/                        # 収集ツールが出力するデータだけを置く（ソースコードは置かない）
  data/
    issues/NNNN.json         # Issue ごとの解析結果（4 桁ゼロ埋め）
    summary.json             # 画面用の日別集計
frontend/                    # 画面（Next.js + TypeScript）。詳細は frontend/AGENTS.md
  AGENTS.md                  # フロントエンドの作業ガイド
  docs/                      # フロントエンドの設計書
  src/                       # 画面のソースコード
.github/
  ISSUE_TEMPLATE/            # 日々のタスク Issue のテンプレート
  workflows/                 # 収集と Pages デプロイのワークフロー
```

## パッケージ構成のルール

- `io.github.kenichiroarai.dailytasks` の直下に機能パッケージ（例: `carryover`）を置く
- 各機能パッケージは次の 5 層で構成する

| 層 | 役割 |
| --- | --- |
| `presentation/` | CLI などの入出力層（command など） |
| `application/` | ユースケースや業務ロジック層（service、logic、業務モデルなど） |
| `domain/` | 共通ロジック層（application のロジックの実装。データの取得・保存、解析、集計、変換など） |
| `infrastructure/` | 業務やドメインを知らない、他の層に依存しない汎用のユーティリティ（メッセージの取得など） |
| `repository/` | 外部 API（GitHub API など）とファイル・DB へのアクセス（DAO）のデータアクセス層 |

- 新しい機能は `carryover/` と同じ構成で追加する
- 各層の処理を受け持つクラスは、後述の「サービスとロジック」に従って `service/` か `logic/` にまとめる。データ系の型は後述の「データ系の型」に従って `model/`、`dto/`、`types/` にまとめる。データアクセスは後述の「外部 API と DAO」に従って `api/` か `dao/` にまとめる
- `command/`、`config/` のように、フレームワーク（Spring など）に固有のものや、各層で使わない種類のものは、役割の分かるパッケージ名でそのまま置く
- 中身のないパッケージには、Git で消えないように Javadoc 付きの `package-info.java` を置く
- テストのパッケージは main と同じ構成にする

### データ系の型

| 種類 | パッケージ | 置く層 | 内容 |
| --- | --- | --- | --- |
| モデル | `model/` | repository 以外（presentation、application） | 層の中や層の間で受け渡すデータ（例: `CarryoverSettings`、`CarryoverIssue`） |
| DTO | `dto/` | repository とやり取りするもの（domain の `dto/`）と repository の内部（repository の `dto/`） | repository との受け渡しやファイルの形のデータ（例: `CarryoverIssueDto`、`CarryoverIssueJson`） |
| 列挙型 | `types/` | 各層 | 値の種類を表す列挙型（例: `MinutesSource`） |
| 設定 | `config/` | presentation | フレームワークの設定（例: `@ConfigurationProperties` の `CarryoverProperties`） |

- domain の `dto/` は、domain が repository に求めるインタフェースの受け渡しの型として domain に置く（呼ぶ側の層が型を持つため）
- repository の `dto/` には、JSON のように決まった形のものをサブパッケージ（例: `dto/json/`）に分けて置く。決まった形でないものは `dto/` の直下に置く
- 列挙型は `model/` や `dto/` に混ぜず、その層の `types/` に置く

### 外部 API と DAO

repository 層のデータアクセスは、取得元によって次の 2 つに分ける。

| 種類 | インタフェース（domain） | 実装（repository） | 名前 | 内容 |
| --- | --- | --- | --- | --- |
| 外部 API | `domain/repository/api/` | `repository/api/<提供元>/` | `XxxApi` / `XxxApiImpl` | GitHub API などの外部のサービス（例: `repository/api/github/GitHubIssueApiImpl`） |
| DAO | `domain/repository/dao/` | `repository/dao/<用途>/` | `XxxDao` / `XxxDaoImpl` | ファイル（JSON など）や DB（例: `repository/dao/carryoverdata/CarryoverDataDaoImpl`） |

- `<提供元>` は API の提供元ごと（例: `github`）、`<用途>` はデータの用途ごと（例: `carryoverdata`、`defaultminutes`）のパッケージにする
- DB へのアクセスは今はないが、追加する場合もファイルと同じく DAO として `repository/dao/<用途>/` に置く
- 実装には `@Repository` を付ける

### サービスとロジック

| 種類 | パッケージ | 名前 | Bean | 内容 |
| --- | --- | --- | --- | --- |
| サービス | `service/` | `XxxService` / `XxxServiceImpl` | `@Service` | 一連の流れを行う |
| ロジック | `logic/` | `XxxLogic` / `XxxLogicImpl` | `@Component` | 個々の処理を行い、何度も呼ばれる |

- **サービス**: サービス・ロジック・ユーティリティ・モデルなどを使い、一連の流れを行う。初期化、主処理、サブ処理、終了処理のように、処理（メソッド）の流れがはっきりしている（例: `CarryoverServiceImpl#collect` は取得、解析、保存、集計を順に行う）
- **ロジック**: ロジック・ユーティリティ・モデルなどを使うが、サービスは呼ばない。メソッドごとに論理のまとまりになっており、各メソッドは基本的に互いに依存せず、サービス・ロジック・ユーティリティなどから何度も呼ばれる（例: get、read、write、exists、parse、aggregate、変換）。インスタンス変数を共有する点がユーティリティとは異なる
- インタフェースと実装に分ける場合は、ほかの型と同じく、インタフェースを呼ぶ側の層の `logic/`、実装を呼ばれる側の層の `logic/impl/` に置く。自層の中だけで使う部品（変換など）は、インタフェースを作らずに `logic/` に具象クラスを置いてよい
- 名前に `Parser`、`Aggregator`、`Converter`、`Provider` などは使わず、`XxxLogic`（例: `CarryoverParseLogic`、`CarryoverDtoConvertLogic`、`MessageLogic`）とする

## 層間のルール

依存性逆転の原則（DIP）に従う。目的は、各層（infrastructure を除く）を独立させ、下の層を別の実装に差し替えやすくすることである。例えば repository を JSON ファイルから DB に切り替えても、domain から上の層は変更しない。

処理の流れ（呼び出し）は上から下へ一本で追えるようにし、依存（import）はその逆向きにする。参照できる先を制限し、飛び越しをさせない。

処理の流れ:

```mermaid
flowchart LR
    entryPoint[DailyTasksApplication] -.->|起動| presentation
    presentation -->|呼び出し| application
    application -->|呼び出し| domain
    domain -->|呼び出し| repository
```

依存の向き（import）:

```mermaid
flowchart LR
    repository -->|"domain のインタフェースを実装"| domain
    domain -->|"application のインタフェースを実装"| application
    application -->|"presentation のインタフェースを実装"| presentation
    infrastructure["infrastructure（全層から利用可）"]
```

### 参照の向き

- 依存（import）は処理の流れの逆向きに「すぐ上の層」だけにする。処理の流れと同じ向きの参照と飛び越しは禁止する
  - 許可: application → presentation、domain → application、repository → domain
  - 禁止の例: presentation → application、application → domain、domain → repository、repository → application、domain → presentation、エントリポイント → presentation
- infrastructure は全層から参照してよい。infrastructure から他の層は参照しない
- infrastructure には業務やドメインを知らない汎用のユーティリティだけを置く。機能固有の値（対象リポジトリ、出力先など）や業務の判断は置かない

### 公開するものと実装

- 呼ぶ側の層は、下の層に求めるインタフェースと受け渡しの型を、自層の種類ごとのパッケージに置く（モデルは `model/`、サービスは `service/`、ロジックは `logic/` など）
  - presentation: application に求めるサービス（`service/CarryoverService`）と設定（`model/CarryoverSettings`）
  - application: domain に求めるロジック（データの取得・保存 `logic/CarryoverIssueLogic`、解析 `logic/CarryoverParseLogic`、集計 `logic/CarryoverAggregateLogic`）と業務モデル（`model/`）
  - domain: repository に求める外部 API（`repository/api/GitHubIssueApi`）と DAO（`repository/dao/CarryoverDataDao`、`repository/dao/DefaultMinutesDao`）と DTO（`dto/`）
- 呼ばれる側の層は、すぐ上の層のインタフェースを `impl/` で実装する（例: `application/service/impl/CarryoverServiceImpl` は `presentation/service/CarryoverService` を実装する）
- 依存する側（下の層）が何を参照するかは制御しない。下の層は、すぐ上の層のどのクラス（`impl/` を含む）を参照してもよい。制限するのは層の向きと飛び越しだけとする
- 下の層を差し替えるときは、上の層のインタフェースを実装した別の `impl/` を用意する。上の層のコードは変更しない

### DI（依存性の注入）

- 実装クラスは Spring の Bean として登録し、生成と組み立ては Spring に任せる。自分で `new` して下の層の実装を生成しない
  - presentation のコマンド・各層のロジック（`logic/`、`logic/impl/`）: `@Component`
  - サービス（`service/impl/`）: `@Service`
  - repository の外部 API と DAO の実装: `@Repository`
- 注入はコンストラクタインジェクションだけにする。フィールドインジェクション（フィールドへの `@Autowired`）は使わない
- 受け取るのは「自層のインタフェース」（と自層の部品、infrastructure のインタフェース、`JsonMapper` などのフレームワークの部品）とする。実装は Spring が下の層から注入する
- コンストラクタが 1 つなら `@Autowired` は付けない。テスト用のコンストラクタ（出力先や `HttpClient` の差し替えなど）を別に持つ場合だけ、本番用のコンストラクタに `@Autowired` を付ける
- Bean はステートレス（シングルトンで状態を持たない）にする。設定値はフィールドに持たず、メソッドの引数で渡す
- コマンドは `CommandLineRunner` として Spring Boot の起動後に実行する。起動クラスは `SpringApplication.run` を呼ぶだけとする

### データの受け渡しと変換

- 境界で受け渡すデータ型は、呼ぶ側の層が持つもの（`model/`、`dto/`）を使う。変換は呼ぶ側の層で、自層の型に行ってから渡す。呼ばれる側の層は、受け取った型を必要に応じて自層の内部の型に変換する
  - presentation は設定ファイルとコマンドライン引数の値を `presentation/model/CarryoverSettings` に変換し、application からは件数などの結果だけを受け取る
  - application は業務モデル（`application/model/`）を使って処理の流れを組み立てる。domain はこのモデルを使って解析・集計する
  - domain は application のモデルと自層の DTO（`domain/dto/`）を相互に変換し（`domain/logic/CarryoverDtoConvertLogic`）、DTO で repository とやり取りする
  - repository は domain の DTO を入出力に使い、JSON の形の型（`repository/dto/json/`）との変換を自層で行う（`repository/logic/CarryoverJsonConvertLogic`）
- JSON の形（Jackson のアノテーション）は `repository/dto/json/` だけが持つ。domain の DTO には付けない
- GitHub API の応答のように、JSON の形の型を作らずに読み取れるものは、外部 API の実装が domain の DTO を直接作ってよい

### 設定値の引き継ぎ

- 設定値も業務データと同じく、上の層から下の層へ引き継ぎ、境界ごとに呼ぶ側の層の型に変換して渡す。どこからでも参照できる共有の設定（infrastructure の設定クラスなど）は作らない
- 値の出どころは入力の窓口である presentation（コマンドライン引数・設定ファイル `src/main/resources/application.properties`）とする。設定ファイルは presentation だけが読み込む
  - 設定ファイルの `carryover.*` は `presentation/config/CarryoverProperties`（`@ConfigurationProperties`、コンストラクタでバインド）で受け取る。`@ConfigurationProperties` のクラスは presentation にだけ置く
  - トークンは `carryover.token=${GITHUB_TOKEN:}` として環境変数 `GITHUB_TOKEN` から設定する
  - `@Value` で設定値を個別に取得しない。`Environment` を各層から参照しない
  - presentation → application: `presentation/model/CarryoverSettings`
  - application → domain: `application/model/CarryoverSource`
  - domain → repository: `domain/dto/GitHubSettingsDto`、ファイルのパス（`Path`）
- GitHub API のベース URL のような、業務の設定ではない技術的な固定値は、それを使う外部 API や DAO の実装の定数にする
- トークンは設定型の `toString` に含めない

### 文字列（メッセージ）の管理

- 使い方、ログ・例外のメッセージは `src/main/resources/messages.properties` に置き、各クラスはキーを定数で持って、コンストラクタで注入した `infrastructure/logic/MessageLogic` で取得する
- `MessageLogic` は Spring の `MessageSource`（`spring.messages.*` で設定）を引数なしで使うため、`{}` や `%s` はそのまま返る
- ログは SLF4J の `{}`、例外は `String.format` の `%s` / `%d` を埋め込み位置に使う。取得したメッセージはいったん変数に入れてから渡す
- 解析ルールそのものの文字列（対象セクション名、正規表現など）は messages.properties に置かず、それを使う実装の定数にする

### ルールの検査

- 層間のルール（依存はすぐ上の層だけ。飛び越しと処理の流れと同じ向きの参照の禁止）、フィールドインジェクションの禁止、`@Value` の禁止、`@ConfigurationProperties` を presentation にだけ置くこと、Jackson のアノテーションを `repository/dto/json/` にだけ置くこと、ロジック（`logic/`）がサービス（`service/`）を参照しないことは `ArchitectureTest`（ArchUnit）で検査する。違反があると `mvn test` は失敗する

## フロントエンド

フロントエンドの構成ルール、設計書の扱い、TypeScript のコーディングルール、ビルド・テストのコマンドは [frontend/AGENTS.md](frontend/AGENTS.md) に記載する。

## Issue 解析の仕様

- 日付は Issue タイトルの `YYYY年MM月DD日` から取得し、「その日の残」の日付とする
- 対象セクションは `## 負債`（#175〜#229）、`## 繰り越し`（#230〜#350）、`## 持ち越し`（#351〜）。次の `##` までを対象とする
- `## ルーティン`、`## 汎用タスク`、`## 追加`、`## 先行` は対象外
- 未チェック（`- [ ]`）・チェック済み（`- [x]`）の両方を対象とし、`checked` フラグで区別する
- 行の形式は `項目名YYYY/MM/DD（時間表記）`。時間表記は次のいずれか
  - `残り時間：N分`
  - `N分`
  - `残りN分`
  - `先行分残りN分`
  - 時間表記なし → `config/default-minutes.json` の標準時間で補完し、`minutesSource` を `default` とする
- N は小数（例: `8.5`）を許容する
- 本文の `残：N` は検証用に `declaredCount` として保存し、解析件数と食い違う場合は警告ログを出す
- 項目名は正規化する（末尾の `(` や `（15分）` などの混入を除去）

## 収集と公開

- Issue の一覧は毎回すべて取得する（100 件 / ページ）。解析・保存するかどうかはモードで決める
- 差分モード（既定）: 保存済みの JSON（`docs/data/issues/NNNN.json` の `updatedAt`）を「どこまで処理したか」の記録として使う。Issue 番号が大きい順の最新 10 件（10 日分）は `updatedAt` に関係なく毎回解析し直して上書きする。それより前の Issue は、未取得の Issue と更新された Issue（編集やクローズを含む）だけを解析・保存する
- 全件モード（`--full`）: #1 から最新まで解析し直す。解析ルールや標準時間を変えた場合はこちらを使う
- 集計（`summary.json`）は、対象セクションが最初に現れた Issue（#175）以降を毎回すべて作り直す
- 出力した `docs/data/` 配下の JSON は Git で管理する
- GitHub Actions（`.github/workflows/update-carryover.yml`）は毎日のスケジュール、手動実行（`full` 入力あり）、`main` への push で動き（Issue の編集ごとにコミットされないよう、Issue イベントでは動かさない）、差分を commit したあと `frontend/` をビルド（lint・型チェック・テストを含む）し、`frontend/out` を Pages にデプロイする。basePath は `actions/configure-pages` の出力を環境変数 `PAGES_BASE_PATH` で渡す
- GitHub Pages の Source は「GitHub Actions」にする
- GitHub API のトークンは環境変数 `GITHUB_TOKEN` から取得する

## ビルド・テスト

```bash
# テスト（JaCoCo 100% チェック）+ 実行可能 jar の作成:
mvn clean package

# テストのみ:
mvn test

# 収集の実行（差分モード / 全件モード）:
java -jar target/daily-tasks-0.1.0.jar
java -jar target/daily-tasks-0.1.0.jar --full
```

- 画面のビルド・テストのコマンドは [frontend/AGENTS.md](frontend/AGENTS.md) を参照する

- カバレッジレポート: `target/site/jacoco/index.html`
- 行 / 分岐カバレッジが 100% 未満だと `mvn test` は失敗する

## Eclipse

- 「ファイル」→「インポート」→「Maven」→「既存 Maven プロジェクト」でリポジトリのルート（`pom.xml`）を読み込む
- Java 25 に対応した Eclipse（2025-09 以降）を使い、JDK 25 をインストール済み JRE に登録する
- `.classpath`、`.project`、`.settings/`、`bin/` は Git で管理しない。`pom.xml` を変更したら「Maven」→「プロジェクトの更新」で反映する

## 作業時の原則

- Issue 本文のフォーマットの揺れに強い解析にする（想定外の行は落とさずに警告ログを出す）
- 既存の JSON の形式を変える場合は、全件モードで再生成できることを確認する
- 本ドキュメントのパッケージ構成ルール・コーディングルール・テストルール・Javadoc ルールに従う
- `target/` はビルド生成物であり、Git で管理しない
- `frontend/node_modules/`、`frontend/.next/`、`frontend/out/`、`frontend/public/data/`（`docs/data/` のコピー）も Git で管理しない

## Java のコーディングルール

### メソッドの戻り値

- メソッドの戻り値は変数 `result` で定義する
- メソッドの戻り値の変数は先頭で宣言する
- return 文は `return result;` に統一する
- このルールは TypeScript（`frontend/`）にも適用する。React コンポーネントの JSX など対象外とするものは [frontend/AGENTS.md](frontend/AGENTS.md) の「TypeScript のコーディングルール」を参照する

### 処理コメント

- 機能ごと、処理のまとまり単位に `/* コメント */` で記載する
- 通常コメントは `//` で記載する

### Javadoc

- 修飾子に限らず必須
- 後述の「Javadoc のフォーマットルール」に従う

### アクセス修飾子

- 外部に公開しないもの、サブクラスも使わないもの（フィールド・メソッド・コンストラクタ・入れ子クラス）は `private` にする
- 修飾子なし（パッケージプライベート）は原則として禁止する。enum のコンストラクタも `private` を明示する
- テストから呼び出すためだけにパッケージプライベートにしない。テストでは `testutil/ReflectionTestUtil`（標準のリフレクション）で private のメソッド・フィールドにアクセスする
- 例外: JUnit の `@TempDir` フィールドなど、フレームワークの制約で `private` にできないもの。インタフェースのメソッドは暗黙的に `public` なので修飾子を付けなくてよい

### record の禁止

- `record` は使用しない。DTO や値オブジェクトも通常の `class` で定義する
- 理由: アクセサ・コンストラクタ・`equals` / `hashCode` / `toString` が自動生成されソース上に行が存在しないため、ブレークポイントを設定できず、どこで参照されたかのトレースやデバッグができない
- 代わりに次の形で実装する
  - フィールドは `private`（変更不要なら `final`）で定義する
  - 値はコンストラクタ（またはセッター）で設定する
  - 値の取得は `getXxx()` 形式のゲッターを明示的に定義する（JSON 変換時もゲッターが呼ばれるため、ブレークポイントで参照箇所を追える）
  - `equals` / `hashCode` / `toString` が必要な場合は明示的に実装する

```java
// 望ましくない形式:
public record CarryoverItem(String name) {
}

// 望ましい形式:
public class CarryoverItem {

    private final String name;

    public CarryoverItem(final String name) {

        this.name = name;

    }

    public String getName() {

        final String result = this.name;
        return result;

    }

}
```

### 早期リターンパターン

- 早期リターン（ガード節）を使用し、不要なネストを避ける
- 条件が満たされない場合は早期に `return` する
- if-else の代わりにガード節を使い、インデントの深さを最小限に抑える

```java
// 望ましくない形式:
if (condition) {
    // 処理A
    // 処理B
}

// 望ましい形式:
if (!condition) {
    return result;
}
// 処理A
// 処理B
```

```java
public boolean someMethod(String input) {
    boolean result = false;  // 先頭で戻り値変数を宣言

    // 早期リターン（ガード節）
    if (input == null) {
        return result;
    }

    // メインの処理
    result = true;

    return result;  // 統一された形式で return
}
```

## テストのコーディングルール

### テスト単位

- メソッド単位で行い、`private` / `protected` / デフォルト / `public` すべて対象とする
- private メソッド・private 変数へのアクセスは標準のリフレクション（`java.lang.reflect`）を使う。共通処理はテスト用のユーティリティクラスにまとめる

### テストクラスのアノテーション

```java
@SuppressWarnings({
    "nls", "static-method"
})
```

### テストメソッド名

- `testXxx_パターンYyy` の形式とする
- 「Xxx」の先頭は大文字で対象メソッド名を入れる
- 「パターン」は正常系 `normal`、準正常系 `semi`、異常系 `error` とする
- 「Yyy」の先頭は大文字でテスト項目を入れる
- 例: `testXxx_normalYyy` / `testXxx_semiYyy` / `testXxx_errorYyy`

### テストメソッドのアクセス修飾子

- `testXXX` メソッドのアクセス修飾子はすべて `public` にする

### テストメソッドの中身

- 対象メソッドごとに行い、1 つのテストメソッドに 1 つのテストを実装する
- 正常系・準正常系・異常系に分けて実装する
  - 正常系: 正常処理が完了するパターン（正常に return され、throw されない）
  - 準正常系: 処理が正しく完了しないパターン（引数不正などにより return または throw）
  - 異常系: 正常系・準正常系以外の想定外パターン（通信エラー・ファイル入出力エラーなどにより throw）
- GitHub API への実通信はテストで行わない（`HttpClient` をモック化するか、テスト用のレスポンスを使う）

### テストメソッドの Javadoc

- フォーマット: `対象メソッド名 メソッドのテスト - パターン:テスト内容`
- パターンには正常系・準正常系・異常系を入れる

```java
/**
 * targetMethod メソッドのテスト - 正常系:引数が1文字の場合
 */
```

### テストコードの実装順序

1. **期待値の定義** — `/* 期待値の定義 */`、`expected` で始まる変数
2. **準備** — `/* 準備 */`、`test` で始まる変数
3. **テスト対象の実行** — `/* テスト対象の実行 */`、`test` で始まる変数
4. **検証の準備** — `/* 検証の準備 */`、`actual` で始まる変数
5. **検証の実施** — `/* 検証の実施 */`
   - `Assertions.assertTrue` / `assertFalse` / `assertEquals` は `actualXXX` と説明を記載する
   - `Assertions.assertEquals` は `expectedXXX` と `actualXXX` と説明を記載する

### 検証方法の指定

- 1 行ずつ検証する
- `Assertions.assertTrue` / `assertFalse` は、`Assertions.assertEquals` で代行できる場合は代行する。ただし `condition` が `boolean` なら `assertTrue` / `assertFalse` を使用する
- 例外の検証は `Assertions.assertThrows` を使い、例外の型とメッセージを検証する
- `isInstance` / `instanceof` の比較は `Assertions.assertInstanceOf` を使用する
- null チェックは `Assertions.assertNull` を使用する
- それ以外は `Assertions.assertEquals` を使用し、期待値は「期待値の定義」の値を使う

### メッセージの検証

- ログメッセージは Logback の `ListAppender` で取得し、1 行ずつ検証する

```java
@Test
public void testMethod() {

    /* 期待値の定義 */
    final String[] expectedMsgs = {
            "メッセージ1",
            "メッセージ2",
            "メッセージ3",
    };
    /* 準備 */

    /* テスト対象の実行 */

    /* 検証の準備 */
    final String[] actualMsgs = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage)
        .toArray(String[]::new);

    /* 検証の実施 */

    // ログのチェック
    final int verMsgLength = Math.min(expectedMsgs.length, actualMsgs.length);

    for (int i = 0; i < verMsgLength; i++) {

        Assertions.assertEquals(expectedMsgs[i], actualMsgs[i],
            String.format("メッセージが一致しません: %s", expectedMsgs[i]));

    }

    // ログの数のチェック
    Assertions.assertEquals(expectedMsgs.length, actualMsgs.length);

}
```

## Javadoc のフォーマットルール

### 基本形式

```java
/**
 * クラスの説明をここに書きます。
 * 複数行の説明の場合は、このように記述します。
 *
 * @author 作成者名
 * @version バージョン番号
 * @since いつからこのクラスが存在するか（例：0.1.0）
 */
public class SampleClass {

    /** フィールドの説明をここに書きます。 */
    private String field;

    /**
     * メソッドの説明をここに書きます。
     * 処理の詳細や目的を記述します。
     *
     * @param param1 最初のパラメータの説明
     * @param param2 2番目のパラメータの説明
     * @return 戻り値の説明
     * @throws Exception1 例外が発生する条件の説明
     * @throws Exception2 別の例外が発生する条件の説明
     * @see 関連するクラスやメソッドへの参照
     * @deprecated 非推奨となった場合の説明（該当する場合）
     */
    public String sampleMethod(String param1, int param2) throws Exception {
        // メソッドの実装
    }
}
```

### 主要なタグ

| タグ | 用途 |
| --- | --- |
| `@param` | メソッドのパラメータの説明 |
| `@return` | 戻り値の説明 |
| `@throws` | 発生する可能性のある例外の説明 |
| `@author` | 作成者 |
| `@version` | バージョン情報 |
| `@since` | 導入されたバージョン |
| `@see` | 関連する他のクラスやメソッドへの参照 |
| `@deprecated` | 非推奨であることを示す |
| `@link` | 他のクラスやメソッドへのリンク |
| `@code` | コードの例を示す |
| `@value` | 定数値を参照する |
| `@serial` | シリアライズに関する情報 |

### 記述ガイドライン

- 最初の文は要約文として簡潔に書く
- フィールド（インスタンス変数・クラス変数・enum 定数）の Javadoc は `/** 説明 */` の 1 行で書く
- 完全な文章で、技術的に正確に記述する
- 必要な情報を漏れなく記載し、HTML タグを適切に使う

コード例:

```java
/**
 * サンプルコードの使用例：
 * <pre>
 * {@code
 *     String result = obj.sampleMethod("test", 123);
 * }
 * </pre>
 */
```

リンク:

```java
/**
 * 詳細は{@link OtherClass#otherMethod()}を参照してください。
 */
```

箇条書き:

```java
/**
 * このメソッドは以下の処理を行います：
 * <ul>
 * <li>データの検証</li>
 * <li>データの変換</li>
 * <li>結果の保存</li>
 * </ul>
 */
```

### チーム統一フォーマット例

```java
/**
 * [クラス/メソッド/フィールドの名前]の説明
 *
 * 詳細な説明（必要な場合）
 *
 * 業務ロジックの説明（必要な場合）
 *
 * @author      作成者 <email@example.com>
 * @param       [引数名] [引数の説明]
 * @return      [戻り値の説明]
 * @throws      [例外クラス名] [例外の発生条件]
 * @see         [参照すべき他のクラスやメソッド]
 * @since       [追加されたバージョン]
 * @version     [現在のバージョン]
 * @deprecated  [非推奨となった理由と代替手段]（該当する場合）
 */
```

## 変更時のチェックリスト

- [ ] パッケージ構成ルール（機能パッケージ + 5 層）の順守
- [ ] サービス（一連の流れ。`service/`）とロジック（個々の処理。`logic/`、サービスを呼ばない）の区分の順守
- [ ] データ系の型（`model/`、`dto/`、`types/`）と、データアクセス（外部 API は `api/<提供元>/`、ファイル・DB は `dao/<用途>/`）の置き場所の順守
- [ ] 層間のルール（依存性逆転：依存はすぐ上の層だけ、飛び越し・処理の流れと同じ向きの参照なし、データ・設定値は呼ぶ側の層で自層の型に変換）の順守（`ArchitectureTest` が通ること）
- [ ] 下の層を差し替える場合、上の層のインタフェースを実装した `impl/` を追加し、上の層のコードを変更していないこと
- [ ] DI のルール（Bean として登録、コンストラクタインジェクション、ステートレス、設定値はメソッドの引数で渡す）の順守
- [ ] JSON 形式の後方互換の確認（変更時は全件モードで再生成）
- [ ] テストの追加 / 更新（命名・実装順序・検証方法を含む）
- [ ] `mvn test` で JaCoCo カバレッジ 100% を維持
- [ ] コーディングルール（戻り値 `result`、早期リターン、処理コメント、`record` 禁止）の順守
- [ ] アクセス修飾子の順守（公開しないものは `private`、修飾子なしは原則禁止）
- [ ] Javadoc の追加 / 更新
- [ ] 解析ルールを変更した場合、`declaredCount` との食い違いと補完件数のログを確認
- [ ] 画面を変更した場合、[frontend/AGENTS.md](frontend/AGENTS.md) の「変更時のチェックリスト」を確認
- [ ] README の更新

## やってはいけないこと

- 機能パッケージの外（`io.github.kenichiroarai.dailytasks` 直下など。起動クラスを除く）に業務クラスを置くこと
- 層を飛び越して参照すること（例: repository → application、domain → presentation、エントリポイント → presentation）
- 処理の流れと同じ向き（上の層から下の層）に参照すること（例: presentation → application、application → domain、domain → repository）
- 呼ぶ側の層のインタフェースや受け渡しの型に、呼ばれる側の層の都合（JSON の形の Jackson のアノテーションなど）を持ち込むこと
- フィールドインジェクション（フィールドへの `@Autowired`）、`@Value` での設定値の個別取得、Bean のフィールドに設定値を持つこと
- infrastructure に機能固有の値や業務の判断を置くこと、infrastructure から他の層を参照すること
- kmg-core / kmg-fund などの社内基盤ライブラリに依存すること
- シークレット（`GITHUB_TOKEN` など）をコード・ログ・JSON に出すこと
- テストで GitHub API に実通信すること
- `docs/data/` の JSON を手で編集すること（必ず収集ツールで生成する）
- `docs/` にソースコード（HTML / JS / CSS など）を置くこと（画面のソースは `frontend/` に置く）
- `record` を使うこと（ブレークポイントを設定できずデバッグ・トレースの妨げになるため。通常の `class` とゲッターで実装する）
- 外部に公開しないメンバーを修飾子なし（パッケージプライベート）にすること（テストのためだけに公開範囲を広げない）
- 深いネストのままガード節を使わずに実装すること
- テストメソッドに複数ケースを詰め込むこと

## 参考リンク

- README: `./README.md`
- フロントエンドの作業ガイド: `./frontend/AGENTS.md`
- フロントエンドの設計書: `./frontend/docs/design.md`
- GitHub REST API（Issues）: `https://docs.github.com/rest/issues/issues`

## 順守

以上の内容を順守し、タスクを遂行してください。
