package io.github.kenichiroarai.dailytasks.carryover.application.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.CarryoverAggregator;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.CarryoverParser;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.github.GitHubIssueClient;
import io.github.kenichiroarai.dailytasks.carryover.repository.CarryoverDataRepository;
import io.github.kenichiroarai.dailytasks.testutil.LogAssertions;
import io.github.kenichiroarai.dailytasks.testutil.LogCapture;

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
     * テスト用の Issue 本文の解析
     */
    private static final CarryoverParser PARSER = new CarryoverParser(
        new DefaultMinutes(Map.of("国語", Double.valueOf(15))));

    /**
     * テスト用の一時ディレクトリ
     */
    @TempDir
    Path tempDir;

    /**
     * テスト用の GitHub API クライアント<br>
     * <p>
     * 通信せずに登録した Issue を返す。
     * </p>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubGitHubIssueClient extends GitHubIssueClient {

        /**
         * 返す Issue
         */
        private final List<DailyTaskIssue> issues;

        /**
         * コンストラクタ<br>
         *
         * @param issues
         *               返す Issue
         */
        StubGitHubIssueClient(final List<DailyTaskIssue> issues) {

            super(null, "https://api.example.com", "owner/repo", null);
            this.issues = issues;

        }

        /**
         * 登録した Issue を返す<br>
         *
         * @return 登録した Issue
         */
        @Override
        public List<DailyTaskIssue> fetchAllIssues() {

            final List<DailyTaskIssue> result = this.issues;
            return result;

        }

    }

    /**
     * テスト対象を作成する<br>
     *
     * @param repository
     *                   JSON ファイルの読み書き
     *
     * @return テスト対象
     */
    private static CarryoverServiceImpl createTarget(final CarryoverDataRepository repository) {

        final CarryoverServiceImpl result = new CarryoverServiceImpl(
            new StubGitHubIssueClient(List.of(CarryoverServiceImplTest.ISSUE1, CarryoverServiceImplTest.ISSUE2)),
            repository, CarryoverServiceImplTest.PARSER, new CarryoverAggregator());
        return result;

    }

    /**
     * collect メソッドのテスト - 正常系:全件モードの場合はすべての Issue を解析する
     */
    @Test
    public void testCollect_normalFull() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 2;
        final String[] expectedMsgs = {
            "モード=全件、取得=2 件、解析・保存=2 件、集計=2 日",
            "標準時間で補完した行=1 件、標準時間が未登録の行=0 件",
        };

        /* 準備 */
        final CarryoverDataRepository testRepository = new CarryoverDataRepository(this.tempDir);
        testRepository.saveIssue(CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testRepository);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverServiceImpl.class)) {

            final int testResult = testTarget.collect(true);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult;
            final boolean actualSummaryExists = Files.isRegularFile(this.tempDir.resolve("summary.json"));
            final boolean actualIssue2Exists = Files.isRegularFile(this.tempDir.resolve("issues").resolve("0002.json"));

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
            Assertions.assertTrue(actualSummaryExists, "集計の JSON が出力される必要があります");
            Assertions.assertTrue(actualIssue2Exists, "Issue #2 の JSON が出力される必要があります");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * collect メソッドのテスト - 正常系:差分モードの場合は更新日時が同じ保存済みの Issue を解析しない
     */
    @Test
    public void testCollect_normalDiff() throws IOException {

        /* 期待値の定義 */
        final int expectedCount = 1;
        final String[] expectedMsgs = {
            "モード=差分、取得=2 件、解析・保存=1 件、集計=2 日",
            "標準時間で補完した行=1 件、標準時間が未登録の行=0 件",
        };

        /* 準備 */
        final CarryoverDataRepository testRepository = new CarryoverDataRepository(this.tempDir);
        testRepository.saveIssue(CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1));
        final CarryoverServiceImpl testTarget = CarryoverServiceImplTest.createTarget(testRepository);

        /* テスト対象の実行 */
        try (LogCapture testLog = new LogCapture(CarryoverServiceImpl.class)) {

            final int testResult = testTarget.collect(false);

            /* 検証の準備 */
            final String[] actualMsgs = testLog.getMessages();
            final int actualCount = testResult;

            /* 検証の実施 */
            Assertions.assertEquals(expectedCount, actualCount, "解析件数が一致しません");
            LogAssertions.assertMessages(expectedMsgs, actualMsgs);

        }

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:全件モードの場合
     */
    @Test
    public void testNeedsUpdate_normalFull() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImpl.needsUpdate(true, testStored, CarryoverServiceImplTest.ISSUE1);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:未保存の場合
     */
    @Test
    public void testNeedsUpdate_normalNotStored() {

        /* 期待値の定義 */

        /* 準備 */

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImpl.needsUpdate(false, null, CarryoverServiceImplTest.ISSUE1);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 正常系:更新日時が変わった場合
     */
    @Test
    public void testNeedsUpdate_normalUpdated() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1);
        final DailyTaskIssue testRemote = new DailyTaskIssue(1, "2026年03月24日のタスク", "closed", "u9", "");

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImpl.needsUpdate(false, testStored, testRemote);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertTrue(actualNeedsUpdate, "解析し直すと判定される必要があります");

    }

    /**
     * needsUpdate メソッドのテスト - 準正常系:更新日時が同じ場合
     */
    @Test
    public void testNeedsUpdate_semiNotUpdated() {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverIssue testStored = CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1);

        /* テスト対象の実行 */
        final boolean testResult = CarryoverServiceImpl.needsUpdate(false, testStored,
            CarryoverServiceImplTest.ISSUE1);

        /* 検証の準備 */
        final boolean actualNeedsUpdate = testResult;

        /* 検証の実施 */
        Assertions.assertFalse(actualNeedsUpdate, "解析し直さないと判定される必要があります");

    }

    /**
     * countBySource メソッドのテスト - 正常系:取得元ごとの行数を数える場合
     */
    @Test
    public void testCountBySource_normalCount() {

        /* 期待値の定義 */
        final long expectedCount = 1;

        /* 準備 */
        final Map<Integer, CarryoverIssue> testIssues = Map.of(Integer.valueOf(1),
            CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE1), Integer.valueOf(2),
            CarryoverServiceImplTest.PARSER.parse(CarryoverServiceImplTest.ISSUE2));

        /* テスト対象の実行 */
        final long testResult = CarryoverServiceImpl.countBySource(testIssues, MinutesSource.PARSED);

        /* 検証の準備 */
        final long actualCount = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "行数が一致しません");

    }

}
