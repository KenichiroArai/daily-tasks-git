package io.github.kenichiroarai.dailytasks.carryover.application.model;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultMinutesModel} のテスト<br>
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
public class DefaultMinutesModelTest {

    /**
     * find メソッドのテスト - 正常系:登録済みの項目の場合
     */
    @Test
    public void testFind_normalRegistered() {

        /* 期待値の定義 */
        final Double expectedMinutes = (double) 15;

        /* 準備 */
        final DefaultMinutesModel testTarget = new DefaultMinutesModel(Map.of("国語", (double) 15));

        /* テスト対象の実行 */
        final Double testResult = testTarget.find("国語");

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "標準時間が一致しません");

    }

    /**
     * find メソッドのテスト - 準正常系:未登録の項目の場合
     */
    @Test
    public void testFind_semiNotRegistered() {

        /* 期待値の定義 */

        /* 準備 */
        final DefaultMinutesModel testTarget = new DefaultMinutesModel(Map.of("国語", (double) 15));

        /* テスト対象の実行 */
        final Double testResult = testTarget.find("英語");

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualMinutes, "未登録の項目は null になる必要があります");

    }

}
