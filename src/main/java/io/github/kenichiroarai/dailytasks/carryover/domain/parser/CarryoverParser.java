package io.github.kenichiroarai.dailytasks.carryover.domain.parser;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;

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
     *              日々のタスク Issue
     *
     * @return 持ち越しの解析結果
     */
    CarryoverIssue parse(DailyTaskIssue issue);

}
