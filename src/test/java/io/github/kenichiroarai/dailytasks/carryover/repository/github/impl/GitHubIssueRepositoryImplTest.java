package io.github.kenichiroarai.dailytasks.carryover.repository.github.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.testutil.LogAssertions;
import io.github.kenichiroarai.dailytasks.testutil.LogCapture;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * {@link GitHubIssueRepositoryImpl} のテスト<br>
 * <p>
 * GitHub API への実通信は行わず、{@link StubHttpClient} の応答を使う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings({
    "nls", "static-method", "resource",
})
public class GitHubIssueRepositoryImplTest {

    /**
     * テスト用の API のベース URL
     */
    private static final String API_BASE_URL = "https://api.example.com";

    /**
     * テスト用のリポジトリ
     */
    private static final String REPOSITORY = "owner/repo";

    /**
     * 1 ページ目の URL
     */
    private static final String PAGE1_URI
        = "https://api.example.com/repos/owner/repo/issues?state=all&sort=created&direction=asc&per_page=100&page=1";

    /**
     * Issue の JSON を作成する<br>
     *
     * @param number
     *               Issue 番号
     *
     * @return Issue の JSON
     */
    private static String issueJson(final int number) {

        final String result = String.format(
            "{\"number\":%d,\"title\":\"2026年10月06日のタスク\",\"state\":\"open\",\"updated_at\":\"2026-10-06T14:23:08Z\",\"body\":\"本文\"}",
            Integer.valueOf(number));
        return result;

    }

    /**
     * スタブの HTTP クライアントを使うテスト対象を作成する<br>
     *
     * @param httpClient
     *                   HTTP クライアント
     *
     * @return テスト対象
     */
    private static GitHubIssueRepositoryImpl createTarget(final HttpClient httpClient) {

        final GitHubIssueRepositoryImpl result = new GitHubIssueRepositoryImpl(() -> httpClient,
            GitHubIssueRepositoryImplTest.API_BASE_URL, new JsonMapper(), MessageProviderTestUtil.create());
        return result;

    }

    /**
     * 接続設定を作成する<br>
     *
     * @param token
     *              トークン
     *
     * @return 接続設定
     */
    private static GitHubSettingsDto createSettings(final String token) {

        final GitHubSettingsDto result = new GitHubSettingsDto(GitHubIssueRepositoryImplTest.REPOSITORY, token);
        return result;

    }

    /**
     * private の fetchPage メソッドを呼び出す<br>
     *
     * @param target
     *                   テスト対象
     * @param httpClient
     *                   HTTP クライアント
     * @param settings
     *                   接続設定
     * @param page
     *                   ページ番号
     *
     * @return API の応答
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static JsonNode fetchPage(final GitHubIssueRepositoryImpl target, final HttpClient httpClient,
        final GitHubSettingsDto settings, final int page) throws Exception {

        final JsonNode result = ReflectionTestUtil.invoke(target, "fetchPage", new Class<?>[] {
            HttpClient.class, GitHubSettingsDto.class, int.class
        }, httpClient, settings, Integer.valueOf(page));
        return result;

    }

    /**
     * private の send メソッドを呼び出す<br>
     *
     * @param target
     *                   テスト対象
     * @param httpClient
     *                   HTTP クライアント
     * @param request
     *                   リクエスト
     *
     * @return 応答
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static HttpResponse<String> send(final GitHubIssueRepositoryImpl target, final HttpClient httpClient,
        final HttpRequest request) throws Exception {

        final HttpResponse<String> result = ReflectionTestUtil.invoke(target, "send", new Class<?>[] {
            HttpClient.class, HttpRequest.class
        }, httpClient, request);
        return result;

    }

    /**
     * private の readTree メソッドを呼び出す<br>
     *
     * @param target
     *               テスト対象
     * @param body
     *               応答の本文
     *
     * @return JSON
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static JsonNode readTree(final GitHubIssueRepositoryImpl target, final String body) throws Exception {

        final JsonNode result = ReflectionTestUtil.invoke(target, "readTree", new Class<?>[] {
            String.class
        }, body);
        return result;

    }

    /**
     * private の hasToken メソッドを呼び出す<br>
     *
     * @param settings
     *                 接続設定
     *
     * @return トークンがある場合は true
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static boolean hasToken(final GitHubSettingsDto settings) throws Exception {

        final boolean result = ReflectionTestUtil.<Boolean> invokeStatic(GitHubIssueRepositoryImpl.class, "hasToken",
            new Class<?>[] {
                GitHubSettingsDto.class
            }, settings).booleanValue();
        return result;

    }

    /**
     * private の toIssue メソッドを呼び出す<br>
     *
     * @param node
     *             API の応答の Issue
     *
     * @return Issue
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static GitHubIssueDto toIssue(final JsonNode node) throws Exception {

        final GitHubIssueDto result = ReflectionTestUtil.invokeStatic(GitHubIssueRepositoryImpl.class, "toIssue",
            new Class<?>[] {
                JsonNode.class
            }, node);
        return result;

    }

    /**
     * private の textOrEmpty メソッドを呼び出す<br>
     *
     * @param node
     *                  API の応答
     * @param fieldName
     *                  項目名
     *
     * @return 項目の値
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static String textOrEmpty(final JsonNode node, final String fieldName) throws Exception {

        final String result = ReflectionTestUtil.invokeStatic(GitHubIssueRepositoryImpl.class, "textOrEmpty",
            new Class<?>[] {
                JsonNode.class, String.class
            }, node, fieldName);
        return result;

    }

    /**
     * GitHubIssueRepositoryImpl コンストラクタのテスト - 正常系:JSON の変換とメッセージの取得だけを指定した場合は GitHub API のベース URL を使う
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testGitHubIssueRepositoryImpl_normalDefaultBaseUrl() throws Exception {

        /* 期待値の定義 */
        final String expectedApiBaseUrl = "https://api.github.com";

        /* 準備 */
        final JsonMapper testJsonMapper = new JsonMapper();

        /* テスト対象の実行 */
        final GitHubIssueRepositoryImpl testTarget = new GitHubIssueRepositoryImpl(testJsonMapper,
            MessageProviderTestUtil.create());

        /* 検証の準備 */
        final String actualApiBaseUrl = ReflectionTestUtil.getField(testTarget, "apiBaseUrl");
        final JsonMapper actualJsonMapper = ReflectionTestUtil.getField(testTarget, "jsonMapper");

        /* 検証の実施 */
        Assertions.assertEquals(expectedApiBaseUrl, actualApiBaseUrl, "API のベース URL が一致しません");
        Assertions.assertEquals(testJsonMapper, actualJsonMapper, "JSON の変換が一致しません");

    }

    /**
     * fetchAllIssues メソッドのテスト - 正常系:1 ページでプルリクエストを除外する場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testFetchAllIssues_normalSinglePageWithoutPullRequest() throws IOException {

        /* 期待値の定義 */
        final List<Integer> expectedNumbers = List.of(1);
        final String[]      expectedMsgs    = {
            "Issue を 1 件取得しました（1 ページ）",
        };

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient();
            LogCapture testLog = new LogCapture(GitHubIssueRepositoryImpl.class)) {

            testHttpClient.addResponse(200,
                "[" + GitHubIssueRepositoryImplTest.issueJson(1) + ",{\"number\":2,\"pull_request\":{}}]");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);

            /* テスト対象の実行 */
            final List<GitHubIssueDto> testResult = testTarget
                .fetchAllIssues(GitHubIssueRepositoryImplTest.createSettings(null));

            /* 検証の準備 */
            final String[]      actualMsgs    = testLog.getMessages();
            final List<Integer> actualNumbers = testResult.stream().map(GitHubIssueDto::getNumber).toList();

            /* 検証の実施 */
            Assertions.assertEquals(expectedNumbers, actualNumbers, "Issue 番号が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * fetchAllIssues メソッドのテスト - 正常系:複数ページを取得する場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testFetchAllIssues_normalMultiplePages() throws IOException {

        /* 期待値の定義 */
        final int expectedSize         = 100;
        final int expectedRequestCount = 2;

        /* 準備 */
        final String testPage1 = IntStream.rangeClosed(1, 100).mapToObj(GitHubIssueRepositoryImplTest::issueJson)
            .collect(Collectors.joining(",", "[", "]"));

        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(200, testPage1).addResponse(200, "[]");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);

            /* テスト対象の実行 */
            final List<GitHubIssueDto> testResult = testTarget
                .fetchAllIssues(GitHubIssueRepositoryImplTest.createSettings(null));

            /* 検証の準備 */
            final int actualSize         = testResult.size();
            final int actualRequestCount = testHttpClient.getRequests().size();

            /* 検証の実施 */
            Assertions.assertEquals(expectedSize, actualSize, "Issue の件数が一致しません");
            Assertions.assertEquals(expectedRequestCount, actualRequestCount, "リクエスト数が一致しません");

        }

    }

    /**
     * fetchPage メソッドのテスト - 正常系:トークンを指定した場合は認証ヘッダを付ける
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testFetchPage_normalWithToken() throws Exception {

        /* 期待値の定義 */
        final Optional<String> expectedAuthorization = Optional.of("Bearer test-token");
        final String           expectedUri           = GitHubIssueRepositoryImplTest.PAGE1_URI;

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(200, "[]");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);

            /* テスト対象の実行 */
            GitHubIssueRepositoryImplTest.fetchPage(testTarget, testHttpClient,
                GitHubIssueRepositoryImplTest.createSettings("test-token"), 1);

            /* 検証の準備 */
            final Optional<String> actualAuthorization = testHttpClient.getRequests().get(0).headers()
                .firstValue("Authorization");
            final String           actualUri           = testHttpClient.getRequests().get(0).uri().toString();

            /* 検証の実施 */
            Assertions.assertEquals(expectedAuthorization, actualAuthorization, "認証ヘッダが一致しません");
            Assertions.assertEquals(expectedUri, actualUri, "URL が一致しません");

        }

    }

    /**
     * fetchPage メソッドのテスト - 正常系:トークンを指定しない場合は認証ヘッダを付けない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testFetchPage_normalWithoutToken() throws Exception {

        /* 期待値の定義 */
        final Optional<String> expectedAuthorization = Optional.empty();

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(200, "[]");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);

            /* テスト対象の実行 */
            GitHubIssueRepositoryImplTest.fetchPage(testTarget, testHttpClient,
                GitHubIssueRepositoryImplTest.createSettings(null), 1);

            /* 検証の準備 */
            final Optional<String> actualAuthorization
                = testHttpClient.getRequests().get(0).headers().firstValue("Authorization");

            /* 検証の実施 */
            Assertions.assertEquals(expectedAuthorization, actualAuthorization, "認証ヘッダが一致しません");

        }

    }

    /**
     * fetchPage メソッドのテスト - 異常系:ステータスコードが 200 以外の場合
     */
    @Test
    public void testFetchPage_errorStatus() {

        /* 期待値の定義 */
        final String expectedMessage = "GitHub API の呼び出しに失敗しました: status=403, uri=" + GitHubIssueRepositoryImplTest.PAGE1_URI;

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(403, "{}");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);
            final GitHubSettingsDto testSettings = GitHubIssueRepositoryImplTest.createSettings(null);

            /* テスト対象の実行 */
            final IOException testException = Assertions.assertThrows(IOException.class,
                () -> GitHubIssueRepositoryImplTest.fetchPage(testTarget, testHttpClient, testSettings, 1));

            /* 検証の準備 */
            final String actualMessage = testException.getMessage();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

        }

    }

    /**
     * fetchPage メソッドのテスト - 異常系:応答が配列ではない場合
     */
    @Test
    public void testFetchPage_errorNotArray() {

        /* 期待値の定義 */
        final String expectedMessage = "GitHub API の応答が配列ではありません: uri=" + GitHubIssueRepositoryImplTest.PAGE1_URI;

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(200, "{\"message\":\"x\"}");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);
            final GitHubSettingsDto testSettings = GitHubIssueRepositoryImplTest.createSettings(null);

            /* テスト対象の実行 */
            final IOException testException = Assertions.assertThrows(IOException.class,
                () -> GitHubIssueRepositoryImplTest.fetchPage(testTarget, testHttpClient, testSettings, 1));

            /* 検証の準備 */
            final String actualMessage = testException.getMessage();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

        }

    }

    /**
     * readTree メソッドのテスト - 正常系:JSON を読み込む場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testReadTree_normalArray() throws Exception {

        /* 期待値の定義 */
        final int expectedSize = 2;

        /* 準備 */
        final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(null);

        /* テスト対象の実行 */
        final JsonNode testResult = GitHubIssueRepositoryImplTest.readTree(testTarget, "[1,2]");

        /* 検証の準備 */
        final int actualSize = testResult.size();

        /* 検証の実施 */
        Assertions.assertEquals(expectedSize, actualSize, "要素数が一致しません");

    }

    /**
     * readTree メソッドのテスト - 異常系:JSON として読み込めない場合
     */
    @Test
    public void testReadTree_errorInvalidJson() {

        /* 期待値の定義 */

        /* 準備 */
        final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(null);

        /* テスト対象の実行 */
        final IOException testException = Assertions.assertThrows(IOException.class,
            () -> GitHubIssueRepositoryImplTest.readTree(testTarget, "[不正"));

        /* 検証の準備 */
        final Throwable actualCause = testException.getCause();

        /* 検証の実施 */
        Assertions.assertInstanceOf(JacksonException.class, actualCause, "原因の例外の型が一致しません");

    }

    /**
     * send メソッドのテスト - 正常系:応答を返す場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testSend_normalResponse() throws Exception {

        /* 期待値の定義 */
        final String expectedBody = "[]";

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.addResponse(200, "[]");
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);
            final HttpRequest testRequest = HttpRequest.newBuilder(URI.create(GitHubIssueRepositoryImplTest.PAGE1_URI))
                .build();

            /* テスト対象の実行 */
            final HttpResponse<String> testResult = GitHubIssueRepositoryImplTest.send(testTarget, testHttpClient,
                testRequest);

            /* 検証の準備 */
            final String actualBody = testResult.body();

            /* 検証の実施 */
            Assertions.assertEquals(expectedBody, actualBody, "本文が一致しません");

        }

    }

    /**
     * send メソッドのテスト - 異常系:通信が中断された場合
     */
    @Test
    public void testSend_errorInterrupted() {

        /* 期待値の定義 */
        final String expectedMessage = "GitHub API の呼び出しが中断されました";

        /* 準備 */
        try (StubHttpClient testHttpClient = new StubHttpClient()) {

            testHttpClient.setInterruptedException(new InterruptedException("中断"));
            final GitHubIssueRepositoryImpl testTarget = GitHubIssueRepositoryImplTest.createTarget(testHttpClient);
            final HttpRequest testRequest = HttpRequest.newBuilder(URI.create(GitHubIssueRepositoryImplTest.PAGE1_URI))
                .build();

            /* テスト対象の実行 */
            final IOException testException = Assertions.assertThrows(IOException.class,
                () -> GitHubIssueRepositoryImplTest.send(testTarget, testHttpClient, testRequest));

            /* 検証の準備 */
            final String    actualMessage     = testException.getMessage();
            final Throwable actualCause       = testException.getCause();
            final boolean   actualInterrupted = Thread.interrupted();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");
            Assertions.assertInstanceOf(InterruptedException.class, actualCause, "原因の例外の型が一致しません");
            Assertions.assertTrue(actualInterrupted, "割り込み状態が復元される必要があります");

        }

    }

    /**
     * hasToken メソッドのテスト - 正常系:トークンを指定した場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testHasToken_normalSpecified() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final GitHubSettingsDto testSettings = GitHubIssueRepositoryImplTest.createSettings("test-token");

        /* テスト対象の実行 */
        final boolean testResult = GitHubIssueRepositoryImplTest.hasToken(testSettings);

        /* 検証の準備 */
        final boolean actualHasToken = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualHasToken, "トークンありと判定される必要があります");

    }

    /**
     * hasToken メソッドのテスト - 準正常系:トークンが null の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testHasToken_semiNull() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final GitHubSettingsDto testSettings = GitHubIssueRepositoryImplTest.createSettings(null);

        /* テスト対象の実行 */
        final boolean testResult = GitHubIssueRepositoryImplTest.hasToken(testSettings);

        /* 検証の準備 */
        final boolean actualHasToken = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualHasToken, "トークンなしと判定される必要があります");

    }

    /**
     * hasToken メソッドのテスト - 準正常系:トークンが空白の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testHasToken_semiBlank() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final GitHubSettingsDto testSettings = GitHubIssueRepositoryImplTest.createSettings(" ");

        /* テスト対象の実行 */
        final boolean testResult = GitHubIssueRepositoryImplTest.hasToken(testSettings);

        /* 検証の準備 */
        final boolean actualHasToken = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualHasToken, "トークンなしと判定される必要があります");

    }

    /**
     * toIssue メソッドのテスト - 正常系:API の応答を Issue に変換する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testToIssue_normalConvert() throws Exception {

        /* 期待値の定義 */
        final int    expectedNumber    = 371;
        final String expectedTitle     = "2026年10月06日のタスク";
        final String expectedState     = "open";
        final String expectedUpdatedAt = "2026-10-06T14:23:08Z";
        final String expectedBody      = "本文";

        /* 準備 */
        final JsonNode testNode = new JsonMapper().readTree(GitHubIssueRepositoryImplTest.issueJson(371));

        /* テスト対象の実行 */
        final GitHubIssueDto testResult = GitHubIssueRepositoryImplTest.toIssue(testNode);

        /* 検証の準備 */
        final int    actualNumber    = testResult.getNumber();
        final String actualTitle     = testResult.getTitle();
        final String actualState     = testResult.getState();
        final String actualUpdatedAt = testResult.getUpdatedAt();
        final String actualBody      = testResult.getBody();

        /* 検証の実施 */
        Assertions.assertEquals(expectedNumber, actualNumber, "Issue 番号が一致しません");
        Assertions.assertEquals(expectedTitle, actualTitle, "タイトルが一致しません");
        Assertions.assertEquals(expectedState, actualState, "状態が一致しません");
        Assertions.assertEquals(expectedUpdatedAt, actualUpdatedAt, "更新日時が一致しません");
        Assertions.assertEquals(expectedBody, actualBody, "本文が一致しません");

    }

    /**
     * textOrEmpty メソッドのテスト - 正常系:値がある場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testTextOrEmpty_normalValue() throws Exception {

        /* 期待値の定義 */
        final String expectedValue = "本文";

        /* 準備 */
        final JsonNode testNode = new JsonMapper().readTree("{\"body\":\"本文\"}");

        /* テスト対象の実行 */
        final String testResult = GitHubIssueRepositoryImplTest.textOrEmpty(testNode, "body");

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "値が一致しません");

    }

    /**
     * textOrEmpty メソッドのテスト - 準正常系:値が null の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testTextOrEmpty_semiNull() throws Exception {

        /* 期待値の定義 */
        final String expectedValue = "";

        /* 準備 */
        final JsonNode testNode = new JsonMapper().readTree("{\"body\":null}");

        /* テスト対象の実行 */
        final String testResult = GitHubIssueRepositoryImplTest.textOrEmpty(testNode, "body");

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "値が一致しません");

    }

    /**
     * textOrEmpty メソッドのテスト - 準正常系:項目がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testTextOrEmpty_semiMissing() throws Exception {

        /* 期待値の定義 */
        final String expectedValue = "";

        /* 準備 */
        final JsonNode testNode = new JsonMapper().readTree("{}");

        /* テスト対象の実行 */
        final String testResult = GitHubIssueRepositoryImplTest.textOrEmpty(testNode, "body");

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "値が一致しません");

    }

}
