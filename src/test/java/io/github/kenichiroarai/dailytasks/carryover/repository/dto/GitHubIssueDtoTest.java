package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link GitHubIssueDto} のテスト<br>
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
public class GitHubIssueDtoTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static GitHubIssueDto createTarget() {

        final GitHubIssueDto result = new GitHubIssueDto(371, "2026年10月06日のタスク", "open", "2026-10-06T14:23:08Z", "本文");
        return result;

    }

    /**
     * getBody メソッドのテスト - 正常系:本文を返す場合
     */
    @Test
    public void testGetBody_normalValue() {

        /* 期待値の定義 */
        final String expectedBody = "本文";

        /* 準備 */
        final GitHubIssueDto testTarget = GitHubIssueDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getBody();

        /* 検証の準備 */
        final String actualBody = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedBody, actualBody, "本文が一致しません");

    }

    /**
     * getNumber メソッドのテスト - 正常系:Issue 番号を返す場合
     */
    @Test
    public void testGetNumber_normalValue() {

        /* 期待値の定義 */
        final int expectedNumber = 371;

        /* 準備 */
        final GitHubIssueDto testTarget = GitHubIssueDtoTest.createTarget();

        /* テスト対象の実行 */
        final int testResult = testTarget.getNumber();

        /* 検証の準備 */
        final int actualNumber = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedNumber, actualNumber, "Issue 番号が一致しません");

    }

    /**
     * getState メソッドのテスト - 正常系:状態を返す場合
     */
    @Test
    public void testGetState_normalValue() {

        /* 期待値の定義 */
        final String expectedState = "open";

        /* 準備 */
        final GitHubIssueDto testTarget = GitHubIssueDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getState();

        /* 検証の準備 */
        final String actualState = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedState, actualState, "状態が一致しません");

    }

    /**
     * getTitle メソッドのテスト - 正常系:タイトルを返す場合
     */
    @Test
    public void testGetTitle_normalValue() {

        /* 期待値の定義 */
        final String expectedTitle = "2026年10月06日のタスク";

        /* 準備 */
        final GitHubIssueDto testTarget = GitHubIssueDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getTitle();

        /* 検証の準備 */
        final String actualTitle = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedTitle, actualTitle, "タイトルが一致しません");

    }

    /**
     * getUpdatedAt メソッドのテスト - 正常系:更新日時を返す場合
     */
    @Test
    public void testGetUpdatedAt_normalValue() {

        /* 期待値の定義 */
        final String expectedUpdatedAt = "2026-10-06T14:23:08Z";

        /* 準備 */
        final GitHubIssueDto testTarget = GitHubIssueDtoTest.createTarget();

        /* テスト対象の実行 */
        final String testResult = testTarget.getUpdatedAt();

        /* 検証の準備 */
        final String actualUpdatedAt = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedUpdatedAt, actualUpdatedAt, "更新日時が一致しません");

    }

}
