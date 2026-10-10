package io.github.kenichiroarai.dailytasks.carryover.domain.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;

/**
 * 持ち越しのデータ（Issue、解析結果、集計、標準時間）の取得・保存サービス<br>
 * <p>
 * application 層に domain のモデルで取得・保存の手段を提供する。データの置き場所や形式は repository 層に任せる。データの取得元と保存先は呼び出しごとに引数で受け取る。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverIssueService {

    /**
     * すべての日々のタスク Issue を取得する<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return 日々のタスク Issue（作成順）
     *
     * @throws IOException
     *                     取得に失敗した場合
     */
    List<DailyTaskIssue> fetchAllIssues(CarryoverSource source) throws IOException;

    /**
     * 保存済みの持ち越しの解析結果を読み込む<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return Issue 番号と解析結果の対応。保存されていない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    Map<Integer, CarryoverIssue> loadIssues(CarryoverSource source) throws IOException;

    /**
     * 持ち越しの解析結果を保存する<br>
     *
     * @param source
     *               データの取得元と保存先
     * @param issue
     *               持ち越しの解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveIssue(CarryoverSource source, CarryoverIssue issue) throws IOException;

    /**
     * 画面用の集計を保存する<br>
     *
     * @param source
     *                データの取得元と保存先
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    void saveSummary(CarryoverSource source, CarryoverSummary summary) throws IOException;

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return 項目ごとの標準時間。設定がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    DefaultMinutes loadDefaultMinutes(CarryoverSource source) throws IOException;

}
