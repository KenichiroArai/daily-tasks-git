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
- ビルド / パッケージ管理: Maven（maven-shade-plugin で実行可能 jar を作成）
- ライブラリ: Jackson（JSON）、SLF4J + Logback（ログ）
- HTTP: 標準の `java.net.http.HttpClient`（GitHub REST API）
- フロントエンド: Next.js（App Router、`output: 'export'` の静的エクスポート）+ TypeScript + React + Recharts、zod（JSON の検証）、CSS Modules。Node.js 20.9 以降（CI は 22）
- 公開: GitHub Pages（GitHub Actions でデプロイ）
- テスト: JUnit 5 / JaCoCo（行・分岐 100%）。フロントエンドは Vitest + Testing Library、ESLint、Prettier
- 外部の社内基盤ライブラリ（kmg-core / kmg-fund など）には依存しない

## ディレクトリ構成

```text
pom.xml
src/main/java/io/github/kenichiroarai/dailytasks/
  DailyTasksApplication.java # 起動クラス（引数解析と carryover の実行）
  carryover/                 # 機能パッケージ（持ち越しの収集・集計）
    presentation/            # CLI などの入出力層
      command/               # コマンドライン引数の解釈と実行
    application/             # ユースケースや業務ロジック層
      service/               # サービスのインタフェース
      service/impl/          # サービスの実装（収集・解析・集計の流れ）
    domain/                  # 共通ロジック、ドメインモデル層
      model/                 # Issue、持ち越し項目、日別集計などのモデル
      parser/                # Issue 本文の解析ルール
      aggregator/            # 日別の集計
    infrastructure/          # 基盤となる処理層
      github/                # GitHub REST API クライアント
    repository/              # JSON ファイルの読み書き（Dao）
src/main/resources/
  logback.xml
src/test/java/io/github/kenichiroarai/dailytasks/  # main と同じ構成
  testutil/                  # テスト用のユーティリティ（ログ取得など）
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
| `application/` | ユースケースや業務ロジック層（service など） |
| `domain/` | 共通ロジック、ドメインモデル層（model / parser など） |
| `infrastructure/` | 基盤となる処理層（GitHub API クライアントなど） |
| `repository/` | JSON ファイルなどのデータアクセス層 |

- 新しい機能は `carryover/` と同じ構成で追加する
- 中身のないパッケージには、Git で消えないように Javadoc 付きの `package-info.java` を置く
- サービスはインタフェース（`application/service/`）と実装（`application/service/impl/`）に分ける
- domain 層は infrastructure 層・repository 層に依存しない
- テストのパッケージは main と同じ構成にする

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
- GitHub Actions（`.github/workflows/update-carryover.yml`）は毎日のスケジュール、手動実行（`full` 入力あり）、Issue イベント（opened / edited / closed / reopened）、`main` への push で動き、差分を commit したあと `frontend/` をビルド（lint・型チェック・テストを含む）し、`frontend/out` を Pages にデプロイする。basePath は `actions/configure-pages` の出力を環境変数 `PAGES_BASE_PATH` で渡す
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

    /**
     * フィールドの説明をここに書きます。
     */
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
- [ ] JSON 形式の後方互換の確認（変更時は全件モードで再生成）
- [ ] テストの追加 / 更新（命名・実装順序・検証方法を含む）
- [ ] `mvn test` で JaCoCo カバレッジ 100% を維持
- [ ] コーディングルール（戻り値 `result`、早期リターン、処理コメント、`record` 禁止）の順守
- [ ] Javadoc の追加 / 更新
- [ ] 解析ルールを変更した場合、`declaredCount` との食い違いと補完件数のログを確認
- [ ] 画面を変更した場合、[frontend/AGENTS.md](frontend/AGENTS.md) の「変更時のチェックリスト」を確認
- [ ] README の更新

## やってはいけないこと

- 機能パッケージの外（`io.github.kenichiroarai.dailytasks` 直下など。起動クラスを除く）に業務クラスを置くこと
- domain 層から infrastructure 層・repository 層に依存すること
- kmg-core / kmg-fund などの社内基盤ライブラリに依存すること
- シークレット（`GITHUB_TOKEN` など）をコード・ログ・JSON に出すこと
- テストで GitHub API に実通信すること
- `docs/data/` の JSON を手で編集すること（必ず収集ツールで生成する）
- `docs/` にソースコード（HTML / JS / CSS など）を置くこと（画面のソースは `frontend/` に置く）
- `record` を使うこと（ブレークポイントを設定できずデバッグ・トレースの妨げになるため。通常の `class` とゲッターで実装する）
- 深いネストのままガード節を使わずに実装すること
- テストメソッドに複数ケースを詰め込むこと

## 参考リンク

- README: `./README.md`
- フロントエンドの作業ガイド: `./frontend/AGENTS.md`
- フロントエンドの設計書: `./frontend/docs/design.md`
- GitHub REST API（Issues）: `https://docs.github.com/rest/issues/issues`

## 順守

以上の内容を順守し、タスクを遂行してください。
