package io.github.kenichiroarai.dailytasks.carryover.domain.parser;

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

/**
 * {@link CarryoverParser} のテスト<br>
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
public class CarryoverParserTest {

    /**
     * テスト用の標準時間
     */
    private static final DefaultMinutes DEFAULT_MINUTES = new DefaultMinutes(
        Map.of("国語", Double.valueOf(15), "マラソン日記", Double.valueOf(0)));

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("2026年10月06日のタスク", testBody);

        /* テスト対象の実行 */
        final CarryoverIssue testResult;

        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            testResult = testTarget.parse(testIssue);

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("2025年10月01日のタスク",
            "## ルーティン\n- [ ] 国語（15分）\n## 追加");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = testTarget.parse(testIssue);

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("2026年04月10日のタスク",
            "## 負債\n- [ ] 国語2026/03/30\n## 繰り越し\n- [ ] 国語2026/03/31（残り9分）");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = testTarget.parse(testIssue);

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("タスク", null);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverIssue testResult = testTarget.parse(testIssue);

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("2026年10月06日のタスク",
            "## 持ち越し\nメモ\n\n-----\n- [ ] 国語2026/06/18（残り時間：15分）");

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverIssue testResult = testTarget.parse(testIssue);

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
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);
        final DailyTaskIssue testIssue = CarryoverParserTest.createIssue("2026年10月06日のタスク",
            "## 持ち越し\n残：3\n- [ ] 国語2026/06/18（残り時間：15分）");

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            testTarget.parse(testIssue);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * parseItem メソッドのテスト - 正常系:時間表記から残り時間を取得する場合
     */
    @Test
    public void testParseItem_normalParsedMinutes() {

        /* 期待値の定義 */
        final String expectedName = "国語";
        final String expectedOriginDate = "2026-06-18";
        final double expectedMinutes = 4;
        final MinutesSource expectedMinutesSource = MinutesSource.PARSED;
        final String expectedSection = "持ち越し";

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        final CarryoverItem testResult = testTarget.parseItem(371, "持ち越し", false, "国語2026/06/18（残り時間：4分）",
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
     */
    @Test
    public void testParseItem_normalDefaultMinutes() {

        /* 期待値の定義 */
        final double expectedMinutes = 15;
        final MinutesSource expectedMinutesSource = MinutesSource.DEFAULT;
        final String[] expectedMsgs = {};

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverItem testResult = testTarget.parseItem(371, "負債", false, "国語2026/03/30", "- [ ] 国語2026/03/30");

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
     */
    @Test
    public void testParseItem_semiUnknownMinutes() {

        /* 期待値の定義 */
        final double expectedMinutes = 0;
        final MinutesSource expectedMinutesSource = MinutesSource.UNKNOWN;
        final String[] expectedMsgs = {
            "#371 標準時間が未登録のため 0 分とします: 英語",
        };

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverItem testResult = testTarget.parseItem(371, "負債", false, "英語2026/03/30", "- [ ] 英語2026/03/30");

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
     */
    @Test
    public void testParseItem_semiWithoutOriginDate() {

        /* 期待値の定義 */
        final String expectedName = "国語";
        final double expectedMinutes = 10;
        final String[] expectedMsgs = {
            "#371 持ち越し元の日付がありません: - [ ] 国語（10分）",
        };

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverItem testResult = testTarget.parseItem(371, "持ち越し", false, "国語（10分）", "- [ ] 国語（10分）");

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
     */
    @Test
    public void testParseItem_semiUnparsableMinutes() {

        /* 期待値の定義 */
        final double expectedMinutes = 15;
        final MinutesSource expectedMinutesSource = MinutesSource.DEFAULT;
        final String[] expectedMsgs = {
            "#371 時間表記を解釈できないため標準時間で補完します: - [ ] 国語2026/06/18（未定）",
        };

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverItem testResult = testTarget.parseItem(371, "持ち越し", false, "国語2026/06/18（未定）",
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
     */
    @Test
    public void testParseItem_semiEmptyName() {

        /* 期待値の定義 */
        final String expectedName = "不明";
        final String[] expectedMsgs = {
            "#371 項目名を取得できません: - [ ] 2026/06/18（残り時間：15分）",
        };

        /* 準備 */
        final CarryoverParser testTarget = new CarryoverParser(CarryoverParserTest.DEFAULT_MINUTES);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            final CarryoverItem testResult = testTarget.parseItem(371, "持ち越し", false, "2026/06/18（残り時間：15分）",
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
     */
    @Test
    public void testParseTitleDate_normalDate() {

        /* 期待値の定義 */
        final String expectedDate = "2026-10-06";

        /* 準備 */
        final String testTitle = "2026年10月6日のタスク";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

    /**
     * parseTitleDate メソッドのテスト - 準正常系:タイトルに日付がない場合
     */
    @Test
    public void testParseTitleDate_semiNoDate() {

        /* 期待値の定義 */

        /* 準備 */
        final String testTitle = "タスク";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualDate, "日付は null になる必要があります");

    }

    /**
     * parseTitleDate メソッドのテスト - 準正常系:タイトルが null の場合
     */
    @Test
    public void testParseTitleDate_semiNull() {

        /* 期待値の定義 */

        /* 準備 */
        final String testTitle = null;

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.parseTitleDate(testTitle);

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualDate, "日付は null になる必要があります");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残り時間：N分」の場合
     */
    @Test
    public void testParseMinutes_normalRemainingTime() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(15);

        /* 準備 */
        final String testText = "（残り時間：15分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残り時間：N分」で小数の場合
     */
    @Test
    public void testParseMinutes_normalDecimal() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(8.5);

        /* 準備 */
        final String testText = "（残り時間：8.5分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「残りN分」の場合
     */
    @Test
    public void testParseMinutes_normalRemaining() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(9);

        /* 準備 */
        final String testText = "（残り9分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「先行分残りN分」の場合
     */
    @Test
    public void testParseMinutes_normalAdvanceRemaining() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(5);

        /* 準備 */
        final String testText = "（先行分残り5分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:「N分」の場合
     */
    @Test
    public void testParseMinutes_normalSimple() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(30);

        /* 準備 */
        final String testText = "（30分）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 正常系:半角括弧の場合
     */
    @Test
    public void testParseMinutes_normalHalfWidthParentheses() {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(15);

        /* 準備 */
        final String testText = "(15分)";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");

    }

    /**
     * parseMinutes メソッドのテスト - 準正常系:括弧がない場合
     */
    @Test
    public void testParseMinutes_semiNoParentheses() {

        /* 期待値の定義 */

        /* 準備 */
        final String testText = "";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualMinutes, "残り時間は null になる必要があります");

    }

    /**
     * parseMinutes メソッドのテスト - 準正常系:括弧内を解釈できない場合
     */
    @Test
    public void testParseMinutes_semiUnparsable() {

        /* 期待値の定義 */

        /* 準備 */
        final String testText = "（未定）";

        /* テスト対象の実行 */
        final Double testResult = CarryoverParser.parseMinutes(testText);

        /* 検証の準備 */
        final Double actualMinutes = testResult;

        /* 検証の実施 */
        Assertions.assertNull(actualMinutes, "残り時間は null になる必要があります");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:そのままの項目名の場合
     */
    @Test
    public void testNormalizeName_normalPlain() {

        /* 期待値の定義 */
        final String expectedName = "IT・技術学習";

        /* 準備 */
        final String testName = " IT・技術学習 ";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:末尾に括弧書きがある場合
     */
    @Test
    public void testNormalizeName_normalTrailingParentheses() {

        /* 期待値の定義 */
        final String expectedName = "汎用タスク";

        /* 準備 */
        final String testName = "汎用タスク（15分）";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 正常系:末尾に開き括弧がある場合
     */
    @Test
    public void testNormalizeName_normalTrailingOpenParentheses() {

        /* 期待値の定義 */
        final String expectedName = "フロントエンドまたはサーバー系に関係する学習";

        /* 準備 */
        final String testName = "フロントエンドまたはサーバー系に関係する学習(";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * normalizeName メソッドのテスト - 準正常系:空になる場合
     */
    @Test
    public void testNormalizeName_semiEmpty() {

        /* 期待値の定義 */
        final String expectedName = "不明";

        /* 準備 */
        final String testName = "（15分）";

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.normalizeName(testName);

        /* 検証の準備 */
        final String actualName = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedName, actualName, "項目名が一致しません");

    }

    /**
     * splitLines メソッドのテスト - 正常系:改行コードが混在する場合
     */
    @Test
    public void testSplitLines_normalMixedLineBreaks() {

        /* 期待値の定義 */
        final List<String> expectedLines = List.of("a", "b", "c");

        /* 準備 */
        final String testBody = "a\r\nb\nc";

        /* テスト対象の実行 */
        final List<String> testResult = CarryoverParser.splitLines(testBody);

        /* 検証の準備 */
        final List<String> actualLines = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLines, actualLines, "行が一致しません");

    }

    /**
     * splitLines メソッドのテスト - 準正常系:本文が null の場合
     */
    @Test
    public void testSplitLines_semiNull() {

        /* 期待値の定義 */
        final List<String> expectedLines = List.of();

        /* 準備 */
        final String testBody = null;

        /* テスト対象の実行 */
        final List<String> testResult = CarryoverParser.splitLines(testBody);

        /* 検証の準備 */
        final List<String> actualLines = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLines, actualLines, "行が一致しません");

    }

    /**
     * addSection メソッドのテスト - 正常系:対象セクションを追加する場合
     */
    @Test
    public void testAddSection_normalTarget() {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("持ち越し");

        /* 準備 */
        final List<String> testSections = new ArrayList<>();

        /* テスト対象の実行 */
        CarryoverParser.addSection(testSections, "持ち越し");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * addSection メソッドのテスト - 準正常系:追加済みのセクションの場合
     */
    @Test
    public void testAddSection_semiDuplicate() {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of("持ち越し");

        /* 準備 */
        final List<String> testSections = new ArrayList<>(List.of("持ち越し"));

        /* テスト対象の実行 */
        CarryoverParser.addSection(testSections, "持ち越し");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * addSection メソッドのテスト - 準正常系:対象外のセクションの場合
     */
    @Test
    public void testAddSection_semiNotTarget() {

        /* 期待値の定義 */
        final List<String> expectedSections = List.of();

        /* 準備 */
        final List<String> testSections = new ArrayList<>();

        /* テスト対象の実行 */
        CarryoverParser.addSection(testSections, "先行");

        /* 検証の準備 */
        final List<String> actualSections = testSections;

        /* 検証の実施 */
        Assertions.assertEquals(expectedSections, actualSections, "対象セクション名が一致しません");

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 正常系:空行の場合は警告しない
     */
    @Test
    public void testWarnUnexpectedLine_normalBlank() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.warnUnexpectedLine(371, "   ");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 正常系:区切り線の場合は警告しない
     */
    @Test
    public void testWarnUnexpectedLine_normalSeparator() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.warnUnexpectedLine(371, "-----");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * warnUnexpectedLine メソッドのテスト - 準正常系:想定外の行の場合は警告する
     */
    @Test
    public void testWarnUnexpectedLine_semiUnexpected() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 想定外の行です: メモ",
        };

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.warnUnexpectedLine(371, "メモ");

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 正常系:残数と解析件数が一致する場合
     */
    @Test
    public void testVerifyDeclaredCount_normalMatch() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.verifyDeclaredCount(371, Integer.valueOf(76), 76);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 正常系:残数の記載がない場合
     */
    @Test
    public void testVerifyDeclaredCount_normalNoDeclared() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {};

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.verifyDeclaredCount(371, null, 76);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * verifyDeclaredCount メソッドのテスト - 準正常系:残数と解析件数が一致しない場合
     */
    @Test
    public void testVerifyDeclaredCount_semiMismatch() {

        /* 期待値の定義 */
        final String[] expectedMsgs = {
            "#371 残数と解析件数が一致しません: 残=76, 解析=75",
        };

        /* 準備 */

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverParser.class)) {

            CarryoverParser.verifyDeclaredCount(371, Integer.valueOf(76), 75);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();

            /* 検証の実施 */
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * formatDate メソッドのテスト - 正常系:月・日をゼロ埋めする場合
     */
    @Test
    public void testFormatDate_normalZeroPadding() {

        /* 期待値の定義 */
        final String expectedDate = "2026-06-05";

        /* 準備 */

        /* テスト対象の実行 */
        final String testResult = CarryoverParser.formatDate("2026", "6", "5");

        /* 検証の準備 */
        final String actualDate = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedDate, actualDate, "日付が一致しません");

    }

}
