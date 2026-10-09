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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;

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
public class GitHubIssueRepositoryImpl implements GitHubIssueRepository {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubIssueRepositoryImpl.class);

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
     * GitHub API の接続設定
     */
    private final GitHubSettingsDto settings;

    /**
     * JSON の変換
     */
    private final ObjectMapper objectMapper;

    /**
     * コンストラクタ<br>
     *
     * @param settings
     *                 GitHub API の接続設定
     */
    public GitHubIssueRepositoryImpl(final GitHubSettingsDto settings) {

        this(HttpClient::newHttpClient, GitHubIssueRepositoryImpl.API_BASE_URL, settings);

    }

    /**
     * HTTP クライアントの生成と API のベース URL を指定するコンストラクタ<br>
     *
     * @param httpClientSupplier
     *                           HTTP クライアントの生成
     * @param apiBaseUrl
     *                           API のベース URL（例: https://api.github.com）
     * @param settings
     *                           GitHub API の接続設定
     */
    public GitHubIssueRepositoryImpl(final Supplier<HttpClient> httpClientSupplier, final String apiBaseUrl,
        final GitHubSettingsDto settings) {

        this.httpClientSupplier = httpClientSupplier;
        this.apiBaseUrl = apiBaseUrl;
        this.settings = settings;
        this.objectMapper = new ObjectMapper();

    }

    /**
     * すべての Issue（オープン・クローズ済み）を取得する<br>
     * <p>
     * プルリクエストは除外する。
     * </p>
     *
     * @return Issue（作成順）
     *
     * @throws IOException
     *                     通信に失敗した場合、または応答が不正な場合
     */
    @Override
    public List<GitHubIssueDto> fetchAllIssues() throws IOException {

        final List<GitHubIssueDto> result = new ArrayList<>();

        int page = 1;
        int pageSize;

        try (HttpClient httpClient = this.httpClientSupplier.get()) {

            do {

                /* 1 ページ分の取得 */
                final JsonNode issues = this.fetchPage(httpClient, page);
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

        GitHubIssueRepositoryImpl.LOGGER.info("Issue を {} 件取得しました（{} ページ）", result.size(), page - 1);
        return result;

    }

    /**
     * 1 ページ分の Issue を取得する<br>
     *
     * @param httpClient
     *                   HTTP クライアント
     * @param page
     *                   ページ番号（1 始まり）
     *
     * @return Issue の配列
     *
     * @throws IOException
     *                     通信に失敗した場合、または応答が不正な場合
     */
    private JsonNode fetchPage(final HttpClient httpClient, final int page) throws IOException {

        JsonNode result = null;

        /* リクエストの作成 */
        final URI uri = URI.create(String.format("%s/repos/%s/issues?state=all&sort=created&direction=asc&per_page=%d&page=%d",
            this.apiBaseUrl, this.settings.getRepository(), GitHubIssueRepositoryImpl.PER_PAGE, page));
        final HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(GitHubIssueRepositoryImpl.TIMEOUT)
            .header("Accept", "application/vnd.github+json").header("X-GitHub-Api-Version", "2022-11-28").GET();

        if (this.hasToken()) {

            builder.header("Authorization", "Bearer " + this.settings.getToken());

        }

        /* 送信と応答の検証 */
        final HttpResponse<String> response = GitHubIssueRepositoryImpl.send(httpClient, builder.build());

        if (response.statusCode() != GitHubIssueRepositoryImpl.STATUS_OK) {

            throw new IOException(String.format("GitHub API の呼び出しに失敗しました: status=%d, uri=%s",
                response.statusCode(), uri));

        }

        result = this.objectMapper.readTree(response.body());

        if (!result.isArray()) {

            throw new IOException(String.format("GitHub API の応答が配列ではありません: uri=%s", uri));

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
    private static HttpResponse<String> send(final HttpClient httpClient, final HttpRequest request)
        throws IOException {

        HttpResponse<String> result = null;

        try {

            result = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        } catch (final InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new IOException("GitHub API の呼び出しが中断されました", e);

        }

        return result;

    }

    /**
     * トークンが指定されているかを返す<br>
     *
     * @return true：指定されている、false：指定されていない
     */
    private boolean hasToken() {

        boolean result = false;
        final String token = this.settings.getToken();

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

        result = value.asText();
        return result;

    }

}
