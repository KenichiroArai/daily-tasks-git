package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link MinutesSource} のテスト<br>
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
public class MinutesSourceTest {

    /**
     * getValue メソッドのテスト - 正常系:本文から取得した場合の値
     */
    @Test
    public void testGetValue_normalParsed() {

        /* 期待値の定義 */
        final String expectedValue = "parsed";

        /* 準備 */
        final MinutesSource testTarget = MinutesSource.PARSED;

        /* テスト対象の実行 */
        final String testResult = testTarget.getValue();

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "JSON での値が一致しません");

    }

    /**
     * getValue メソッドのテスト - 正常系:標準時間で補完した場合の値
     */
    @Test
    public void testGetValue_normalDefault() {

        /* 期待値の定義 */
        final String expectedValue = "default";

        /* 準備 */
        final MinutesSource testTarget = MinutesSource.DEFAULT;

        /* テスト対象の実行 */
        final String testResult = testTarget.getValue();

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "JSON での値が一致しません");

    }

    /**
     * getValue メソッドのテスト - 正常系:標準時間が未登録の場合の値
     */
    @Test
    public void testGetValue_normalUnknown() {

        /* 期待値の定義 */
        final String expectedValue = "unknown";

        /* 準備 */
        final MinutesSource testTarget = MinutesSource.UNKNOWN;

        /* テスト対象の実行 */
        final String testResult = testTarget.getValue();

        /* 検証の準備 */
        final String actualValue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "JSON での値が一致しません");

    }

    /**
     * fromValue メソッドのテスト - 正常系:保存するときの値から取得元を返す場合
     */
    @Test
    public void testFromValue_normalDefault() {

        /* 期待値の定義 */
        final MinutesSource expectedSource = MinutesSource.DEFAULT;

        /* 準備 */
        final String testValue = "default";

        /* テスト対象の実行 */
        final MinutesSource testResult = MinutesSource.fromValue(testValue);

        /* 検証の準備 */
        final MinutesSource actualSource = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSource, actualSource, "残り時間の取得元が一致しません");

    }

    /**
     * fromValue メソッドのテスト - 準正常系:不明な値の場合
     */
    @Test
    public void testFromValue_semiUnknownValue() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な残り時間の取得元です: xxx";

        /* 準備 */
        final String testValue = "xxx";

        /* テスト対象の実行 */
        final IllegalArgumentException testException = Assertions.assertThrows(IllegalArgumentException.class,
            () -> MinutesSource.fromValue(testValue));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

}
