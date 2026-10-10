package io.github.kenichiroarai.dailytasks.carryover.application.service.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.impl.CarryoverAggregatorImpl;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.CarryoverParser;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.impl.CarryoverParserImpl;
import io.github.kenichiroarai.dailytasks.carryover.domain.service.CarryoverIssueService;
import io.github.kenichiroarai.dailytasks.testutil.LogAssertions;
import io.github.kenichiroarai.dailytasks.testutil.LogCapture;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverServiceImpl} のテスト<br>
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
public class CarryoverServiceImplTest {

    /**
     * テスト用の Issue #1（時間表記なし、更新日時 u1）
     */
    private static final DailyTaskIssue ISSUE1 = new DailyTaskIssue(1, "2026年03月24日のタスク", "closed", "u1",
        "## 負債\n- [ ] 国語2026/03/20");

    /**
     * テスト用の Issue #2（時間表記あり、更新日時 u2）
     */
    private static final DailyTaskIssue ISSUE2 = new DailyTaskIssue(2, "2026年03月25日のタスク", "open", "u2",
        "## 負債\n- [ ] 英語2026/03/20（残り時間：10分）");

    /**
     * テスト用の標準時間
     */
    private static final DefaultMinutes DEFAULT_MINUTES = new DefaultMinutes(Map.of("国語", Double.valueOf(15)));

    /**
     * テスト用の Issue 本文の解析
     */
    private static final CarryoverParser PARSER = new CarryoverParserImpl(MessageProviderTestUtil.create());

    /**
     * テスト用の持ち越しの収集・集計の設定
     */
    private static final CarryoverSettings SETTINGS = new CarryoverSettings("owner/repo", "test-token",
        Path.of("docs", "data"), Path.of("config", "default-minutes.json"), 10);

    /**
     * テスト用の持ち越しのデータの取得・保存サービス<br>
     * <p>
     * 通信やファイルの入出力をせず、登録した Issue と保存済みの解析結果をメモリ上で扱う。
     * </p>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubCarryoverIssueService implements CarryoverIssueService {

        /**
         * 取得する Issue
         */
        private final List<DailyTaskIssue> remoteIssues;

        /**
         * 保存済みの解析結果
         */
        private final Map<Integer, CarryoverIssue> storedIssues = new TreeMap<>();

        /**
         * 保存した Issue 番号
         */
        private final List<Integer> savedNumbers = new ArrayList<>();

        /**
         * 保存した集計
         */
        private CarryoverSummary savedSummary;

        /**
         * 受け取ったデータの取得元と保存先
         */
        private final List<CarryoverSource> sources = new ArrayList<>();

        /**
         * コンストラクタ<br>
         *
         * @param remoteIssues
         *                     取得する Issue
         */
        private StubCarryoverIssueService(final List<DailyTaskIssue> remoteIssues) {

            this.remoteIssues = remoteIssues;

        }

        /**
         * 保存済みの解析結果を登録する<br>
         *
         * @param issue
         *              解析結果
         */
        private void store(final CarryoverIssue issue) {

            this.storedIssues.put(Integer.valueOf(issue.getNumber()), issue);

        }

        /**
         * 登録した Issue を返す<br>
         *
         * @param source
         *               データの取得元と保存先
         *
         * @return 登録した Issue
         */
        @Override
        public List<DailyTaskIssue> fetchAllIssues(final CarryoverSource source) {

            this.sources.add(source);
            final List<DailyTaskIssue> result = this.remoteIssues;
            return result;

        }

        /**
         * 保存済みの解析結果を返す<br>
         *
         * @param source
         *               データの取得元と保存先
         *
         * @return 保存済みの解析結果
         */
        @Override
        public Map<Integer, CarryoverIssue> loadIssues(final CarryoverSource source) {

            this.sources.add(source);
            final Map<Integer, CarryoverIssue> result = new TreeMap<>(this.storedIssues);
            return result;

        }

        /**
         * 保存した Issue 番号を記録する<br>
         *
         * @param source
         *               データの取得元と保存先
         * @param issue
         *               解析結果
         */
        @Override
        public void saveIssue(final CarryoverSource source, final CarryoverIssue issue) {

            this.sources.add(source);
            this.savedNumbers.add(Integer.valueOf(issue.getNumber()));

        }

        /**
         * 保存した集計を記録する<br>
         *
         * @param source
         *                データの取得元と保存先
         * @param summary
         *                画面用の集計
         */
        @Override
        public void saveSummary(final CarryoverSource source, final CarryoverSummary summary) {

            this.sources.add(source);
            this.savedSummary = summary;

        }

        /**
         * テスト用の標準時間を返す<br>
         *
         * @param source
         *               データの取得元と保存先
         *
         * @return テスト用の標準時間
         */
        @Override
        public DefaultMinutes loadDefaultMinutes(final CarryoverSource source) {

            this.sources.add(source);
            final DefaultMinutes result = CarryoverServiceImplTest.DEFAULT_MINUTES;
            return result;

        }

        /**
         * 受け取ったデータの取得元と保存先を返す<br>
         *
         * @return 受け取ったデータの取得元と保存先（呼び出し順）
         */
        private List<CarryoverSource> getSources() {

            final List<CarryoverSource> result = this.sources;
            return result;

        }

        /**
         * 保存した Issue 番号を返す<br>
         *
         * @return 保存した Issue 番号
         */
        private List<Integer> getSavedNumbers() {

            final List<Integer> result = this.savedNumbers;
            return result;

        }

        /**
         * 保存した集計を返す<br>
         *
         * @return 保存した集計。保存していない場合は null
         */
        private CarryoverSummary getSavedSummary() {

            final CarryoverSummary result = this.savedSummary;
            return result;

        }

    }

    /**
     * テスト対象を作成する<br>
     *
     * @param issueService
     *                     持ち越しのデータの取得・保存
     *
     * @return テスト対象
     */
    private static CarryoverServiceImpl createTarget(final CarryoverIssueService issueService) {

        final CarryoverServiceImpl result = new CarryoverServiceImpl(issueService, CarryoverServiceImplTest.PARSER,
            new CarryoverAggregatorImpl(), MessageProviderTestUtil.create());
        return result;

    }

    /**
     * テスト用の標準時間で Issue を解析する<br>
     *
     * @param issue
     *              日々のタスク Issue
     *
     * @return 持ち越しの解析結果
     */
    private static CarryoverIssue parse(final DailyTaskIssue issue) {

        final CarryoverIssue result = CarryoverServiceImplTest.PARSER.parse(issue,
            CarryoverServiceImplTest.DEFAULT_MINUTES);
        return result;

    }

    /**
     * private の toSource メソッドを呼び出す<br>
     *
     * @param settings
     *                 持ち越しの収集・集計の設定
     *
     * @return データの取得元と保存先
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static CarryoverSource toSource(final CarryoverSettings settings) throws Exception {

        final CarryoverSource result = ReflectionTestUtil.invokeStatic(CarryoverServiceImpl.class, "toSource",
            new Class<?>[] {
                CarryoverSettings.class
            }, settings);
        return result;

    }

    /**
     * private の needsUpdate メソッドを呼び出す<br>
     *
     * @param full
     *                    全件モードか
     * @param stored
     *                    保存済みの解析結果
     * @param remoteIssue
     *                    取得した Issue
     * @param recentFrom
     *                    毎回解析し直す Issue 番号の下限
     *
     * @return 解析し直す場合は true
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static boolean needsUpdate(final boolean full, final CarryoverIssue stored,
        final DailyTaskIssue remoteIssue, final int recentFrom) throws Exception {

        final boolean result = ReflectionTestUtil.<Boolean> invokeStatic(CarryoverServiceImpl.class, "needsUpdate",
            new Class<?>[] {
                boolean.class, CarryoverIssue.class, DailyTaskIssue.class, int.class
            }, Boolean.valueOf(full), stored, remoteIssue, Integer.valueOf(recentFrom)).booleanValue();
        return result;

    }

    /**
     * private の recentFrom メソッドを呼び出す<br>
     *
     * @param issues
     *               取得した Issue
     * @param count
     *               件数
     *
     * @return 毎回解析し直す Issue 番号の下限
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static int recentFrom(final List<DailyTaskIssue> issues, final int count) throws Exception {

        final int result = ReflectionTestUtil.<Integer> invokeStatic(CarryoverServiceImpl.class, "recentFrom",
            new Class<?>[] {
                List.class, int.class
            }, issues, Integer.valueOf(count)).intValue();
        return result;

    }

    /**
     * private の countBySource メソッドを呼び出す<br>
     *
     * @param issues
     *               解析結果
     * @param source
     *               残り時間の取得元
     *
     * @return 行数
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static long countBySource(final Map<Integer, CarryoverIssue> issues, final MinutesSource source)
        throws Exception {

        final long result = ReflectionTestUtil.<Long> invokeStatic(CarryoverServiceImpl.class, "countBySource",
            new Class<?>[] {
                Map.class, MinutesSource.class
            }, issues, source).longValue();
        return result;

    }

    /**
     * #1 から指定件数までの Issue を作成する<br>
     * <p>
     * Issue #N の日付は 2026年03月N日、更新日時は uN とする。
     * </p>
     *
     * @param count
     *              件数
     *
     * @return 作成した Issue
     */
    private static List<DailyTaskIssue> createIssues(final int count) {

        final List<DailyTaskIssue> result = IntStream.rangeClosed(1, count)
            .mapToObj(number -> new DailyTaskIssue(number, String.format("2026年03月%02d日のタスク", Integer.valueOf(number)),
                "closed", "u" + number, "## 負債\n- [ ] 英語2026/03/01（残り時間：10分）"))
            .toList();
        return result;

    }

    /**
     * collect メソッドのテスト - 正常系:設定を domain 層のデータの取得元に変換して渡す場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testCollect_normalSource() throws IOException {

        /* 期待値の定義 */
        final int expectedSourceCount = 5;
        final String expectedRepository = "owner/repo";
        final Path expectedDataDir = Path.of("docs", "data");

        /* 準備 */
        final StubCarryoverIssueService testIssueService = new StubCarryoverIssueService(
            List.of(CarryoverServiceImplTest.ISSUE1));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testIssueService);

        /* テスト対象の実行 */
        testTarget.collect(CarryoverServiceImplTest.SETTINGS, true);

        /* 検証の準備 */
        final List<CarryoverSource> actualSources = testIssueService.getSources();
        final int actualSourceCount = actualSources.size();
        final String actualRepository = actualSources.get(0).getRepository();
        final Path actualDataDir = actualSources.get(0).getDataDir();

        /* 検証の実施 */
        Assertions.assertEquals(expectedSourceCount, actualSourceCount, "取得元を受け取った回数が一致しません");
        Assertions.assertEquals(expectedRepository, actualRepository, "リポジトリが一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "保存先が一致しません");

    }

    /**
     * collect メソッドのテスト - 正常系:全件モードの場合はすべての Issue を解析する
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testCollect_normalFull() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 2;
        final List<Integer> expectedSavedNumbers = List.of(Integer.valueOf(1), Integer.valueOf(2));
        final int expectedDays = 2;
        final String[] expectedMsgs = {
            "モード=全件、取得=2 件、解析・保存=2 件、集計=2 日",
            "標準時間で補完した行=1 件、標準時間が未登録の行=0 件",
        };

        /* 準備 */
        final StubCarryoverIssueService testIssueService = new StubCarryoverIssueService(
            List.of(CarryoverServiceImplTest.ISSUE1, CarryoverServiceImplTest.ISSUE2));
        testIssueService.store(CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testIssueService);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverServiceImpl.class)) {

            final int testResult = testTarget.collect(CarryoverServiceImplTest.SETTINGS, true);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult;
            final List<Integer> actualSavedNumbers = testIssueService.getSavedNumbers();
            final int actualDays = testIssueService.getSavedSummary().getDays().size();

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
            Assertions.assertEquals(expectedSavedNumbers, actualSavedNumbers, "保存した Issue 番号が一致しません");
            Assertions.assertEquals(expectedDays, actualDays, "集計の日数が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * collect メソッドのテスト - 正常系:差分モードの場合は最新 10 件より前で更新日時が同じ保存済みの Issue を解析しない
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testCollect_normalDiff() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 10;
        final String[] expectedMsgs = {
            "モード=差分、取得=12 件、解析・保存=10 件、集計=12 日",
            "標準時間で補完した行=0 件、標準時間が未登録の行=0 件",
        };

        /* 準備 */
        final List<DailyTaskIssue> testIssues = CarryoverServiceImplTest.createIssues(12);
        final StubCarryoverIssueService testIssueService = new StubCarryoverIssueService(testIssues);
        testIssueService.store(CarryoverServiceImplTest.parse(testIssues.get(0)));
        testIssueService.store(CarryoverServiceImplTest.parse(testIssues.get(1)));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testIssueService);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverServiceImpl.class)) {

            final int testResult = testTarget.collect(CarryoverServiceImplTest.SETTINGS, false);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult;

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * collect メソッドのテスト - 正常系:差分モードの場合は最新 10 件の保存済みの Issue を更新日時が同じでも解析し直す
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testCollect_normalDiffRecent() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 2;
        final String[] expectedMsgs = {
            "モード=差分、取得=2 件、解析・保存=2 件、集計=2 日",
            "標準時間で補完した行=1 件、標準時間が未登録の行=0 件",
        };

        /* 準備 */
        final StubCarryoverIssueService testIssueService = new StubCarryoverIssueService(
            List.of(CarryoverServiceImplTest.ISSUE1, CarryoverServiceImplTest.ISSUE2));
        testIssueService.store(CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testIssueService);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverServiceImpl.class)) {

            final int testResult = testTarget.collect(CarryoverServiceImplTest.SETTINGS, false);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult;

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * toSource メソッドのテスト - 正常系:設定を domain 層の取得元に引き継ぐ場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testToSource_normalConvert() throws Exception {

        /* 期待値の定義 */
        final String expectedRepository = "owner/repo";
        final String expectedToken = "test-token";
        final Path expectedDataDir = Path.of("docs", "data");
        final Path expectedDefaultMinutesFile = Path.of("config", "default-minutes.json");

        /* 準備 */
        final CarryoverSettings testSettings = new CarryoverSettings(expectedRepository, expectedToken,
            expectedDataDir, expectedDefaultMinutesFile, 10);

        /* テスト対象の実行 */
        final CarryoverSource testResult = CarryoverServiceImplTest.toSource(testSettings);

        /* 検証の準備 */
        final String actualRepository = testResult.getRepository();
        final String actualToken = testResult.getToken();
        final Path actualDataDir = testResult.getDataDir();
        final Path actualDefaultMinutesFile = testResult.getDefaultMinutesFile();

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "リポジトリが一致しません");
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "保存先が一致しません");
        Assertions.assertEquals(expectedDefaultMinutesFile, actualDefaultMinutesFile, "標準時間の設定ファイルが一致しません");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:全件モードの場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNeedsUpdate_normalFull() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImplTest.needsUpdate(true, testStored, CarryoverServiceImplTest.ISSUE1,
            2);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:未保存の場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNeedsUpdate_normalNotStored() throws Exception {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImplTest.needsUpdate(false, null, CarryoverServiceImplTest.ISSUE1, 2);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:更新日時が変わった場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNeedsUpdate_normalUpdated() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1);
        final DailyTaskIssue testRemote = new DailyTaskIssue(1, "2026年03月24日のタスク", "closed", "u9", "");

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImplTest.needsUpdate(false, testStored, testRemote, 2);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:最新の Issue の場合は更新日時が同じでも解析し直す
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNeedsUpdate_normalRecent() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImplTest.needsUpdate(false, testStored,
            CarryoverServiceImplTest.ISSUE1, 1);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 準正常系:更新日時が同じ場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testNeedsUpdate_semiNotUpdated() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImplTest.needsUpdate(false, testStored,
            CarryoverServiceImplTest.ISSUE1, 2);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualNeedsUpdate, "解析し直さないと判定される必要があります");

    }

    /**
     * recentFrom メソッドのテスト - 正常系:件数より多い Issue がある場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testRecentFrom_normalMoreThanCount() throws Exception {

        /* 期待値の定義 */
        final int expectedRecentFrom = 3;

        /* 準備 */
        final List<DailyTaskIssue> testIssues = CarryoverServiceImplTest.createIssues(12);

        /* テスト対象の実行 */
        final int testResult = CarryoverServiceImplTest.recentFrom(testIssues, 10);

        /* 検証の準備 */
        final int actualRecentFrom = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRecentFrom, actualRecentFrom, "Issue 番号の下限が一致しません");

    }

    /**
     * recentFrom メソッドのテスト - 正常系:件数より少ない Issue しかない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testRecentFrom_normalLessThanCount() throws Exception {

        /* 期待値の定義 */
        final int expectedRecentFrom = 1;

        /* 準備 */
        final List<DailyTaskIssue> testIssues = List.of(CarryoverServiceImplTest.ISSUE2,
            CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final int testResult = CarryoverServiceImplTest.recentFrom(testIssues, 10);

        /* 検証の準備 */
        final int actualRecentFrom = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRecentFrom, actualRecentFrom, "Issue 番号の下限が一致しません");

    }

    /**
     * recentFrom メソッドのテスト - 準正常系:Issue がない場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testRecentFrom_semiEmpty() throws Exception {

        /* 期待値の定義 */
        final int expectedRecentFrom = 0;

        /* 準備 */
        final List<DailyTaskIssue> testIssues = List.of();

        /* テスト対象の実行 */
        final int testResult = CarryoverServiceImplTest.recentFrom(testIssues, 10);

        /* 検証の準備 */
        final int actualRecentFrom = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedRecentFrom, actualRecentFrom, "Issue 番号の下限が一致しません");

    }

    /**
     * countBySource メソッドのテスト - 正常系:取得元ごとの行数を数える場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testCountBySource_normalCount() throws Exception {

        /* 期待値の定義 */
        final long expectedCount = 1;

        /* 準備 */
        final Map<Integer, CarryoverIssue> testIssues = Map.of(Integer.valueOf(1),
            CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE1), Integer.valueOf(2),
            CarryoverServiceImplTest.parse(CarryoverServiceImplTest.ISSUE2));

        /* テスト対象の実行 */
        final long testResult = CarryoverServiceImplTest.countBySource(testIssues, MinutesSource.PARSED);

        /* 検証の準備 */
        final long actualCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "行数が一致しません");

    }

}
