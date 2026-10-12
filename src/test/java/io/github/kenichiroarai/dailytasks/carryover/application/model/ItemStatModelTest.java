package io.github.kenichiroarai.dailytasks.carryover.application.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.application.types.MinutesSourceTypes;

/**
 * {@link ItemStatModel} のテスト<br>
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
public class ItemStatModelTest {

    /**
     * テスト用の持ち越し項目を作成する<br>
     *
     * @param checked
     *                チェック済みか
     * @param minutes
     *                残り時間（分）
     *
     * @return 持ち越し項目
     */
    private static CarryoverItemModel createItem(final boolean checked, final double minutes) {

        final CarryoverItemModel result
            = new CarryoverItemModel("国語", "2026-06-18", checked, minutes, MinutesSourceTypes.PARSED, "持ち越し", "raw");
        return result;

    }

    /**
     * テスト用の集計値（未チェック 15 分とチェック済み 8.5 分）を作成する<br>
     *
     * @return 集計値
     */
    private static ItemStatModel createStat() {

        final ItemStatModel result = new ItemStatModel();
        result.add(ItemStatModelTest.createItem(false, 15));
        result.add(ItemStatModelTest.createItem(true, 8.5));
        return result;

    }

    /**
     * add メソッドのテスト - 正常系:チェック済みの項目を加える場合
     */
    @Test
    public void testAdd_normalChecked() {

        /* 期待値の定義 */
        final int expectedCheckedCount = 1;

        /* 準備 */
        final ItemStatModel testTarget = new ItemStatModel();

        /* テスト対象の実行 */
        testTarget.add(ItemStatModelTest.createItem(true, 15));

        /* 検証の準備 */
        final int actualCheckedCount = testTarget.getCheckedCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedCount, actualCheckedCount, "チェック済みの件数が一致しません");

    }

    /**
     * add メソッドのテスト - 正常系:未チェックの項目を加える場合
     */
    @Test
    public void testAdd_normalUnchecked() {

        /* 期待値の定義 */
        final int expectedCheckedCount = 0;

        /* 準備 */
        final ItemStatModel testTarget = new ItemStatModel();

        /* テスト対象の実行 */
        testTarget.add(ItemStatModelTest.createItem(false, 15));

        /* 検証の準備 */
        final int actualCheckedCount = testTarget.getCheckedCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedCount, actualCheckedCount, "チェック済みの件数が一致しません");

    }

    /**
     * getCheckedCount メソッドのテスト - 正常系:チェック済みの件数を返す場合
     */
    @Test
    public void testGetCheckedCount_normalValue() {

        /* 期待値の定義 */
        final int expectedCheckedCount = 1;

        /* 準備 */
        final ItemStatModel testTarget = ItemStatModelTest.createStat();

        /* テスト対象の実行 */
        final int testResult = testTarget.getCheckedCount();

        /* 検証の準備 */
        final int actualCheckedCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedCount, actualCheckedCount, "チェック済みの件数が一致しません");

    }

    /**
     * getCheckedMinutes メソッドのテスト - 正常系:チェック済みの残り時間を返す場合
     */
    @Test
    public void testGetCheckedMinutes_normalValue() {

        /* 期待値の定義 */
        final double expectedCheckedMinutes = 8.5;

        /* 準備 */
        final ItemStatModel testTarget = ItemStatModelTest.createStat();

        /* テスト対象の実行 */
        final double testResult = testTarget.getCheckedMinutes();

        /* 検証の準備 */
        final double actualCheckedMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedMinutes, actualCheckedMinutes, "チェック済みの残り時間が一致しません");

    }

    /**
     * getCount メソッドのテスト - 正常系:件数を返す場合
     */
    @Test
    public void testGetCount_normalValue() {

        /* 期待値の定義 */
        final int expectedCount = 2;

        /* 準備 */
        final ItemStatModel testTarget = ItemStatModelTest.createStat();

        /* テスト対象の実行 */
        final int testResult = testTarget.getCount();

        /* 検証の準備 */
        final int actualCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");

    }

    /**
     * getMinutes メソッドのテスト - 正常系:残り時間を返す場合
     */
    @Test
    public void testGetMinutes_normalValue() {

        /* 期待値の定義 */
        final double expectedMinutes = 23.5;

        /* 準備 */
        final ItemStatModel testTarget = ItemStatModelTest.createStat();

        /* テスト対象の実行 */
        final double testResult = testTarget.getMinutes();

        /* 検証の準備 */
        final double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

}
