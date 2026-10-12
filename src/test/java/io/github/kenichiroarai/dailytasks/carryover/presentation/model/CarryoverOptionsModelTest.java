package io.github.kenichiroarai.dailytasks.carryover.presentation.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverOptionsModel} のテスト<br>
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
public class CarryoverOptionsModelTest {

    /**
     * CarryoverOptionsModel コンストラクタのテスト - 正常系:全件モードの既定値は false
     */
    @Test
    public void testCarryoverOptions_normalDefaultFull() {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();

        /* 検証の準備 */
        final boolean actualFull = testTarget.isFull();

        /* 検証の実施 */
        Assertions.assertFalse(actualFull, "全件モードの既定値は false である必要があります");

    }

    /**
     * CarryoverOptionsModel コンストラクタのテスト - 正常系:使い方の表示の既定値は false
     */
    @Test
    public void testCarryoverOptions_normalDefaultHelp() {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();

        /* 検証の準備 */
        final boolean actualHelp = testTarget.isHelp();

        /* 検証の実施 */
        Assertions.assertFalse(actualHelp, "使い方の表示の既定値は false である必要があります");

    }

    /**
     * setFull メソッドのテスト - 正常系:false を設定した場合
     */
    @Test
    public void testSetFull_normalFalse() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();
        testTarget.setFull(true);

        /* テスト対象の実行 */
        testTarget.setFull(false);

        /* 検証の準備 */
        final boolean actualFull = testTarget.isFull();

        /* 検証の実施 */
        Assertions.assertFalse(actualFull, "差分モードである必要があります");

    }

    /**
     * setFull メソッドのテスト - 正常系:true を設定した場合
     */
    @Test
    public void testSetFull_normalTrue() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();

        /* テスト対象の実行 */
        testTarget.setFull(true);

        /* 検証の準備 */
        final boolean actualFull = testTarget.isFull();

        /* 検証の実施 */
        Assertions.assertTrue(actualFull, "全件モードである必要があります");

    }

    /**
     * setHelp メソッドのテスト - 正常系:false を設定した場合
     */
    @Test
    public void testSetHelp_normalFalse() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();
        testTarget.setHelp(true);

        /* テスト対象の実行 */
        testTarget.setHelp(false);

        /* 検証の準備 */
        final boolean actualHelp = testTarget.isHelp();

        /* 検証の実施 */
        Assertions.assertFalse(actualHelp, "使い方を表示しない必要があります");

    }

    /**
     * setHelp メソッドのテスト - 正常系:true を設定した場合
     */
    @Test
    public void testSetHelp_normalTrue() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverOptionsModel testTarget = new CarryoverOptionsModel();

        /* テスト対象の実行 */
        testTarget.setHelp(true);

        /* 検証の準備 */
        final boolean actualHelp = testTarget.isHelp();

        /* 検証の実施 */
        Assertions.assertTrue(actualHelp, "使い方を表示する必要があります");

    }

}
