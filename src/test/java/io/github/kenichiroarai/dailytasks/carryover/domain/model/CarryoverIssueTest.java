package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverIssue} のテスト<br>
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
public class CarryoverIssueTest {

    /**
     * テスト用の持ち越し項目（未チェック、15分）
     */
    private static final CarryoverItem ITEM1 = new CarryoverItem("国語", "2026-06-18", false, 15, MinutesSource.PARSED,
        "持ち越し", "- [ ] 国語2026/06/18（残り時間：15分）");

    /**
     * テスト用の持ち越し項目（チェック済み、8.5分）
     */
    private static final CarryoverItem ITEM2 = new CarryoverItem("音楽", "2026-08-18", true, 8.5, MinutesSource.PARSED,
        "持ち越し", "- [x] 音楽2026/08/18（残り時間：8.5分）");

    /**
     * テスト用の解析結果を作成する<br>
     *
     * @param state
     *                 Issue の状態
     * @param sections
     *                 対象セクション名
     *
     * @return 解析結果
     */
    private static CarryoverIssue createIssue(final String state, final List<String> sections) {

        final CarryoverIssue result = new CarryoverIssue(371, "2026年10月06日のタスク", "2026-10-06", state,
            "2026-10-06T14:23:08Z", sections, Integer.valueOf(2), List.of(CarryoverIssueTest.ITEM1,
                CarryoverIssueTest.ITEM2));
        return result;

    }

    /**
     * getNumber メソッドのテスト - 正常系:Issue 番号を返す場合
     */
    @Test
    public void testGetNumber_normalValue() {

        /* 期待値の定義 */
        final int expectedNumber = 371;

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final int testResult = testTarget.getNumber();

        /* 検証の準備 */
        final int actualNumber = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedNumber, actualNumber, "Issue 番号が一致しません");

    }

    /**
     * getTitle メソッドのテスト - 正常系:タイトルを返す場合
     */
    @Test
    public void testGetTitle_normalValue() {

        /* 期待値の定義 */
        final String expectedTitle = "2026年10月06日のタスク";

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final String testResult = testTarget.getTitle();

        /* 検証の準備 */
        final String actualTitle = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedTitle, actualTitle, "タイトルが一致しません");

    }

    /**
     * getDate メソッドのテスト - 正常系:日付を返す場合
     */
    @Test
    public void testGetDate_normalValue() {

        /* 期待値の定義 */
        final String expectedDate = "2026-10-06";

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final String testResult = testTarget.getDate();

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

    /**
     * getState メソッドのテスト - 正常系:状態を返す場合
     */
    @Test
    public void testGetState_normalValue() {

        /* 期待値の定義 */
        final String expectedState = "closed";

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("closed", List.of("持ち越し"));

        /* テスト対象の実行 */
        final String testResult = testTarget.getState();

        /* 検証の準備 */
        final String actualState = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedState, actualState, "状態が一致しません");

    }

    /**
     * getUpdatedAt メソッドのテスト - 正常系:更新日時を返す場合
     */
    @Test
    public void testGetUpdatedAt_normalValue() {

        /* 期待値の定義 */
        final String expectedUpdatedAt = "2026-10-06T14:23:08Z";

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final String testResult = testTarget.getUpdatedAt();

        /* 検証の準備 */
        final String actualUpdatedAt = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedUpdatedAt, actualUpdatedAt, "更新日時が一致しません");

    }

    /**
     * getSections メソッドのテスト - 正常系:対象セクション名を返す場合
     */
    @Test
    public void testGetSections_normalValue() {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("持ち越し");

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final List<String> testResult = testTarget.getSections();

        /* 検証の準備 */
        final List<String> actualSections = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * getDeclaredCount メソッドのテスト - 正常系:残数を返す場合
     */
    @Test
    public void testGetDeclaredCount_normalValue() {

        /* 期待値の定義 */
        final Integer expectedDeclaredCount = Integer.valueOf(2);

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final Integer testResult = testTarget.getDeclaredCount();

        /* 検証の準備 */
        final Integer actualDeclaredCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDeclaredCount, actualDeclaredCount, "残数が一致しません");

    }

    /**
     * getItems メソッドのテスト - 正常系:持ち越し項目を返す場合
     */
    @Test
    public void testGetItems_normalValue() {

        /* 期待値の定義 */
        final List<CarryoverItem> expectedItems = List.of(CarryoverIssueTest.ITEM1, CarryoverIssueTest.ITEM2);

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final List<CarryoverItem> testResult = testTarget.getItems();

        /* 検証の準備 */
        final List<CarryoverItem> actualItems = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedItems, actualItems, "持ち越し項目が一致しません");

    }

    /**
     * getCount メソッドのテスト - 正常系:持ち越し項目の件数を返す場合
     */
    @Test
    public void testGetCount_normalValue() {

        /* 期待値の定義 */
        final int expectedCount = 2;

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final int testResult = testTarget.getCount();

        /* 検証の準備 */
        final int actualCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");

    }

    /**
     * getMinutes メソッドのテスト - 正常系:残り時間の合計を返す場合
     */
    @Test
    public void testGetMinutes_normalSum() {

        /* 期待値の定義 */
        final double expectedMinutes = 23.5;

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final double testResult = testTarget.getMinutes();

        /* 検証の準備 */
        final double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間の合計が一致しません");

    }

    /**
     * hasSection メソッドのテスト - 正常系:対象セクションがある場合
     */
    @Test
    public void testHasSection_normalExists() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final boolean testResult = testTarget.hasSection();

        /* 検証の準備 */
        final boolean actualHasSection = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualHasSection, "対象セクションがあると判定される必要があります");

    }

    /**
     * hasSection メソッドのテスト - 準正常系:対象セクションがない場合
     */
    @Test
    public void testHasSection_semiNotExists() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of());

        /* テスト対象の実行 */
        final boolean testResult = testTarget.hasSection();

        /* 検証の準備 */
        final boolean actualHasSection = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualHasSection, "対象セクションがないと判定される必要があります");

    }

    /**
     * isOpen メソッドのテスト - 正常系:オープン中の場合
     */
    @Test
    public void testIsOpen_normalOpen() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("open", List.of("持ち越し"));

        /* テスト対象の実行 */
        final boolean testResult = testTarget.isOpen();

        /* 検証の準備 */
        final boolean actualOpen = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualOpen, "オープン中と判定される必要があります");

    }

    /**
     * isOpen メソッドのテスト - 準正常系:クローズ済みの場合
     */
    @Test
    public void testIsOpen_semiClosed() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testTarget = CarryoverIssueTest.createIssue("closed", List.of("持ち越し"));

        /* テスト対象の実行 */
        final boolean testResult = testTarget.isOpen();

        /* 検証の準備 */
        final boolean actualOpen = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualOpen, "クローズ済みと判定される必要があります");

    }

}
