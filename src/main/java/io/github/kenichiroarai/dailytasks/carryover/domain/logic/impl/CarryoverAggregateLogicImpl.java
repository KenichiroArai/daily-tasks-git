package io.github.kenichiroarai.dailytasks.carryover.domain.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.application.logic.CarryoverAggregateLogic;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverItemModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailySummaryModel;

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
public class CarryoverAggregateLogicImpl implements CarryoverAggregateLogic {

    /**
     * 解析結果を集計する<br>
     *
     * @param issues
     *               持ち越しの解析結果
     *
     * @return 画面用の持ち越しの集計
     */
    @Override
    public CarryoverSummaryModel aggregate(final Collection<CarryoverIssueModel> issues) {

        CarryoverSummaryModel result = null;

        /* 日付のある Issue を日付順に並べる */
        final List<CarryoverIssueModel> sorted = issues.stream().filter(issue -> issue.getDate() != null)
            .sorted(Comparator.comparing(CarryoverIssueModel::getDate).thenComparingInt(CarryoverIssueModel::getNumber)).toList();

        /* 日別の集計 */
        final List<DailySummaryModel> days       = new ArrayList<>();
        final Map<String, Integer>    itemCounts = new HashMap<>();
        boolean                       tracking   = false;

        for (final CarryoverIssueModel issue : sorted) {

            tracking = tracking || issue.hasSection();

            if (!tracking) {

                continue;

            }

            final DailySummaryModel day = new DailySummaryModel(issue.getDate(), issue.getNumber(), issue.getDeclaredCount());

            for (final CarryoverItemModel item : issue.getItems()) {

                day.add(item);
                itemCounts.merge(item.getName(), 1, Integer::sum);

            }

            days.add(day);

        }

        /* 項目名を延べ件数の多い順に並べる */
        final List<String> items = itemCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
            .map(Map.Entry::getKey).toList();

        final int latestIssue = issues.stream().mapToInt(CarryoverIssueModel::getNumber).max().orElse(0);

        result = new CarryoverSummaryModel(latestIssue, items, days);
        return result;

    }

}
