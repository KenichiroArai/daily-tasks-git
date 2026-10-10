package io.github.kenichiroarai.dailytasks.carryover.repository.github.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * GitHub REST API で Issue を取得するリポジトリの実装<br>
 * <p>
 * HTTP クライアントは取得のたびに生成して閉じる。トークンはログに出力しない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Repository
public class GitHubIssueRepositoryImpl implements GitHubIssueRepository {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubIssueRepositoryImpl.class);

    /**
     * メッセージのキー：Issue の取得件数
     */
    private static final String MSG_FETCHED = "carryover.github.fetched";

    /**
     * メッセージのキー：API の呼び出しの失敗
     */
    private static final String MSG_CALL_FAILED = "carryover.github.callFailed";

    /**
     * メッセージのキー：応答が配列ではない
     */
    private static final String MSG_NOT_ARRAY = "carryover.github.notArray";

    /**
     * メッセージのキー：API の呼び出しの中断
     */
    private static final String MSG_INTERRUPTED = "carryover.github.interrupted";

    /**
     * GitHub API のベース URL
     */
    private static final String API_BASE_URL = "https://api.github.com";

    /**
     * 1 ページあたりの取得件数（GitHub API の上限）
     */
    private static final int PER_PAGE = 100;

    /**
     * 正常応答のステータスコード
     */
    private static final int STATUS_OK = 200;

    /**
     * リクエストのタイムアウト
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * HTTP クライアントの生成
     */
    private final Supplier<HttpClient> httpClientSupplier;

    /**
     * API のベース URL（例: https://api.github.com）
     */
    private final String apiBaseUrl;

    /**
     * JSON の変換
     */
    private final JsonMapper jsonMapper;

    /**
     * メッセージの取得
     */
    private final MessageProvider messageProvider;

    /**
     * コンストラクタ<br>
     *
     * @param jsonMapper
     *                        JSON の変換
     * @param messageProvider
     *                        メッセージの取得
     */
    @Autowired
    public GitHubIssueRepositoryImpl(final JsonMapper jsonMapper, final MessageProvider messageProvider) {

        this(HttpClient::newHttpClient, GitHubIssueRepositoryImpl.API_BASE_URL, jsonMapper, messageProvider);

    }

    /**
     * HTTP クライアントの生成と API のベース URL を指定するコンストラクタ<br>
     *
     * @param httpClientSupplier
     *                           HTTP クライアントの生成
     * @param apiBaseUrl
     *                           API のベース URL（例: https://api.github.com）
     * @param jsonMapper
     *                           JSON の変換
     * @param messageProvider
     *                           メッセージの取得
     */
    public GitHubIssueRepositoryImpl(final Supplier<HttpClient> httpClientSupplier, final String apiBaseUrl,
        final JsonMapper jsonMapper, final MessageProvider messageProvider) {

        this.httpClientSupplier = httpClientSupplier;
        this.apiBaseUrl = apiBaseUrl;
        this.jsonMapper = jsonMapper;
        this.messageProvider = messageProvider;

    }

    /**
     * すべての Issue（オープン・クローズ済み）を取得する<br>
     * <p>
     * プルリクエストは除外する。
     * </p>
     *
     * @param settings
     *                 GitHub API の接続設定
     *
     * @return Issue（作成順）
     *
     * @throws IOException
     *                     通信に失敗した場合、または応答が不正な場合
     */
    @Override
    public List<GitHubIssueDto> fetchAllIssues(final GitHubSettingsDto settings) throws IOException {

        final List<GitHubIssueDto> result = new ArrayList<>();

        int page = 1;
        int pageSize;

        try (HttpClient httpClient = this.httpClientSupplier.get()) {

            do {

                /* 1 ページ分の取得 */
                final JsonNode issues = this.fetchPage(httpClient, settings, page);
                pageSize = issues.size();

                for (final JsonNode node : issues) {

                    // プルリクエストは対象外
                    if (node.has("pull_request")) {

                        continue;

                    }

                    result.add(GitHubIssueRepositoryImpl.toIssue(node));

                }

                page++;

            } while (pageSize >= GitHubIssueRepositoryImpl.PER_PAGE);

        }

        final String message = this.messageProvider.get(GitHubIssueRepositoryImpl.MSG_FETCHED);
        GitHubIssueRepositoryImpl.LOGGER.info(message, result.size(), page - 1);
        return result;

    }

    /**
     * 1 ページ分の Issue を取得する<br>
     *
     * @param httpClient
     *                   HTTP クライアント
     * @param settings
     *                   GitHub API の接続設定
     * @param page
     *                   ページ番号（1 始まり）
     *
     * @return Issue の配列
     *
     * @throws IOException
     *                     通信に失敗した場合、または応答が不正な場合
     */
    private JsonNode fetchPage(final HttpClient httpClient, final GitHubSettingsDto settings, final int page)
        throws IOException {

        JsonNode result = null;

        /* リクエストの作成 */
        final URI uri = URI.create(String.format("%s/repos/%s/issues?state=all&sort=created&direction=asc&per_page=%d&page=%d",
            this.apiBaseUrl, settings.getRepository(), GitHubIssueRepositoryImpl.PER_PAGE, page));
        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(GitHubIssueRepositoryImpl.TIMEOUT)
            .header("Accept", "application/vnd.github+json").header("X-GitHub-Api-Version", "2022-11-28").GET();

        if (GitHubIssueRepositoryImpl.hasToken(settings)) {

            builder.header("Authorization", "Bearer " + settings.getToken());

        }

        /* 送信と応答の検証 */
        final HttpResponse<String> response = this.send(httpClient, builder.build());

        if (response.statusCode() != GitHubIssueRepositoryImpl.STATUS_OK) {

            final String template = this.messageProvider.get(GitHubIssueRepositoryImpl.MSG_CALL_FAILED);
            final String message = String.format(template, response.statusCode(), uri);
            throw new IOException(message);

        }

        result = this.readTree(response.body());

        if (!result.isArray()) {

            final String template = this.messageProvider.get(GitHubIssueRepositoryImpl.MSG_NOT_ARRAY);
            final String message = String.format(template, uri);
            throw new IOException(message);

        }

        return result;

    }

    /**
     * 応答の本文を JSON として読み込む<br>
     *
     * @param body
     *             応答の本文
     *
     * @return JSON
     *
     * @throws IOException
     *                     JSON として読み込めない場合
     */
    private JsonNode readTree(final String body) throws IOException {

        JsonNode result = null;

        try {

            result = this.jsonMapper.readTree(body);

        } catch (final JacksonException e) {

            throw new IOException(e);

        }

        return result;

    }

    /**
     * リクエストを送信する<br>
     *
     * @param httpClient
     *                   HTTP クライアント
     * @param request
     *                   リクエスト
     *
     * @return 応答
     *
     * @throws IOException
     *                     通信に失敗した場合、または中断された場合
     */
    private HttpResponse<String> send(final HttpClient httpClient, final HttpRequest request) throws IOException {

        HttpResponse<String> result = null;

        try {

            result = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        } catch (final InterruptedException e) {

            Thread.currentThread().interrupt();
            final String message = this.messageProvider.get(GitHubIssueRepositoryImpl.MSG_INTERRUPTED);
            throw new IOException(message, e);

        }

        return result;

    }

    /**
     * トークンが指定されているかを返す<br>
     *
     * @param settings
     *                 GitHub API の接続設定
     *
     * @return true：指定されている、false：指定されていない
     */
    private static boolean hasToken(final GitHubSettingsDto settings) {

        boolean result = false;
        final String token = settings.getToken();

        if (token == null) {

            return result;

        }

        result = !token.isBlank();
        return result;

    }

    /**
     * API の応答を Issue の DTO に変換する<br>
     *
     * @param node
     *             Issue の JSON
     *
     * @return Issue の DTO
     */
    private static GitHubIssueDto toIssue(final JsonNode node) {

        final GitHubIssueDto result = new GitHubIssueDto(node.path("number").asInt(),
            GitHubIssueRepositoryImpl.textOrEmpty(node, "title"), GitHubIssueRepositoryImpl.textOrEmpty(node, "state"),
            GitHubIssueRepositoryImpl.textOrEmpty(node, "updated_at"),
            GitHubIssueRepositoryImpl.textOrEmpty(node, "body"));
        return result;

    }

    /**
     * 文字列の項目を返す<br>
     *
     * @param node
     *                  JSON
     * @param fieldName
     *                  項目名
     *
     * @return 値。項目がない場合または null の場合は空文字
     */
    private static String textOrEmpty(final JsonNode node, final String fieldName) {

        String result = "";
        final JsonNode value = node.get(fieldName);

        if (value == null) {

            return result;

        }

        if (value.isNull()) {

            return result;

        }

        result = value.asString();
        return result;

    }

}
