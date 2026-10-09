package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

/**
 * GitHub API の接続設定の DTO<br>
 * <p>
 * トークンはログに出さないよう、{@link #toString()} に含めない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class GitHubSettingsDto {

    /**
     * 対象リポジトリ（owner/name）
     */
    private final String repository;

    /**
     * API トークン。未指定の場合は null
     */
    private final String token;

    /**
     * コンストラクタ<br>
     *
     * @param repository
     *                   対象リポジトリ（owner/name）
     * @param token
     *                   API トークン。未指定の場合は null
     */
    public GitHubSettingsDto(final String repository, final String token) {

        this.repository = repository;
        this.token = token;

    }

    /**
     * 対象リポジトリを返す<br>
     *
     * @return 対象リポジトリ（owner/name）
     */
    public String getRepository() {

        final String result = this.repository;
        return result;

    }

    /**
     * API トークンを返す<br>
     *
     * @return API トークン。未指定の場合は null
     */
    public String getToken() {

        final String result = this.token;
        return result;

    }

    /**
     * 文字列表現を返す<br>
     * <p>
     * トークンは含めない。
     * </p>
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {

        final String result = String.format("GitHubSettingsDto[repository=%s]", this.repository);
        return result;

    }

}
