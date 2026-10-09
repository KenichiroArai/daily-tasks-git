package io.github.kenichiroarai.dailytasks.carryover.domain.aggregator;

import java.util.Collection;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;

/**
 * 持ち越しの解析結果を日別に集計する<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverAggregator {

    /**
     * 解析結果を集計する<br>
     *
     * @param issues
     *               持ち越しの解析結果
     *
     * @return 画面用の持ち越しの集計
     */
    CarryoverSummary aggregate(Collection<CarryoverIssue> issues);

}
