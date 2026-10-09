package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link ItemStatDto} のテスト<br>
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
public class ItemStatDtoTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static ItemStatDto createTarget() {

        final ItemStatDto result = new ItemStatDto(2, 30.5, 1, 10.5);
        return result;

    }

    /**
     * getCount メソッドのテスト - 正常系:件数を返す場合
     */
    @Test
    public void testGetCount_normalValue() {

        /* 期待値の定義 */
        final int expectedCount = 2;

        /* 準備 */
        final ItemStatDto testTarget = ItemStatDtoTest.createTarget();

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
        final double expectedMinutes = 30.5;

        /* 準備 */
        final ItemStatDto testTarget = ItemStatDtoTest.createTarget();

        /* テスト対象の実行 */
        final double testResult = testTarget.getMinutes();

        /* 検証の準備 */
        final double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * getCheckedCount メソッドのテスト - 正常系:チェック済みの件数を返す場合
     */
    @Test
    public void testGetCheckedCount_normalValue() {

        /* 期待値の定義 */
        final int expectedCheckedCount = 1;

        /* 準備 */
        final ItemStatDto testTarget = ItemStatDtoTest.createTarget();

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
        final double expectedCheckedMinutes = 10.5;

        /* 準備 */
        final ItemStatDto testTarget = ItemStatDtoTest.createTarget();

        /* テスト対象の実行 */
        final double testResult = testTarget.getCheckedMinutes();

        /* 検証の準備 */
        final double actualCheckedMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedMinutes, actualCheckedMinutes, "チェック済みの残り時間が一致しません");

    }

}