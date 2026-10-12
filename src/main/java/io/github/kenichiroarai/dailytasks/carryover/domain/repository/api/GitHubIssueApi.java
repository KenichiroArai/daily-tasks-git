package io.github.kenichiroarai.dailytasks.carryover.domain.repository.api;

import java.io.IOException;
import java.util.List;

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubSettingsDto;

/**
 * GitHub の Issue を取得する外部 API<br>
 * <p>
 * domain 層が repository 層に求めるインタフェース。実装は repository 層に置く。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface GitHubIssueApi {

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
    List<GitHubIssueDto> fetchAllIssues(GitHubSettingsDto settings) throws IOException;

}
