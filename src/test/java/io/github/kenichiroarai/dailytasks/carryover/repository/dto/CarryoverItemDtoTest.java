package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverItemDto} のテスト<br>
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
public class CarryoverItemDtoTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverItemDto createTarget() {

        final CarryoverItemDto result = new CarryoverItemDto("音楽", "2026-08-18", true, 11.5, "parsed", "持ち越し",
            "- [x] 音楽2026/08/18（残り時間：11.5分）");
        return result;

    }

    /**
     * getName メソッドのテスト - 正常系:項目名を返す場合
     */
    @Test
    public void testGetName_normalValue() {

        /* 期待値の定義 */
        final String expectedName = "音楽";

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getName();

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * getOriginDate メソッドのテスト - 正常系:持ち越し元の日付を返す場合
     */
    @Test
    public void testGetOriginDate_normalValue() {

        /* 期待値の定義 */
        final String expectedOriginDate = "2026-08-18";

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getOriginDate();

        /* 検証の準備 */
        final String actualOriginDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedOriginDate, actualOriginDate, "持ち越し元の日付が一致しません");

    }

    /**
     * isChecked メソッドのテスト - 正常系:チェック済みを返す場合
     */
    @Test
    public void testIsChecked_normalChecked() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final boolean testResult = testTarget.isChecked();

        /* 検証の準備 */
        final boolean actualChecked = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualChecked, "チェック済みになっていません");

    }

    /**
     * getMinutes メソッドのテスト - 正常系:残り時間を返す場合
     */
    @Test
    public void testGetMinutes_normalValue() {

        /* 期待値の定義 */
        final double expectedMinutes = 11.5;

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final double testResult = testTarget.getMinutes();

        /* 検証の準備 */
        final double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * getMinutesSource メソッドのテスト - 正常系:残り時間の取得元を返す場合
     */
    @Test
    public void testGetMinutesSource_normalValue() {

        /* 期待値の定義 */
        final String expectedMinutesSource = "parsed";

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getMinutesSource();

        /* 検証の準備 */
        final String actualMinutesSource = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * getSection メソッドのテスト - 正常系:セクション名を返す場合
     */
    @Test
    public void testGetSection_normalValue() {

        /* 期待値の定義 */
        final String expectedSection = "持ち越し";

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getSection();

        /* 検証の準備 */
        final String actualSection = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSection, actualSection, "セクション名が一致しません");

    }

    /**
     * getRaw メソッドのテスト - 正常系:元の行を返す場合
     */
    @Test
    public void testGetRaw_normalValue() {

        /* 期待値の定義 */
        final String expectedRaw = "- [x] 音楽2026/08/18（残り時間：11.5分）";

        /* 準備 */
        final CarryoverItemDto testTarget = CarryoverItemDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getRaw();

        /* 検証の準備 */
        final String actualRaw = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRaw, actualRaw, "元の行が一致しません");

    }

}