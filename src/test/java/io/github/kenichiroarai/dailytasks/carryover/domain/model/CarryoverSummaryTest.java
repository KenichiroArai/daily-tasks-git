package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverSummary} のテスト<br>
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
public class CarryoverSummaryTest {

    /**
     * テスト用の日別の集計
     */
    private static final DailySummary DAY = new DailySummary("2026-10-06", 371, null);

    /**
     * テスト用の集計を作成する<br>
     *
     * @return 集計
     */
    private static CarryoverSummary createSummary() {

        final CarryoverSummary result = new CarryoverSummary(371, List.of("音楽", "国語"),
            List.of(CarryoverSummaryTest.DAY));
        return result;

    }

    /**
     * getLatestIssue メソッドのテスト - 正常系:最新の Issue 番号を返す場合
     */
    @Test
    public void testGetLatestIssue_normalValue() {

        /* 期待値の定義 */
        final int expectedLatestIssue = 371;

        /* 準備 */
        final CarryoverSummary testTarget = CarryoverSummaryTest.createSummary();

        /* テスト対象の実行 */
        final int testResult = testTarget.getLatestIssue();

        /* 検証の準備 */
        final int actualLatestIssue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * getItems メソッドのテスト - 正常系:項目名を返す場合
     */
    @Test
    public void testGetItems_normalValue() {

        /* 期待値の定義 */
        final List<String> expectedItems = List.of("音楽", "国語");

        /* 準備 */
        final CarryoverSummary testTarget = CarryoverSummaryTest.createSummary();

        /* テスト対象の実行 */
        final List<String> testResult = testTarget.getItems();

        /* 検証の準備 */
        final List<String> actualItems = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedItems, actualItems, "項目名が一致しません");

    }

    /**
     * getDays メソッドのテスト - 正常系:日別の集計を返す場合
     */
    @Test
    public void testGetDays_normalValue() {

        /* 期待値の定義 */
        final List<DailySummary> expectedDays = List.of(CarryoverSummaryTest.DAY);

        /* 準備 */
        final CarryoverSummary testTarget = CarryoverSummaryTest.createSummary();

        /* テスト対象の実行 */
        final List<DailySummary> testResult = testTarget.getDays();

        /* 検証の準備 */
        final List<DailySummary> actualDays = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDays, actualDays, "日別の集計が一致しません");

    }

}
