package io.github.kenichiroarai.dailytasks.carryover.application.logic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSourceModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailyTaskIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DefaultMinutesModel;

/**
 * 持ち越しのデータ（Issue、解析結果、集計、標準時間）の取得・保存ロジック<br>
 * <p>
 * application 層が domain 層に求めるインタフェース。実装は domain 層に置く。application 層のモデルで取得・保存の手段を提供し、データの置き場所や形式は実装側に任せる。データの取得元と保存先は呼び出しごとに引数で受け取る。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverIssueLogic {

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
    List<DailyTaskIssueModel> fetchAllIssues(CarryoverSourceModel source) throws IOException;

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
    DefaultMinutesModel loadDefaultMinutes(CarryoverSourceModel source) throws IOException;

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
    Map<Integer, CarryoverIssueModel> loadIssues(CarryoverSourceModel source) throws IOException;

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
    void saveIssue(CarryoverSourceModel source, CarryoverIssueModel issue) throws IOException;

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
    void saveSummary(CarryoverSourceModel source, CarryoverSummaryModel summary) throws IOException;

}
