package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverSource} のテスト<br>
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
public class CarryoverSourceTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverSource createTarget() {

        final CarryoverSource result = new CarryoverSource("owner/repo", "test-token", Path.of("docs", "data"),
            Path.of("config", "default-minutes.json"));
        return result;

    }

    /**
     * getRepository メソッドのテスト - 正常系:対象リポジトリを返す場合
     */
    @Test
    public void testGetRepository_normalValue() {

        /* 期待値の定義 */
        final String expectedRepository = "owner/repo";

        /* 準備 */
        final CarryoverSource testTarget = CarryoverSourceTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getRepository();

        /* 検証の準備 */
        final String actualRepository = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "対象リポジトリが一致しません");

    }

    /**
     * getToken メソッドのテスト - 正常系:トークンを返す場合
     */
    @Test
    public void testGetToken_normalValue() {

        /* 期待値の定義 */
        final String expectedToken = "test-token";

        /* 準備 */
        final CarryoverSource testTarget = CarryoverSourceTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getToken();

        /* 検証の準備 */
        final String actualToken = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");

    }

    /**
     * getDataDir メソッドのテスト - 正常系:保存先のディレクトリを返す場合
     */
    @Test
    public void testGetDataDir_normalValue() {

        /* 期待値の定義 */
        final Path expectedDataDir = Path.of("docs", "data");

        /* 準備 */
        final CarryoverSource testTarget = CarryoverSourceTest.createTarget();

        /* テスト対象の実行 */
        final Path testResult = testTarget.getDataDir();

        /* 検証の準備 */
        final Path actualDataDir = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDataDir, actualDataDir, "保存先のディレクトリが一致しません");

    }

    /**
     * getDefaultMinutesFile メソッドのテスト - 正常系:標準時間の設定ファイルを返す場合
     */
    @Test
    public void testGetDefaultMinutesFile_normalValue() {

        /* 期待値の定義 */
        final Path expectedDefaultMinutesFile = Path.of("config", "default-minutes.json");

        /* 準備 */
        final CarryoverSource testTarget = CarryoverSourceTest.createTarget();

        /* テスト対象の実行 */
        final Path testResult = testTarget.getDefaultMinutesFile();

        /* 検証の準備 */
        final Path actualDefaultMinutesFile = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDefaultMinutesFile, actualDefaultMinutesFile, "標準時間の設定ファイルが一致しません");

    }

    /**
     * toString メソッドのテスト - 正常系:トークンを含めない場合
     */
    @Test
    public void testToString_normalWithoutToken() {

        /* 期待値の定義 */
        final String expectedString = "CarryoverSource[repository=owner/repo, dataDir=" + Path.of("docs", "data")
            + ", defaultMinutesFile=" + Path.of("config", "default-minutes.json") + "]";

        /* 準備 */
        final CarryoverSource testTarget = CarryoverSourceTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.toString();

        /* 検証の準備 */
        final String actualString = testResult;
        final boolean actualHasToken = actualString.contains("test-token");

        /* 検証の実施 */
        Assertions.assertEquals(expectedString, actualString, "文字列表現が一致しません");
        Assertions.assertFalse(actualHasToken, "トークンを含めない必要があります");

    }

}