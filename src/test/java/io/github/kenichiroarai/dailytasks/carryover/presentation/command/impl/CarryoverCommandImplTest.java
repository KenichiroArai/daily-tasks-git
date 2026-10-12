package io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.presentation.model.CarryoverOptionsModel;
import io.github.kenichiroarai.dailytasks.carryover.presentation.model.CarryoverPropertiesModel;
import io.github.kenichiroarai.dailytasks.carryover.presentation.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.testutil.MessageLogicTestUtil;
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
     * 呼び出し時のモードと設定を記録し、固定の件数を返す。
     * </p>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubCarryoverService implements CarryoverService {

        /** 呼び出し時のモード */
        private final List<Boolean> calls = new ArrayList<>();

        /** 呼び出し時の設定 */
        private final List<CarryoverPropertiesModel> propertiesList = new ArrayList<>();

        /**
         * 呼び出し時のモードと設定を記録し、3 を返す<br>
         *
         * @param properties
         *                   持ち越しの収集の設定
         * @param full
         *                   全件モードか
         *
         * @return 3
         */
        @Override
        public int collect(final CarryoverPropertiesModel properties, final boolean full) {

            this.propertiesList.add(properties);
            this.calls.add(full);
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

        /**
         * 呼び出し時の設定を返す<br>
         *
         * @return 呼び出し時の設定
         */
        private List<CarryoverPropertiesModel> getPropertiesList() {

            final List<CarryoverPropertiesModel> result = this.propertiesList;
            return result;

        }

    }

    /** テスト用の設定ファイルの値 */
    private static final CarryoverPropertiesModel PROPERTIES
        = new CarryoverPropertiesModel("owner/repo", "test-token", "out/data", "conf/minutes.json", 7);

    /**
     * テスト対象を作成する<br>
     *
     * @param service
     *                持ち越しの収集・集計サービス
     * @param out
     *                使い方の出力先
     *
     * @return テスト対象
     */
    private static CarryoverCommandImpl createTarget(final CarryoverService service, final PrintStream out) {

        final CarryoverCommandImpl result = new CarryoverCommandImpl(service, CarryoverCommandImplTest.PROPERTIES,
            MessageLogicTestUtil.create(), out);
        return result;

    }

    /**
     * private の parseArgs メソッドを呼び出す<br>
     *
     * @param args
     *             コマンドライン引数
     *
     * @return 持ち越しの収集コマンドのオプション
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static CarryoverOptionsModel parseArgs(final String... args) throws Exception {

        final CarryoverCommandImpl  target = CarryoverCommandImplTest.createTarget(new StubCarryoverService(),
            System.out);
        final CarryoverOptionsModel result = ReflectionTestUtil.invoke(target, "parseArgs", new Class<?>[] {
            String[].class
        }, (Object) args);
        return result;

    }

    /**
     * 使い方のメッセージを取得する<br>
     *
     * @return 使い方
     */
    private static String usage() {

        final String result = MessageLogicTestUtil.create().get("carryover.command.usage");
        return result;

    }

    /**
     * CarryoverCommandImpl コンストラクタのテスト - 正常系:出力先を指定しない場合は標準出力を使う
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @SuppressWarnings("resource")
    @Test
    public void testCarryoverCommandImpl_normalSystemOut() throws Exception {

        /* 期待値の定義 */
        final PrintStream expectedOut = System.out;

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();

        /* テスト対象の実行 */
        final CarryoverCommandImpl testTarget = new CarryoverCommandImpl(testService,
            CarryoverCommandImplTest.PROPERTIES, MessageLogicTestUtil.create());

        /* 検証の準備 */
        final Object      actualService = ReflectionTestUtil.getField(testTarget, "carryoverService");
        final PrintStream actualOut     = ReflectionTestUtil.getField(testTarget, "out");

        /* 検証の実施 */
        Assertions.assertEquals(testService, actualService, "サービスが一致しません");
        Assertions.assertEquals(expectedOut, actualOut, "出力先が一致しません");

    }

    /**
     * parseArgs メソッドのテスト - 正常系:--full がある場合は全件モード
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseArgs_normalFull() throws Exception {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testResult = CarryoverCommandImplTest.parseArgs("--full");

        /* 検証の準備 */
        final boolean actualFull = testResult.isFull();
        final boolean actualHelp = testResult.isHelp();

        /* 検証の実施 */
        Assertions.assertTrue(actualFull, "全件モードと判定される必要があります");
        Assertions.assertFalse(actualHelp, "使い方を表示しないと判定される必要があります");

    }

    /**
     * parseArgs メソッドのテスト - 正常系:--help がある場合は使い方を表示する
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseArgs_normalHelp() throws Exception {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testResult = CarryoverCommandImplTest.parseArgs("--help");

        /* 検証の準備 */
        final boolean actualFull = testResult.isFull();
        final boolean actualHelp = testResult.isHelp();

        /* 検証の実施 */
        Assertions.assertFalse(actualFull, "差分モードと判定される必要があります");
        Assertions.assertTrue(actualHelp, "使い方を表示すると判定される必要があります");

    }

    /**
     * parseArgs メソッドのテスト - 正常系:引数なしの場合は差分モードで使い方を表示しない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseArgs_normalNoArgs() throws Exception {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testResult = CarryoverCommandImplTest.parseArgs();

        /* 検証の準備 */
        final boolean actualFull = testResult.isFull();
        final boolean actualHelp = testResult.isHelp();

        /* 検証の実施 */
        Assertions.assertFalse(actualFull, "差分モードと判定される必要があります");
        Assertions.assertFalse(actualHelp, "使い方を表示しないと判定される必要があります");

    }

    /**
     * parseArgs メソッドのテスト - 正常系:-h がある場合は使い方を表示する
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseArgs_normalShortHelp() throws Exception {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final CarryoverOptionsModel testResult = CarryoverCommandImplTest.parseArgs("-h");

        /* 検証の準備 */
        final boolean actualHelp = testResult.isHelp();

        /* 検証の実施 */
        Assertions.assertTrue(actualHelp, "使い方を表示すると判定される必要があります");

    }

    /**
     * parseArgs メソッドのテスト - 準正常系:不明な引数がある場合
     */
    @Test
    public void testParseArgs_semiUnknownArgument() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な引数です: --unknown";

        /* 準備 */

        /* テスト対象の実行 */
        final IllegalArgumentException testException = Assertions.assertThrows(IllegalArgumentException.class,
            () -> CarryoverCommandImplTest.parseArgs("--full", "--unknown"));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

    /**
     * run メソッドのテスト - 正常系:引数なしの場合は差分モードで実行する
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testRun_normalDiff() throws IOException {

        /* 期待値の定義 */
        final List<Boolean>            expectedCalls      = List.of(Boolean.FALSE);
        final CarryoverPropertiesModel expectedProperties = CarryoverCommandImplTest.PROPERTIES;

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final CarryoverCommandImpl testTarget  = CarryoverCommandImplTest.createTarget(testService, System.out);

        /* テスト対象の実行 */
        testTarget.run();

        /* 検証の準備 */
        final List<Boolean>            actualCalls      = testService.getCalls();
        final CarryoverPropertiesModel actualProperties = testService.getPropertiesList().get(0);

        /* 検証の実施 */
        Assertions.assertEquals(expectedCalls, actualCalls, "呼び出し時のモードが一致しません");
        Assertions.assertEquals(expectedProperties, actualProperties, "引き継いだ設定が一致しません");

    }

    /**
     * run メソッドのテスト - 正常系:--full の場合は全件モードで実行する
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testRun_normalFull() throws IOException {

        /* 期待値の定義 */
        final List<Boolean> expectedCalls = List.of(Boolean.TRUE);

        /* 準備 */
        final StubCarryoverService testService = new StubCarryoverService();
        final CarryoverCommandImpl testTarget  = CarryoverCommandImplTest.createTarget(testService, System.out);

        /* テスト対象の実行 */
        testTarget.run("--full");

        /* 検証の準備 */
        final List<Boolean> actualCalls = testService.getCalls();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCalls, actualCalls, "呼び出し時のモードが一致しません");

    }

    /**
     * run メソッドのテスト - 正常系:--help の場合は使い方を表示して収集しない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testRun_normalHelp() throws Exception {

        /* 期待値の定義 */
        final String expectedOutput = CarryoverCommandImplTest.usage() + System.lineSeparator();

        /* 準備 */
        final StubCarryoverService  testService = new StubCarryoverService();
        final ByteArrayOutputStream testOutput  = new ByteArrayOutputStream();
        final CarryoverCommandImpl  testTarget  = CarryoverCommandImplTest.createTarget(testService,
            new PrintStream(testOutput, true, StandardCharsets.UTF_8));

        /* テスト対象の実行 */
        testTarget.run("--help");

        /* 検証の準備 */
        final String  actualOutput    = testOutput.toString(StandardCharsets.UTF_8);
        final boolean actualNotCalled = testService.getCalls().isEmpty();

        /* 検証の実施 */
        Assertions.assertEquals(expectedOutput, actualOutput, "使い方が一致しません");
        Assertions.assertTrue(actualNotCalled, "収集は実行されない必要があります");

    }

    /**
     * run メソッドのテスト - 正常系:-h の場合は使い方を表示して収集しない
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testRun_normalShortHelp() throws IOException {

        /* 期待値の定義 */

        /* 準備 */
        final StubCarryoverService  testService = new StubCarryoverService();
        final ByteArrayOutputStream testOutput  = new ByteArrayOutputStream();
        final CarryoverCommandImpl  testTarget  = CarryoverCommandImplTest.createTarget(testService,
            new PrintStream(testOutput, true, StandardCharsets.UTF_8));

        /* テスト対象の実行 */
        testTarget.run("-h");

        /* 検証の準備 */
        final boolean actualNotCalled = testService.getCalls().isEmpty();

        /* 検証の実施 */
        Assertions.assertTrue(actualNotCalled, "収集は実行されない必要があります");

    }

    /**
     * run メソッドのテスト - 準正常系:不明な引数の場合
     */
    @Test
    public void testRun_semiUnknownArgument() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な引数です: --unknown";

        /* 準備 */
        final CarryoverCommandImpl testTarget
            = CarryoverCommandImplTest.createTarget(new StubCarryoverService(), System.out);

        /* テスト対象の実行 */
        final IllegalArgumentException testException
            = Assertions.assertThrows(IllegalArgumentException.class, () -> testTarget.run("--unknown"));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

}
