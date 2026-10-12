package io.github.kenichiroarai.dailytasks.carryover.application.logic;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailyTaskIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DefaultMinutesModel;

/**
 * 日々のタスク Issue から持ち越し項目を解析する<br>
 * <p>
 * application 層が domain 層に求めるインタフェース。実装は domain 層に置く。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverParseLogic {

    /**
     * Issue を解析する<br>
     *
     * @param issue
     *                       日々のタスク Issue
     * @param defaultMinutes
     *                       時間表記がない行を補完する項目ごとの標準時間
     *
     * @return 持ち越しの解析結果
     */
    CarryoverIssueModel parse(DailyTaskIssueModel issue, DefaultMinutesModel defaultMinutes);

}
