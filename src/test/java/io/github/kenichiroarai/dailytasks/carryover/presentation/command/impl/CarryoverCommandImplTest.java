package io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.application.service.impl.CarryoverServiceImpl;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverCommandImpl} のテスト<br>
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
public class CarryoverCommandImplTest {

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

        final String result = ReflectionTestUtil.getStaticField(CarryoverCommandImpl.class, "USAGE");
        return result;

    }

    /**
     * private の createSettings メソッドを呼び出す<br>
     *
     * @param token
     *              GitHub API のトークン
     *
     * @return 持ち越しの収集・集計の設定
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static CarryoverSettings createSettings(final String token) throws Exception {

        final CarryoverSettings result = ReflectionTestUtil.invokeStatic(CarryoverCommandImpl.class, "createSettings",
            new Class<?>[] {
                String.class
            }, token);
        return result;

    }

    /**
     * CarryoverCommandImpl コンストラクタのテスト - 正常系:出力先だけを指定した場合は application 層の実装を生成する
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testCarryoverCommandImpl_normalOutOnly() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final PrintStream testOut = System.out;

        /* テスト対象の実行 */
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testOut);

        /* 検証の準備 */
        final Object actualService = ReflectionTestUtil.getField(testTarget, "carryoverService");
        final PrintStream actualOut = ReflectionTestUtil.getField(testTarget, "out");

        /* 検証の実施 */
        Assertions.assertInstanceOf(CarryoverServiceImpl.class, actualService, "サービスの型が一致しません");
        Assertions.assertEquals(testOut, actualOut, "出力先が一致しません");

    }

    /**
     * createSettings メソッドのテスト - 正常系:トークンと既定値から設定を作る場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testCreateSettings_normalDefaults() throws Exception {

        /* 期待値の定義 */
        final String expectedRepository = "KenichiroArai/daily-tasks-git";
        final String expectedToken = "test-token";
        final Path expectedDataDir = Path.of("docs", "data");
        final Path expectedDefaultMinutesFile = Path.of("config", "default-minutes.json");

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverSettings testResult = CarryoverCommandImplTest.createSettings(expectedToken);

        /* 検証の準備 */
        final String actualRepository = testResult.getRepository();
        final String actualToken = testResult.getToken();
        final Path actualDataDir = testResult.getDataDir();
        final Path actualDefaultMinutesFile = testResult.getDefaultMinutesFile();

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "リポジトリが一致しません");
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "出力先が一致しません");
        Assertions.assertEquals(expectedDefaultMinutesFile, actualDefaultMinutesFile, "標準時間の設定ファイルが一致しません");

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
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testService, System.out);

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
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testService, System.out);

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
        final String expectedOutput = CarryoverCommandImplTest.usage() + System.lineSeparator();
        final int expectedCount = 0;

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testService,
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
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testService,
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
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(new StubCarryoverService(), System.out);

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
