package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link GitHubSettingsDto} のテスト<br>
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
public class GitHubSettingsDtoTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static GitHubSettingsDto createTarget() {

        final GitHubSettingsDto result = new GitHubSettingsDto("owner/repo", "test-token");
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
        final GitHubSettingsDto testTarget = GitHubSettingsDtoTest.createTarget();

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
        final GitHubSettingsDto testTarget = GitHubSettingsDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getToken();

        /* 検証の準備 */
        final String actualToken = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");

    }

    /**
     * toString メソッドのテスト - 正常系:トークンを含めない場合
     */
    @Test
    public void testToString_normalWithoutToken() {

        /* 期待値の定義 */
        final String expectedString = "GitHubSettingsDto[repository=owner/repo]";

        /* 準備 */
        final GitHubSettingsDto testTarget = GitHubSettingsDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.toString();

        /* 検証の準備 */
        final String  actualString   = testResult;
        final boolean actualHasToken = actualString.contains("test-token");

        /* 検証の実施 */
        Assertions.assertEquals(expectedString, actualString, "文字列表現が一致しません");
        Assertions.assertFalse(actualHasToken, "トークンを含めない必要があります");

    }

}
