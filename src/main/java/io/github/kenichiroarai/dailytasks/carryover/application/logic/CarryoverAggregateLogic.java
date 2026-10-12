package io.github.kenichiroarai.dailytasks.carryover.application.logic;

import java.util.Collection;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;

/**
 * 持ち越しの解析結果を日別に集計する<br>
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
public interface CarryoverAggregateLogic {

    /**
     * 解析結果を集計する<br>
     *
     * @param issues
     *               持ち越しの解析結果
     *
     * @return 画面用の持ち越しの集計
     */
    CarryoverSummaryModel aggregate(Collection<CarryoverIssueModel> issues);

}
