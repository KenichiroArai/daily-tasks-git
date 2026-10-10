package io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.CarryoverAggregator;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailySummary;

/**
 * 持ち越しの解析結果を日別に集計する実装<br>
 * <p>
 * 対象セクションが最初に現れた Issue 以降を集計対象とする。それ以降で対象セクションがない日は 0 件として扱う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
public class CarryoverAggregatorImpl implements CarryoverAggregator {

    /**
     * 解析結果を集計する<br>
     *
     * @param issues
     *               持ち越しの解析結果
     *
     * @return 画面用の持ち越しの集計
     */
    @Override
    public CarryoverSummary aggregate(final Collection<CarryoverIssue> issues) {

        CarryoverSummary result = null;

        /* 日付のある Issue を日付順に並べる */
        final List<CarryoverIssue> sorted = issues.stream().filter(issue -> issue.getDate() != null)
            .sorted(Comparator.comparing(CarryoverIssue::getDate).thenComparingInt(CarryoverIssue::getNumber))
            .toList();

        /* 日別の集計 */
        final List<DailySummary> days = new ArrayList<>();
        final Map<String, Integer> itemCounts = new HashMap<>();
        boolean tracking = false;

        for (final CarryoverIssue issue : sorted) {

            tracking = tracking || issue.hasSection();

            if (!tracking) {

                continue;

            }

            final DailySummary day = new DailySummary(issue.getDate(), issue.getNumber(), issue.getDeclaredCount());

            for (final CarryoverItem item : issue.getItems()) {

                day.add(item);
                itemCounts.merge(item.getName(), 1, Integer::sum);

            }

            days.add(day);

        }

        /* 項目名を延べ件数の多い順に並べる */
        final List<String> items = itemCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer> comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
            .map(Map.Entry::getKey).toList();

        final int latestIssue = issues.stream().mapToInt(CarryoverIssue::getNumber).max().orElse(0);

        result = new CarryoverSummary(latestIssue, items, days);
        return result;

    }

}
