package io.github.kenichiroarai.dailytasks.carryover.domain.parser;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;

/**
 * 日々のタスク Issue から持ち越し項目を解析する<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverParser {

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
    CarryoverIssue parse(DailyTaskIssue issue, DefaultMinutes defaultMinutes);

}
