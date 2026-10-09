package io.github.kenichiroarai.dailytasks.carryover.presentation.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverCommand} のテスト<br>
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
public class CarryoverCommandTest {

    /**
     * テスト用の収集・集計サービス<br>
     * <p>
     * 呼び出し時のモードを記録し、固定の件数を返す。
     * </p>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubCarryoverService implements CarryoverService {

        /**
         * 呼び出し時のモード
         */
        private final List<Boolean> calls = new ArrayList<>();

        /**
         * 呼び出し時のモードを記録し、3 を返す<br>
         *
         * @param full
         *             全件モードか
         *
         * @return 3
         */
        @Override
        public int collect(final boolean full) {

            this.calls.add(Boolean.valueOf(full));
            final int result = 3;
            return result;

        }

        /**
         * 呼び出し時のモードを返す<br>
         *
         * @return 呼び出し時のモード
         */
        private List<Boolean> getCalls() {

            final List<Boolean> result = this.calls;
            return result;

        }

    }

    /**
     * private の USAGE フィールドの値を取得する<br>
     *
     * @return 使い方
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static String usage() throws Exception {

        final String result = ReflectionTestUtil.getStaticField(CarryoverCommand.class, "USAGE");
        return result;

    }

    /**
     * execute メソッドのテスト - 正常系:引数なしの場合は差分モードで実行する
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testExecute_normalDiff() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 3;
        final List<Boolean> expectedCalls = List.of(Boolean.FALSE);

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final CarryoverCommand testTarget = new CarryoverCommand(testService, System.out);

        /* テスト対象の実行 */
        final int testResult = testTarget.execute(new String[] {});

        /* 検証の準備 */
        final int actualCount = testResult;
        final List<Boolean> actualCalls = testService.getCalls();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
        Assertions.assertEquals(expectedCalls, actualCalls, "呼び出し時のモードが一致しません");

    }

    /**
     * execute メソッドのテスト - 正常系:--full の場合は全件モードで実行する
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testExecute_normalFull() throws IOException {

        /* 期待値の定義 */
        final List<Boolean> expectedCalls = List.of(Boolean.TRUE);

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final CarryoverCommand testTarget = new CarryoverCommand(testService, System.out);

        /* テスト対象の実行 */
        testTarget.execute(new String[] {
            "--full"
        });

        /* 検証の準備 */
        final List<Boolean> actualCalls = testService.getCalls();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCalls, actualCalls, "呼び出し時のモードが一致しません");

    }

    /**
     * execute メソッドのテスト - 正常系:--help の場合は使い方を表示して収集しない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testExecute_normalHelp() throws Exception {

        /* 期待値の定義 */
        final String expectedOutput = CarryoverCommandTest.usage() + System.lineSeparator();
        final int expectedCount = 0;

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        final CarryoverCommand testTarget = new CarryoverCommand(testService,
            new PrintStream(testOutput, true, StandardCharsets.UTF_8));

        /* テスト対象の実行 */
        final int testResult = testTarget.execute(new String[] {
            "--help"
        });

        /* 検証の準備 */
        final String actualOutput = testOutput.toString(StandardCharsets.UTF_8);
        final int actualCount = testResult;
        final boolean actualNotCalled = testService.getCalls().isEmpty();

        /* 検証の実施 */
        Assertions.assertEquals(expectedOutput, actualOutput, "使い方が一致しません");
        Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
        Assertions.assertTrue(actualNotCalled, "収集は実行されない必要があります");

    }

    /**
     * execute メソッドのテスト - 正常系:-h の場合は使い方を表示して収集しない
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testExecute_normalShortHelp() throws IOException {

        /* 期待値の定義 */

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        final CarryoverCommand testTarget = new CarryoverCommand(testService,
            new PrintStream(testOutput, true, StandardCharsets.UTF_8));

        /* テスト対象の実行 */
        testTarget.execute(new String[] {
            "-h"
        });

        /* 検証の準備 */
        final boolean actualNotCalled = testService.getCalls().isEmpty();

        /* 検証の実施 */
        Assertions.assertTrue(actualNotCalled, "収集は実行されない必要があります");

    }

    /**
     * execute メソッドのテスト - 準正常系:不明な引数の場合
     */
    @Test
    public void testExecute_semiUnknownArgument() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な引数です: --unknown";

        /* 準備 */
        final CarryoverCommand testTarget = new CarryoverCommand(new StubCarryoverService(), System.out);

        /* テスト対象の実行 */
        final IllegalArgumentException testException = Assertions.assertThrows(IllegalArgumentException.class,
            () -> testTarget.execute(new String[] {
                "--unknown"
            }));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

}
