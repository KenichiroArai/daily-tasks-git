package io.github.kenichiroarai.dailytasks.carryover.repository.github;

import java.io.IOException;
import java.util.List;

import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;

/**
 * GitHub の Issue を取得するリポジトリ<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface GitHubIssueRepository {

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
