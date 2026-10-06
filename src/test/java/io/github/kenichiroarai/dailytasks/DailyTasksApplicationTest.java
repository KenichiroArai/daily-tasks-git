package io.github.kenichiroarai.dailytasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link DailyTasksApplication} のテスト<br>
 * <p>
 * GitHub API に通信しないよう、使い方の表示（--help）だけを実行する。
 * </p>
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
public class DailyTasksApplicationTest {

    /**
     * main メソッドのテスト - 正常系:--help の場合は使い方を表示する
     */
    @Test
    public void testMain_normalHelp() throws IOException {

        /* 期待値の定義 */
        final String expectedPrefix = "使い方: java -jar daily-tasks-0.1.0.jar";

        /* 準備 */
        final PrintStream testOriginalOut = System.out;
        final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();

        try {

            System.setOut(new PrintStream(testOutput, true, StandardCharsets.UTF_8));

            /* テスト対象の実行 */
            DailyTasksApplication.main(new String[] {
                "--help"
            });

        } finally {

            System.setOut(testOriginalOut);

        }

        /* 検証の準備 */
        final boolean actualStartsWith = testOutput.toString(StandardCharsets.UTF_8).startsWith(expectedPrefix);

        /* 検証の実施 */
        Assertions.assertTrue(actualStartsWith, "使い方が表示される必要があります");

    }

}
