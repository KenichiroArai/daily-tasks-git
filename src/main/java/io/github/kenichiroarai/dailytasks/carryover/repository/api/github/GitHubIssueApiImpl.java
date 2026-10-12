package io.github.kenichiroarai.dailytasks.carryover.repository.api.github;

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

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.repository.api.GitHubIssueApi;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.MessageLogic;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * GitHub REST API で Issue を取得する外部 API の実装<br>
 * <p>
 * domain 層の外部 API を実装し、API の応答を domain 層の DTO に変換して返す。HTTP クライアントは取得のたびに生成して閉じる。トークンはログに出力しない。
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
public class GitHubIssueApiImpl implements GitHubIssueApi {

    /** ロガー */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubIssueApiImpl.class);

    /** メッセージのキー：Issue の取得件数 */
    private static final String MSG_FETCHED = "carryover.github.fetched";

    /** メッセージのキー：API の呼び出しの失敗 */
    private static final String MSG_CALL_FAILED = "carryover.github.callFailed";

    /** メッセージのキー：応答が配列ではない */
    private static final String MSG_NOT_ARRAY = "carryover.github.notArray";

    /** メッセージのキー：API の呼び出しの中断 */
    private static final String MSG_INTERRUPTED = "carryover.github.interrupted";

    /** GitHub API のベース URL */
    private static final String API_BASE_URL = "https://api.github.com";

    /** 1 ページあたりの取得件数（GitHub API の上限） */
    private static final int PER_PAGE = 100;

    /** 正常応答のステータスコード */
    private static final int STATUS_OK = 200;

    /** リクエストのタイムアウト */
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /**
     * トークンが指定されているかを返す<br>
     *
     * @param settings
     *                 GitHub API の接続設定
     *
     * @return true：指定されている、false：指定されていない
     */
    private static boolean hasToken(final GitHubSettingsDto settings) {

        boolean      result = false;
        final String token  = settings.getToken();

        if (token == null) {

            return result;

        }

        result = !token.isBlank();
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

        String         result = "";
        final JsonNode value  = node.get(fieldName);

        if (value == null) {

            return result;

        }

        if (value.isNull()) {

            return result;

        }

        result = value.asString();
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
            GitHubIssueApiImpl.textOrEmpty(node, "title"), GitHubIssueApiImpl.textOrEmpty(node, "state"),
            GitHubIssueApiImpl.textOrEmpty(node, "updated_at"),
            GitHubIssueApiImpl.textOrEmpty(node, "body"));
        return result;

    }

    /** HTTP クライアントの生成 */
    private final Supplier<HttpClient> httpClientSupplier;

    /** API のベース URL（例: https://api.github.com） */
    private final String apiBaseUrl;

    /** JSON の変換 */
    private final JsonMapper jsonMapper;

    /** メッセージの取得 */
    private final MessageLogic messageLogic;

    /**
     * コンストラクタ<br>
     *
     * @param jsonMapper
     *                     JSON の変換
     * @param messageLogic
     *                     メッセージの取得
     */
    @Autowired
    public GitHubIssueApiImpl(final JsonMapper jsonMapper, final MessageLogic messageLogic) {

        this(HttpClient::newHttpClient, GitHubIssueApiImpl.API_BASE_URL, jsonMapper, messageLogic);

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
     * @param messageLogic
     *                           メッセージの取得
     */
    public GitHubIssueApiImpl(final Supplier<HttpClient> httpClientSupplier, final String apiBaseUrl,
        final JsonMapper jsonMapper, final MessageLogic messageLogic) {

        this.httpClientSupplier = httpClientSupplier;
        this.apiBaseUrl = apiBaseUrl;
        this.jsonMapper = jsonMapper;
        this.messageLogic = messageLogic;

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

                    result.add(GitHubIssueApiImpl.toIssue(node));

                }

                page++;

            } while (pageSize >= GitHubIssueApiImpl.PER_PAGE);

        }

        final String message = this.messageLogic.get(GitHubIssueApiImpl.MSG_FETCHED);
        GitHubIssueApiImpl.LOGGER.info(message, result.size(), page - 1);
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
        final URI                 uri     = URI
            .create(String.format("%s/repos/%s/issues?state=all&sort=created&direction=asc&per_page=%d&page=%d",
                this.apiBaseUrl, settings.getRepository(), GitHubIssueApiImpl.PER_PAGE, page));
        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(GitHubIssueApiImpl.TIMEOUT)
            .header("Accept", "application/vnd.github+json").header("X-GitHub-Api-Version", "2022-11-28").GET();

        if (GitHubIssueApiImpl.hasToken(settings)) {

            builder.header("Authorization", "Bearer " + settings.getToken());

        }

        /* 送信と応答の検証 */
        final HttpResponse<String> response = this.send(httpClient, builder.build());

        if (response.statusCode() != GitHubIssueApiImpl.STATUS_OK) {

            final String template = this.messageLogic.get(GitHubIssueApiImpl.MSG_CALL_FAILED);
            final String message  = String.format(template, response.statusCode(), uri);
            throw new IOException(message);

        }

        result = this.readTree(response.body());

        if (!result.isArray()) {

            final String template = this.messageLogic.get(GitHubIssueApiImpl.MSG_NOT_ARRAY);
            final String message  = String.format(template, uri);
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
            final String message = this.messageLogic.get(GitHubIssueApiImpl.MSG_INTERRUPTED);
            throw new IOException(message, e);

        }

        return result;

    }

}
