package io.github.kenichiroarai.dailytasks.carryover.domain.parser.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.testutil.LogAssertions;
import io.github.kenichiroarai.dailytasks.testutil.LogCapture;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverParserImpl} のテスト<br>
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
public class CarryoverParserImplTest {

    /**
     * テスト用の標準時間
     */
    private static final DefaultMinutes DEFAULT_MINUTES = new DefaultMinutes(
        Map.of("国語", Double.valueOf(15), "マラソン日記", Double.valueOf(0)));

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverParserImpl createTarget() {

        final CarryoverParserImpl result = new CarryoverParserImpl(MessageProviderTestUtil.create());
        return result;

    }

    /**
     * テスト用の Issue を作成する<br>
     *
     * @param title
     *              タイトル
     * @param body
     *              本文
     *
     * @return Issue
     */
    private static DailyTaskIssue createIssue(final String title, final String body) {

        final DailyTaskIssue result = new DailyTaskIssue(371, title, "open", "2026-10-06T14:23:08Z", body);
        return result;

    }

    /**
     * private の parseItem メソッドを呼び出す<br>
     *
     * @param target
     *                テスト対象
     * @param number
     *                Issue 番号
     * @param section
     *                セクション名
     * @param checked
     *                チェック済みか
     * @param content
     *                チェックボックスの後ろの内容
     * @param raw
     *                元の行
     *
     * @return 持ち越し項目
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static CarryoverItem parseItem(final CarryoverParserImpl target, final int number, final String section,
        final boolean checked, final String content, final String raw) throws Exception {

        final CarryoverItem result = ReflectionTestUtil.invoke(target, "parseItem", new Class<?>[] {
            int.class, String.class, boolean.class, String.class, String.class, DefaultMinutes.class
        }, Integer.valueOf(number), section, Boolean.valueOf(checked), content, raw,
            CarryoverParserImplTest.DEFAULT_MINUTES);
        return result;

    }

    /**
     * private の parseTitleDate メソッドを呼び出す<br>
     *
     * @param title
     *              Issue タイトル
     *
     * @return 日付
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static String parseTitleDate(final String title) throws Exception {

        final String result = ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "parseTitleDate", new Class<?>[] {
            String.class
        }, title);
        return result;

    }

    /**
     * private の parseMinutes メソッドを呼び出す<br>
     *
     * @param text
     *             時間表記を含む文字列
     *
     * @return 残り時間（分）
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static Double parseMinutes(final String text) throws Exception {

        final Double result = ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "parseMinutes", new Class<?>[] {
            String.class
        }, text);
        return result;

    }

    /**
     * private の normalizeName メソッドを呼び出す<br>
     *
     * @param name
     *             項目名
     *
     * @return 正規化した項目名
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static String normalizeName(final String name) throws Exception {

        final String result = ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "normalizeName", new Class<?>[] {
            String.class
        }, name);
        return result;

    }

    /**
     * private の splitLines メソッドを呼び出す<br>
     *
     * @param body
     *             本文
     *
     * @return 行
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static List<String> splitLines(final String body) throws Exception {

        final List<String> result = ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "splitLines",
            new Class<?>[] {
                String.class
            }, body);
        return result;

    }

    /**
     * private の addSection メソッドを呼び出す<br>
     *
     * @param sections
     *                 対象セクション名
     * @param section
     *                 追加するセクション名
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static void addSection(final List<String> sections, final String section) throws Exception {

        ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "addSection", new Class<?>[] {
            List.class, String.class
        }, sections, section);

    }

    /**
     * private の warnUnexpectedLine メソッドを呼び出す<br>
     *
     * @param number
     *               Issue 番号
     * @param line
     *               行
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static void warnUnexpectedLine(final int number, final String line) throws Exception {

        ReflectionTestUtil.invoke(CarryoverParserImplTest.createTarget(), "warnUnexpectedLine", new Class<?>[] {
            int.class, String.class
        }, Integer.valueOf(number), line);

    }

    /**
     * private の verifyDeclaredCount メソッドを呼び出す<br>
     *
     * @param number
     *                      Issue 番号
     * @param declaredCount
     *                      本文の残数
     * @param parsedCount
     *                      解析件数
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static void verifyDeclaredCount(final int number, final Integer declaredCount, final int parsedCount)
        throws Exception {

        ReflectionTestUtil.invoke(CarryoverParserImplTest.createTarget(), "verifyDeclaredCount", new Class<?>[] {
            int.class, Integer.class, int.class
        }, Integer.valueOf(number), declaredCount, Integer.valueOf(parsedCount));

    }

    /**
     * private の formatDate メソッドを呼び出す<br>
     *
     * @param year
     *              年
     * @param month
     *              月
     * @param day
     *              日
     *
     * @return 日付
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static String formatDate(final String year, final String month, final String day) throws Exception {

        final String result = ReflectionTestUtil.invokeStatic(CarryoverParserImpl.class, "formatDate", new Class<?>[] {
            String.class, String.class, String.class
        }, year, month, day);
        return result;

    }

    /**
     * parse メソッドのテスト - 正常系:持ち越しセクションの項目を解析する場合
     */
    @Test
    public void testParse_normalCarryover() {

        /* 期待値の定義 */
        final String expectedDate = "2026-10-06";
        final List<String> expectedSections = List.of("持ち越し");
        final Integer expectedDeclaredCount = Integer.valueOf(2);
        final int expectedCount = 2;
        final double expectedMinutes = 23.5;
        final String[] expectedMsgs = {};

        /* 準備 */
        final String testBody = String.join("\r\n", "## ルーティン", "", "- [ ] 国語（15分）", "## 追加", "## 持ち越し", "残：2",
            "- [ ] 国語2026/06/18（残り時間：15分）", "- [x] 音楽2026/08/18（残り時間：8.5分）", "## 先行",
            "- [ ] 国語2026/10/07（残り時間：15分）");
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("2026年10月06日のタスク", testBody);

        /* テスト対象の実行 */
        final CarryoverIssue testResult;

        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            testResult = testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final String actualDate = testResult.getDate();
            final List<String> actualSections = testResult.getSections();
            final Integer actualDeclaredCount = testResult.getDeclaredCount();
            final int actualCount = testResult.getCount();
            final double actualMinutes = testResult.getMinutes();
            final boolean actualSecondChecked = testResult.getItems().get(1).isChecked();

            /* 検証の実施 */
            Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");
            Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");
            Assertions.assertEquals(expectedDeclaredCount, actualDeclaredCount, "残数が一致しません");
            Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");
            Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間の合計が一致しません");
            Assertions.assertTrue(actualSecondChecked, "2 件目はチェック済みになる必要があります");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parse メソッドのテスト - 正常系:対象セクションがない場合
     */
    @Test
    public void testParse_normalNoSection() {

        /* 期待値の定義 */
        final int expectedCount = 0;

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("2025年10月01日のタスク",
            "## ルーティン\n- [ ] 国語（15分）\n## 追加");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

        /* 検証の準備 */
        final int actualCount = testResult.getCount();
        final boolean actualHasSection = testResult.hasSection();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");
        Assertions.assertFalse(actualHasSection, "対象セクションがないと判定される必要があります");

    }

    /**
     * parse メソッドのテスト - 正常系:旧名称（負債・繰り越し）のセクションを解析する場合
     */
    @Test
    public void testParse_normalOldSectionNames() {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("負債", "繰り越し");

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("2026年04月10日のタスク",
            "## 負債\n- [ ] 国語2026/03/30\n## 繰り越し\n- [ ] 国語2026/03/31（残り9分）");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

        /* 検証の準備 */
        final List<String> actualSections = testResult.getSections();

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * parse メソッドのテスト - 準正常系:タイトルに日付がない場合
     */
    @Test
    public void testParse_semiTitleWithoutDate() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 タイトルから日付を取得できません: タスク",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("タスク", null);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverIssue testResult = testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final String actualDate = testResult.getDate();

            /* 検証の実施 */
            Assertions.assertNull(actualDate, "日付は null になる必要があります");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parse メソッドのテスト - 準正常系:対象セクションに想定外の行がある場合
     */
    @Test
    public void testParse_semiUnexpectedLine() {

        /* 期待値の定義 */
        final int expectedCount = 1;
        final String[] expectedMsgs = {
            "#371 想定外の行です: メモ",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("2026年10月06日のタスク",
            "## 持ち越し\nメモ\n\n-----\n- [ ] 国語2026/06/18（残り時間：15分）");

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverIssue testResult = testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult.getCount();

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parse メソッドのテスト - 準正常系:残数と解析件数が一致しない場合
     */
    @Test
    public void testParse_semiDeclaredCountMismatch() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 残数と解析件数が一致しません: 残=3, 解析=1",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();
        final DailyTaskIssue testIssue = CarryoverParserImplTest.createIssue("2026年10月06日のタスク",
            "## 持ち越し\n残：3\n- [ ] 国語2026/06/18（残り時間：15分）");

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            testTarget.parse(testIssue, CarryoverParserImplTest.DEFAULT_MINUTES);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 正常系:時間表記から残り時間を取得する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_normalParsedMinutes() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "国語";
        final String expectedOriginDate = "2026-06-18";
        final double expectedMinutes = 4;
        final MinutesSource expectedMinutesSource = MinutesSource.PARSED;
        final String expectedSection = "持ち越し";

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "持ち越し", false, "国語2026/06/18（残り時間：4分）",
            "- [ ] 国語2026/06/18（残り時間：4分）");

        /* 検証の準備 */
        final String actualName = testResult.getName();
        final String actualOriginDate = testResult.getOriginDate();
        final double actualMinutes = testResult.getMinutes();
        final MinutesSource actualMinutesSource = testResult.getMinutesSource();
        final String actualSection = testResult.getSection();

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");
        Assertions.assertEquals(expectedOriginDate, actualOriginDate, "持ち越し元の日付が一致しません");
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
        Assertions.assertEquals(expectedSection, actualSection, "セクション名が一致しません");

    }

    /**
     * parseItem メソッドのテスト - 正常系:時間表記がなく標準時間で補完する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_normalDefaultMinutes() throws Exception {

        /* 期待値の定義 */
        final double expectedMinutes = 15;
        final MinutesSource expectedMinutesSource = MinutesSource.DEFAULT;
        final String[] expectedMsgs = {};

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "負債", false, "国語2026/03/30", "- [ ] 国語2026/03/30");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final double actualMinutes = testResult.getMinutes();
            final MinutesSource actualMinutesSource = testResult.getMinutesSource();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
            Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 準正常系:標準時間が未登録の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_semiUnknownMinutes() throws Exception {

        /* 期待値の定義 */
        final double expectedMinutes = 0;
        final MinutesSource expectedMinutesSource = MinutesSource.UNKNOWN;
        final String[] expectedMsgs = {
            "#371 標準時間が未登録のため 0 分とします: 英語",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "負債", false, "英語2026/03/30", "- [ ] 英語2026/03/30");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final double actualMinutes = testResult.getMinutes();
            final MinutesSource actualMinutesSource = testResult.getMinutesSource();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
            Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 準正常系:持ち越し元の日付がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_semiWithoutOriginDate() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "国語";
        final double expectedMinutes = 10;
        final String[] expectedMsgs = {
            "#371 持ち越し元の日付がありません: - [ ] 国語（10分）",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "持ち越し", false, "国語（10分）", "- [ ] 国語（10分）");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final String actualName = testResult.getName();
            final String actualOriginDate = testResult.getOriginDate();
            final double actualMinutes = testResult.getMinutes();

            /* 検証の実施 */
            Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");
            Assertions.assertNull(actualOriginDate, "持ち越し元の日付は null になる必要があります");
            Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 準正常系:時間表記を解釈できない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_semiUnparsableMinutes() throws Exception {

        /* 期待値の定義 */
        final double expectedMinutes = 15;
        final MinutesSource expectedMinutesSource = MinutesSource.DEFAULT;
        final String[] expectedMsgs = {
            "#371 時間表記を解釈できないため標準時間で補完します: - [ ] 国語2026/06/18（未定）",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "持ち越し", false, "国語2026/06/18（未定）",
                "- [ ] 国語2026/06/18（未定）");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final double actualMinutes = testResult.getMinutes();
            final MinutesSource actualMinutesSource = testResult.getMinutesSource();

            /* 検証の実施 */
            Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
            Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 準正常系:項目名がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseItem_semiEmptyName() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "不明";
        final String[] expectedMsgs = {
            "#371 項目名を取得できません: - [ ] 2026/06/18（残り時間：15分）",
        };

        /* 準備 */
        final CarryoverParserImpl testTarget = CarryoverParserImplTest.createTarget();

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            final CarryoverItem testResult = CarryoverParserImplTest.parseItem(testTarget, 371, "持ち越し", false, "2026/06/18（残り時間：15分）",
                "- [ ] 2026/06/18（残り時間：15分）");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final String actualName = testResult.getName();

            /* 検証の実施 */
            Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseTitleDate メソッドのテスト - 正常系:タイトルに日付がある場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseTitleDate_normalDate() throws Exception {

        /* 期待値の定義 */
        final String expectedDate = "2026-10-06";

        /* 準備 */
        final String testTitle = "2026年10月6日のタスク";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

    /**
     * parseTitleDate メソッドのテスト - 準正常系:タイトルに日付がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseTitleDate_semiNoDate() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final String testTitle = "タスク";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualDate, "日付は null になる必要があります");

    }

    /**
     * parseTitleDate メソッドのテスト - 準正常系:タイトルが null の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseTitleDate_semiNull() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final String testTitle = null;

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualDate, "日付は null になる必要があります");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残り時間：N分」の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalRemainingTime() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(15);

        /* 準備 */
        final String testText = "（残り時間：15分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残り時間：N分」で小数の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalDecimal() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(8.5);

        /* 準備 */
        final String testText = "（残り時間：8.5分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残りN分」の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalRemaining() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(9);

        /* 準備 */
        final String testText = "（残り9分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「先行分残りN分」の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalAdvanceRemaining() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(5);

        /* 準備 */
        final String testText = "（先行分残り5分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「N分」の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalSimple() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(30);

        /* 準備 */
        final String testText = "（30分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:半角括弧の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_normalHalfWidthParentheses() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(15);

        /* 準備 */
        final String testText = "(15分)";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 準正常系:括弧がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_semiNoParentheses() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final String testText = "";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualMinutes, "残り時間は null になる必要があります");

    }

    /**
     * parseMinutes メソッドのテスト - 準正常系:括弧内を解釈できない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testParseMinutes_semiUnparsable() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final String testText = "（未定）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParserImplTest.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualMinutes, "残り時間は null になる必要があります");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:そのままの項目名の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNormalizeName_normalPlain() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "IT・技術学習";

        /* 準備 */
        final String testName = " IT・技術学習 ";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:末尾に括弧書きがある場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNormalizeName_normalTrailingParentheses() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "汎用タスク";

        /* 準備 */
        final String testName = "汎用タスク（15分）";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:末尾に開き括弧がある場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNormalizeName_normalTrailingOpenParentheses() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "フロントエンドまたはサーバー系に関係する学習";

        /* 準備 */
        final String testName = "フロントエンドまたはサーバー系に関係する学習(";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 準正常系:空になる場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNormalizeName_semiEmpty() throws Exception {

        /* 期待値の定義 */
        final String expectedName = "不明";

        /* 準備 */
        final String testName = "（15分）";

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * splitLines メソッドのテスト - 正常系:改行コードが混在する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testSplitLines_normalMixedLineBreaks() throws Exception {

        /* 期待値の定義 */
        final List<String> expectedLines = List.of("a", "b", "c");

        /* 準備 */
        final String testBody = "a\r\nb\nc";

        /* テスト対象の実行 */
        final List<String> testResult = CarryoverParserImplTest.splitLines(testBody);

        /* 検証の準備 */
        final List<String> actualLines = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLines, actualLines, "行が一致しません");

    }

    /**
     * splitLines メソッドのテスト - 準正常系:本文が null の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testSplitLines_semiNull() throws Exception {

        /* 期待値の定義 */
        final List<String> expectedLines = List.of();

        /* 準備 */
        final String testBody = null;

        /* テスト対象の実行 */
        final List<String> testResult = CarryoverParserImplTest.splitLines(testBody);

        /* 検証の準備 */
        final List<String> actualLines = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLines, actualLines, "行が一致しません");

    }

    /**
     * addSection メソッドのテスト - 正常系:対象セクションを追加する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testAddSection_normalTarget() throws Exception {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("持ち越し");

        /* 準備 */
        final List<String> testSections = new ArrayList<>();

        /* テスト対象の実行 */
        CarryoverParserImplTest.addSection(testSections, "持ち越し");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * addSection メソッドのテスト - 準正常系:追加済みのセクションの場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testAddSection_semiDuplicate() throws Exception {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("持ち越し");

        /* 準備 */
        final List<String> testSections = new ArrayList<>(List.of("持ち越し"));

        /* テスト対象の実行 */
        CarryoverParserImplTest.addSection(testSections, "持ち越し");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * addSection メソッドのテスト - 準正常系:対象外のセクションの場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testAddSection_semiNotTarget() throws Exception {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of();

        /* 準備 */
        final List<String> testSections = new ArrayList<>();

        /* テスト対象の実行 */
        CarryoverParserImplTest.addSection(testSections, "先行");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 正常系:空行の場合は警告しない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testWarnUnexpectedLine_normalBlank() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.warnUnexpectedLine(371, "   ");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 正常系:区切り線の場合は警告しない
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testWarnUnexpectedLine_normalSeparator() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.warnUnexpectedLine(371, "-----");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 準正常系:想定外の行の場合は警告する
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testWarnUnexpectedLine_semiUnexpected() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 想定外の行です: メモ",
        };

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.warnUnexpectedLine(371, "メモ");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 正常系:残数と解析件数が一致する場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testVerifyDeclaredCount_normalMatch() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.verifyDeclaredCount(371, Integer.valueOf(76), 76);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 正常系:残数の記載がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testVerifyDeclaredCount_normalNoDeclared() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.verifyDeclaredCount(371, null, 76);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 準正常系:残数と解析件数が一致しない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testVerifyDeclaredCount_semiMismatch() throws Exception {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 残数と解析件数が一致しません: 残=76, 解析=75",
        };

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParserImpl.class)) {

            CarryoverParserImplTest.verifyDeclaredCount(371, Integer.valueOf(76), 75);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * formatDate メソッドのテスト - 正常系:月・日をゼロ埋めする場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testFormatDate_normalZeroPadding() throws Exception {

        /* 期待値の定義 */
        final String expectedDate = "2026-06-05";

        /* 準備 */

        /* テスト対象の実行 */
        final String testResult = CarryoverParserImplTest.formatDate("2026", "6", "5");

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

}
