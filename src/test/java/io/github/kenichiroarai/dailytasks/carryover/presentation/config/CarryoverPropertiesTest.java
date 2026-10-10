package io.github.kenichiroarai.dailytasks.carryover.presentation.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverProperties} のテスト<br>
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
public class CarryoverPropertiesTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverProperties createTarget() {

        final CarryoverProperties result = new CarryoverProperties("owner/repo", "secret-token", "docs/data",
            "config/default-minutes.json", 10);
        return result;

    }

    /**
     * getRepository メソッドのテスト - 正常系:コンストラクタで設定した値を返す
     */
    @Test
    public void testGetRepository_normalValue() {

        /* 期待値の定義 */
        final String expectedRepository = "owner/repo";

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getRepository();

        /* 検証の準備 */
        final String actualRepository = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "リポジトリが一致しません");

    }

    /**
     * getToken メソッドのテスト - 正常系:コンストラクタで設定した値を返す
     */
    @Test
    public void testGetToken_normalValue() {

        /* 期待値の定義 */
        final String expectedToken = "secret-token";

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getToken();

        /* 検証の準備 */
        final String actualToken = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");

    }

    /**
     * getDataDir メソッドのテスト - 正常系:コンストラクタで設定した値を返す
     */
    @Test
    public void testGetDataDir_normalValue() {

        /* 期待値の定義 */
        final String expectedDataDir = "docs/data";

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getDataDir();

        /* 検証の準備 */
        final String actualDataDir = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDataDir, actualDataDir, "出力先が一致しません");

    }

    /**
     * getDefaultMinutesFile メソッドのテスト - 正常系:コンストラクタで設定した値を返す
     */
    @Test
    public void testGetDefaultMinutesFile_normalValue() {

        /* 期待値の定義 */
        final String expectedDefaultMinutesFile = "config/default-minutes.json";

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getDefaultMinutesFile();

        /* 検証の準備 */
        final String actualDefaultMinutesFile = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDefaultMinutesFile, actualDefaultMinutesFile, "標準時間の設定ファイルが一致しません");

    }

    /**
     * getRecentCount メソッドのテスト - 正常系:コンストラクタで設定した値を返す
     */
    @Test
    public void testGetRecentCount_normalValue() {

        /* 期待値の定義 */
        final int expectedRecentCount = 10;

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final int testResult = testTarget.getRecentCount();

        /* 検証の準備 */
        final int actualRecentCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRecentCount, actualRecentCount, "最新の Issue の件数が一致しません");

    }

    /**
     * toString メソッドのテスト - 正常系:トークン以外の値を含む
     */
    @Test
    public void testToString_normalValue() {

        /* 期待値の定義 */
        final String expectedString = "CarryoverProperties[repository=owner/repo, dataDir=docs/data, "
            + "defaultMinutesFile=config/default-minutes.json, recentCount=10]";

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.toString();

        /* 検証の準備 */
        final String actualString = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedString, actualString, "文字列表現が一致しません");

    }

    /**
     * toString メソッドのテスト - 正常系:トークンを含まない
     */
    @Test
    public void testToString_normalNoToken() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverProperties testTarget = CarryoverPropertiesTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.toString();

        /* 検証の準備 */
        final boolean actualContainsToken = testResult.contains("secret-token");

        /* 検証の実施 */
        Assertions.assertFalse(actualContainsToken, "トークンが含まれてはいけません");

    }

}
