package io.github.kenichiroarai.dailytasks.carryover.repository;

import java.io.IOException;
import java.util.Map;

import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverSummaryDto;

/**
 * 持ち越しの JSON ファイル（Issue ごとの解析結果と画面用の集計）を読み書きするリポジトリ<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverDataRepository {

    /**
     * 保存済みの Issue ごとの解析結果を読み込む<br>
     *
     * @return Issue 番号と解析結果の対応。保存先がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    Map<Integer, CarryoverIssueDto> loadIssues() throws IOException;

    /**
     * Issue ごとの解析結果を保存する<br>
     *
     * @param issue
     *              解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveIssue(CarryoverIssueDto issue) throws IOException;

    /**
     * 画面用の集計を保存する<br>
     *
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveSummary(CarryoverSummaryDto summary) throws IOException;

}
