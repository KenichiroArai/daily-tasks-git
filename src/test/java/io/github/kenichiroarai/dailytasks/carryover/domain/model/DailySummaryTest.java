package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link DailySummary} のテスト<br>
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
public class DailySummaryTest {

    /**
     * テスト用の持ち越し項目を作成する<br>
     *
     * @param name
     *                   項目名
     * @param originDate
     *                   持ち越し元の日付
     *
     * @return 持ち越し項目
     */
    private static CarryoverItem createItem(final String name, final String originDate) {

        final CarryoverItem result = new CarryoverItem(name, originDate, false, 15, MinutesSource.PARSED, "持ち越し",
            "raw");
        return result;

    }

    /**
     * テスト用の日別の集計（国語 2 件、音楽 1 件）を作成する<br>
     *
     * @return 日別の集計
     */
    private static DailySummary createSummary() {

        final DailySummary result = new DailySummary("2026-10-06", 371, Integer.valueOf(3));
        result.add(DailySummaryTest.createItem("国語", "2026-06-18"));
        result.add(DailySummaryTest.createItem("国語", "2026-08-01"));
        result.add(DailySummaryTest.createItem("音楽", "2026-08-18"));
        return result;

    }

    /**
     * add メソッドのテスト - 正常系:持ち越し元の日付がある場合は月ごとに集計される
     */
    @Test
    public void testAdd_normalWithOriginDate() {

        /* 期待値の定義 */
        final int expectedMonthCount = 1;

        /* 準備 */
        final DailySummary testTarget = new DailySummary("2026-10-06", 371, null);

        /* テスト対象の実行 */
        testTarget.add(DailySummaryTest.createItem("国語", "2026-06-18"));

        /* 検証の準備 */
        final int actualMonthCount = testTarget.getByOriginMonth().get("2026-06").getCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMonthCount, actualMonthCount, "持ち越し元の月の件数が一致しません");

    }

    /**
     * add メソッドのテスト - 準正常系:持ち越し元の日付がない場合は月ごとに集計されない
     */
    @Test
    public void testAdd_semiWithoutOriginDate() {

        /* 期待値の定義 */

        /* 準備 */
        final DailySummary testTarget = new DailySummary("2026-10-06", 371, null);

        /* テスト対象の実行 */
        testTarget.add(DailySummaryTest.createItem("国語", null));

        /* 検証の準備 */
        final boolean actualEmpty = testTarget.getByOriginMonth().isEmpty();

        /* 検証の実施 */
        Assertions.assertTrue(actualEmpty, "持ち越し元の月ごとの集計は空になる必要があります");

    }

    /**
     * getDate メソッドのテスト - 正常系:日付を返す場合
     */
    @Test
    public void testGetDate_normalValue() {

        /* 期待値の定義 */
        final String expectedDate = "2026-10-06";

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final String testResult = testTarget.getDate();

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

    /**
     * getIssue メソッドのテスト - 正常系:Issue 番号を返す場合
     */
    @Test
    public void testGetIssue_normalValue() {

        /* 期待値の定義 */
        final int expectedIssue = 371;

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final int testResult = testTarget.getIssue();

        /* 検証の準備 */
        final int actualIssue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedIssue, actualIssue, "Issue 番号が一致しません");

    }

    /**
     * getDeclaredCount メソッドのテスト - 正常系:残数を返す場合
     */
    @Test
    public void testGetDeclaredCount_normalValue() {

        /* 期待値の定義 */
        final Integer expectedDeclaredCount = Integer.valueOf(3);

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final Integer testResult = testTarget.getDeclaredCount();

        /* 検証の準備 */
        final Integer actualDeclaredCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDeclaredCount, actualDeclaredCount, "残数が一致しません");

    }

    /**
     * getTotal メソッドのテスト - 正常系:全体の件数を返す場合
     */
    @Test
    public void testGetTotal_normalCount() {

        /* 期待値の定義 */
        final int expectedCount = 3;

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final ItemStat testResult = testTarget.getTotal();

        /* 検証の準備 */
        final int actualCount = testResult.getCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "全体の件数が一致しません");

    }

    /**
     * getByItem メソッドのテスト - 正常系:項目ごとの件数を返す場合
     */
    @Test
    public void testGetByItem_normalCount() {

        /* 期待値の定義 */
        final int expectedCount = 2;

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final int testResult = testTarget.getByItem().get("国語").getCount();

        /* 検証の準備 */
        final int actualCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "項目ごとの件数が一致しません");

    }

    /**
     * getByOriginMonth メソッドのテスト - 正常系:持ち越し元の月を返す場合
     */
    @Test
    public void testGetByOriginMonth_normalKeys() {

        /* 期待値の定義 */
        final Set<String> expectedMonths = Set.of("2026-06", "2026-08");

        /* 準備 */
        final DailySummary testTarget = DailySummaryTest.createSummary();

        /* テスト対象の実行 */
        final Set<String> testResult = testTarget.getByOriginMonth().keySet();

        /* 検証の準備 */
        final Set<String> actualMonths = Set.copyOf(testResult);

        /* 検証の実施 */
        Assertions.assertEquals(expectedMonths, actualMonths, "持ち越し元の月が一致しません");

    }

}
