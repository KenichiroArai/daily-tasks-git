package io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.impl;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailySummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;

/**
 * {@link CarryoverAggregatorImpl} のテスト<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings({
    "nls", "static-method"
})
public class CarryoverAggregatorImplTest {

    /**
     * テスト用の解析結果を作成する<br>
     *
     * @param number
     *                 Issue 番号
     * @param date
     *                 日付
     * @param sections
     *                 対象セクション名
     * @param items
     *                 持ち越し項目
     *
     * @return 解析結果
     */
    private static CarryoverIssue createIssue(final int number, final String date, final List<String> sections,
        final List<CarryoverItem> items) {

        final CarryoverIssue result
            = new CarryoverIssue(number, "title", date, "closed", "2026-10-06T00:00:00Z", sections, null, items);
        return result;

    }

    /**
     * テスト用の持ち越し項目を作成する<br>
     *
     * @param name
     *             項目名
     *
     * @return 持ち越し項目
     */
    private static CarryoverItem createItem(final String name) {

        final CarryoverItem result
            = new CarryoverItem(name, "2026-06-18", false, 15, MinutesSource.PARSED, "持ち越し", "raw");
        return result;

    }

    /**
     * aggregate メソッドのテスト - 正常系:対象セクションが現れた日以降を日付順に集計する場合
     */
    @Test
    public void testAggregate_normalFromFirstSection() {

        /* 期待値の定義 */
        final List<String>  expectedDates       = List.of("2026-03-24", "2026-03-25", "2026-03-26");
        final List<Integer> expectedCounts      = List.of(2, 0, 1);
        final List<String>  expectedItems       = List.of("音楽", "国語");
        final int           expectedLatestIssue = 177;

        /* 準備 */
        final List<CarryoverIssue>    testIssues = List.of(
            CarryoverAggregatorImplTest.createIssue(177, "2026-03-26", List.of("負債"),
                List.of(CarryoverAggregatorImplTest.createItem("音楽"))),
            CarryoverAggregatorImplTest.createIssue(176, "2026-03-25", List.of(), List.of()),
            CarryoverAggregatorImplTest.createIssue(175, "2026-03-24", List.of("負債"),
                List.of(CarryoverAggregatorImplTest.createItem("国語"), CarryoverAggregatorImplTest.createItem("音楽"))),
            CarryoverAggregatorImplTest.createIssue(174, "2026-03-23", List.of(), List.of()),
            CarryoverAggregatorImplTest.createIssue(1, null, List.of("負債"), List.of()));
        final CarryoverAggregatorImpl testTarget = new CarryoverAggregatorImpl();

        /* テスト対象の実行 */
        final CarryoverSummary testResult = testTarget.aggregate(testIssues);

        /* 検証の準備 */
        final List<String>  actualDates       = testResult.getDays().stream().map(DailySummary::getDate).toList();
        final List<Integer> actualCounts      = testResult.getDays().stream()
            .map(day -> day.getTotal().getCount()).toList();
        final List<String>  actualItems       = testResult.getItems();
        final int           actualLatestIssue = testResult.getLatestIssue();

        /* 検証の実施 */
        Assertions.assertEquals(expectedDates, actualDates, "日付が一致しません");
        Assertions.assertEquals(expectedCounts, actualCounts, "件数が一致しません");
        Assertions.assertEquals(expectedItems, actualItems, "項目名が一致しません");
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * aggregate メソッドのテスト - 準正常系:解析結果がない場合
     */
    @Test
    public void testAggregate_semiEmpty() {

        /* 期待値の定義 */
        final int expectedLatestIssue = 0;

        /* 準備 */
        final CarryoverAggregatorImpl testTarget = new CarryoverAggregatorImpl();

        /* テスト対象の実行 */
        final CarryoverSummary testResult = testTarget.aggregate(List.of());

        /* 検証の準備 */
        final int     actualLatestIssue = testResult.getLatestIssue();
        final boolean actualDaysEmpty   = testResult.getDays().isEmpty();

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");
        Assertions.assertTrue(actualDaysEmpty, "日別の集計は空になる必要があります");

    }

}
