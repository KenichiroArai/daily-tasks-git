package io.github.kenichiroarai.dailytasks.carryover.domain.repository.dao;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverSummaryDto;

/**
 * 持ち越しのデータ（Issue ごとの解析結果と画面用の集計）を読み書きする DAO<br>
 * <p>
 * domain 層が repository 層に求めるインタフェース。実装は repository 層に置き、保存の形式（JSON ファイルなど）は実装側に任せる。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverDataDao {

    /**
     * 保存済みの Issue ごとの解析結果を読み込む<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     *
     * @return Issue 番号と解析結果の対応。保存先がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    Map<Integer, CarryoverIssueDto> loadIssues(Path dataDir) throws IOException;

    /**
     * Issue ごとの解析結果を保存する<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     * @param issue
     *                解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveIssue(Path dataDir, CarryoverIssueDto issue) throws IOException;

    /**
     * 画面用の集計を保存する<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveSummary(Path dataDir, CarryoverSummaryDto summary) throws IOException;

}
