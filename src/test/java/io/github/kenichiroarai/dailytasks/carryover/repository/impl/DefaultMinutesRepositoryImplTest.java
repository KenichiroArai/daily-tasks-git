package io.github.kenichiroarai.dailytasks.carryover.repository.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.kenichiroarai.dailytasks.testutil.LogAssertions;
import io.github.kenichiroarai.dailytasks.testutil.LogCapture;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * {@link DefaultMinutesRepositoryImpl} のテスト<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class DefaultMinutesRepositoryImplTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static DefaultMinutesRepositoryImpl createTarget() {

        final DefaultMinutesRepositoryImpl result
            = new DefaultMinutesRepositoryImpl(new JsonMapper(), MessageProviderTestUtil.create());
        return result;

    }

    /**
     * テスト用の一時ディレクトリ
     */
    @TempDir
    Path tempDir;

    /**
     * load メソッドのテスト - 異常系:設定ファイルが不正な場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoad_errorInvalidJson() throws IOException {

        /* 期待値の定義 */

        /* 準備 */
        final Path testConfig = this.tempDir.resolve("default-minutes.json");
        Files.writeString(testConfig, "[不正");
        final DefaultMinutesRepositoryImpl testTarget = DefaultMinutesRepositoryImplTest.createTarget();

        /* テスト対象の実行 */
        final IOException testException = Assertions.assertThrows(IOException.class, () -> testTarget.load(testConfig));

        /* 検証の準備 */
        final Throwable actualCause = testException.getCause();

        /* 検証の実施 */
        Assertions.assertInstanceOf(JacksonException.class, actualCause, "原因の例外の型が一致しません");

    }

    /**
     * load メソッドのテスト - 正常系:設定ファイルを読み込む場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoad_normalConfig() throws IOException {

        /* 期待値の定義 */
        final Double expectedMinutes = (double) 30;

        /* 準備 */
        final Path testConfig = this.tempDir.resolve("default-minutes.json");
        Files.writeString(testConfig, "{\"高校数学\": 30, \"国語\": 15}");
        final DefaultMinutesRepositoryImpl testTarget = DefaultMinutesRepositoryImplTest.createTarget();

        /* テスト対象の実行 */
        final Map<String, Double> testResult = testTarget.load(testConfig);

        /* 検証の準備 */
        final Double actualMinutes = testResult.get("高校数学");

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "標準時間が一致しません");

    }

    /**
     * load メソッドのテスト - 準正常系:設定ファイルがない場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoad_semiNoFile() throws IOException {

        /* 期待値の定義 */
        final Path     testConfig   = this.tempDir.resolve("none.json");
        final String[] expectedMsgs = {
            "標準時間の設定ファイルがありません: " + testConfig,
        };

        /* 準備 */
        final DefaultMinutesRepositoryImpl testTarget = DefaultMinutesRepositoryImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(DefaultMinutesRepositoryImpl.class)) {

            final Map<String, Double> testResult = testTarget.load(testConfig);

            /* 検証の準備 */
            final String[] actualMsgs    = testLog.getMessages();
            final Double   actualMinutes = testResult.get("国語");

            /* 検証の実施 */
            Assertions.assertNull(actualMinutes, "標準時間は未登録になる必要があります");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

}
