package io.github.kenichiroarai.dailytasks.carryover.domain.service.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.repository.CarryoverDataRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.DefaultMinutesRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.impl.GitHubIssueRepositoryImpl;
import io.github.kenichiroarai.dailytasks.carryover.repository.impl.CarryoverDataRepositoryImpl;
import io.github.kenichiroarai.dailytasks.carryover.repository.impl.DefaultMinutesRepositoryImpl;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverIssueServiceImpl} のテスト<br>
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
public class CarryoverIssueServiceImplTest {

    /**
     * CarryoverIssueServiceImpl コンストラクタのテスト - 正常系:GitHub の Issue の取得を生成する場合
     *
     * @throws Exception
     *                   フィールドの取得に失敗した場合
     */
    @Test
    public void testCarryoverIssueServiceImpl_normalGitHubIssueRepository() throws Exception {

        /* 期待値の定義 */
        final Class<GitHubIssueRepositoryImpl> expectedType = GitHubIssueRepositoryImpl.class;

        /* 準備 */
        final CarryoverSource testSource = CarryoverIssueServiceImplTest.createSource();

        /* テスト対象の実行 */
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(testSource);

        /* 検証の準備 */
        final Object actualRepository = ReflectionTestUtil.getField(testTarget, "gitHubIssueRepository");

        /* 検証の実施 */
        Assertions.assertInstanceOf(expectedType, actualRepository, "GitHub の Issue の取得の型が一致しません");

    }

    /**
     * CarryoverIssueServiceImpl コンストラクタのテスト - 正常系:JSON ファイルの読み書きを生成する場合
     *
     * @throws Exception
     *                   フィールドの取得に失敗した場合
     */
    @Test
    public void testCarryoverIssueServiceImpl_normalCarryoverDataRepository() throws Exception {

        /* 期待値の定義 */
        final Class<CarryoverDataRepositoryImpl> expectedType = CarryoverDataRepositoryImpl.class;

        /* 準備 */
        final CarryoverSource testSource = CarryoverIssueServiceImplTest.createSource();

        /* テスト対象の実行 */
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(testSource);

        /* 検証の準備 */
        final Object actualRepository = ReflectionTestUtil.getField(testTarget, "carryoverDataRepository");

        /* 検証の実施 */
        Assertions.assertInstanceOf(expectedType, actualRepository, "JSON ファイルの読み書きの型が一致しません");

    }

    /**
     * CarryoverIssueServiceImpl コンストラクタのテスト - 正常系:標準時間の設定の読み込みを生成する場合
     *
     * @throws Exception
     *                   フィールドの取得に失敗した場合
     */
    @Test
    public void testCarryoverIssueServiceImpl_normalDefaultMinutesRepository() throws Exception {

        /* 期待値の定義 */
        final Class<DefaultMinutesRepositoryImpl> expectedType = DefaultMinutesRepositoryImpl.class;

        /* 準備 */
        final CarryoverSource testSource = CarryoverIssueServiceImplTest.createSource();

        /* テスト対象の実行 */
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(testSource);

        /* 検証の準備 */
        final Object actualRepository = ReflectionTestUtil.getField(testTarget, "defaultMinutesRepository");

        /* 検証の実施 */
        Assertions.assertInstanceOf(expectedType, actualRepository, "標準時間の設定の読み込みの型が一致しません");

    }

    /**
     * fetchAllIssues メソッドのテスト - 正常系:取得した Issue をモデルに変換する場合
     *
     * @throws Exception
     *                   取得に失敗した場合
     */
    @Test
    public void testFetchAllIssues_normalConvert() throws Exception {

        /* 期待値の定義 */
        final String expectedTitle = "2026年10月06日のタスク";

        /* 準備 */
        final StubGitHubIssueRepository testGitHub = new StubGitHubIssueRepository(List
            .of(new GitHubIssueDto(371, "2026年10月06日のタスク", "open", "2026-10-06T14:23:08Z", "本文")));
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(testGitHub,
            new StubCarryoverDataRepository(Map.of()), new StubDefaultMinutesRepository(Map.of()));

        /* テスト対象の実行 */
        final List<DailyTaskIssue> testResult = testTarget.fetchAllIssues();

        /* 検証の準備 */
        final String actualTitle = testResult.get(0).getTitle();

        /* 検証の実施 */
        Assertions.assertEquals(expectedTitle, actualTitle, "タイトルが一致しません");

    }

    /**
     * loadIssues メソッドのテスト - 正常系:保存済みの解析結果をモデルに変換する場合
     *
     * @throws Exception
     *                   読み込みに失敗した場合
     */
    @Test
    public void testLoadIssues_normalConvert() throws Exception {

        /* 期待値の定義 */
        final MinutesSource expectedMinutesSource = MinutesSource.UNKNOWN;

        /* 準備 */
        final CarryoverItemDto testItem = new CarryoverItemDto("国語", "2026-06-18", false, 0.0, "unknown", "持ち越し",
            "- [ ] 国語2026/06/18");
        final CarryoverIssueDto testDto = new CarryoverIssueDto(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), null, 1, 0.0, List.of(testItem));
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(
            new StubGitHubIssueRepository(List.of()), new StubCarryoverDataRepository(Map.of(371, testDto)),
            new StubDefaultMinutesRepository(Map.of()));

        /* テスト対象の実行 */
        final Map<Integer, CarryoverIssue> testResult = testTarget.loadIssues();

        /* 検証の準備 */
        final MinutesSource actualMinutesSource = testResult.get(371).getItems().get(0).getMinutesSource();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * saveIssue メソッドのテスト - 正常系:解析結果を DTO に変換して保存する場合
     *
     * @throws Exception
     *                   書き込みに失敗した場合
     */
    @Test
    public void testSaveIssue_normalConvert() throws Exception {

        /* 期待値の定義 */
        final String expectedMinutesSource = "parsed";

        /* 準備 */
        final StubCarryoverDataRepository testData = new StubCarryoverDataRepository(Map.of());
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(
            new StubGitHubIssueRepository(List.of()), testData, new StubDefaultMinutesRepository(Map.of()));
        final CarryoverItem testItem = new CarryoverItem("音楽", "2026-08-18", false, 11.5, MinutesSource.PARSED,
            "持ち越し", "- [ ] 音楽2026/08/18（残り時間：11.5分）");
        final CarryoverIssue testIssue = new CarryoverIssue(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), Integer.valueOf(1), List.of(testItem));

        /* テスト対象の実行 */
        testTarget.saveIssue(testIssue);

        /* 検証の準備 */
        final String actualMinutesSource = testData.getSavedIssues().get(0).getItems().get(0).getMinutesSource();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * saveSummary メソッドのテスト - 正常系:集計を DTO に変換して保存する場合
     *
     * @throws Exception
     *                   書き込みに失敗した場合
     */
    @Test
    public void testSaveSummary_normalConvert() throws Exception {

        /* 期待値の定義 */
        final int expectedLatestIssue = 371;

        /* 準備 */
        final StubCarryoverDataRepository testData = new StubCarryoverDataRepository(Map.of());
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(
            new StubGitHubIssueRepository(List.of()), testData, new StubDefaultMinutesRepository(Map.of()));
        final CarryoverSummary testSummary = new CarryoverSummary(371, List.of(), List.of());

        /* テスト対象の実行 */
        testTarget.saveSummary(testSummary);

        /* 検証の準備 */
        final int actualLatestIssue = testData.getSavedSummary().getLatestIssue();

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * loadDefaultMinutes メソッドのテスト - 正常系:標準時間をモデルに変換する場合
     *
     * @throws Exception
     *                   読み込みに失敗した場合
     */
    @Test
    public void testLoadDefaultMinutes_normalConvert() throws Exception {

        /* 期待値の定義 */
        final Double expectedMinutes = Double.valueOf(15.0);

        /* 準備 */
        final CarryoverIssueServiceImpl testTarget = new CarryoverIssueServiceImpl(
            new StubGitHubIssueRepository(List.of()), new StubCarryoverDataRepository(Map.of()),
            new StubDefaultMinutesRepository(Map.of("音楽", Double.valueOf(15.0))));

        /* テスト対象の実行 */
        final DefaultMinutes testResult = testTarget.loadDefaultMinutes();

        /* 検証の準備 */
        final Double actualMinutes = testResult.find("音楽");

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "標準時間が一致しません");

    }

    /**
     * データの取得元と保存先を作成する<br>
     *
     * @return データの取得元と保存先
     */
    private static CarryoverSource createSource() {

        final CarryoverSource result = new CarryoverSource("owner/repo", "test-token", Path.of("docs", "data"),
            Path.of("config", "default-minutes.json"));
        return result;

    }

    /**
     * テスト用の GitHub の Issue の取得<br>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubGitHubIssueRepository implements GitHubIssueRepository {

        /**
         * 返す Issue
         */
        private final List<GitHubIssueDto> issues;

        /**
         * コンストラクタ<br>
         *
         * @param issues
         *               返す Issue
         */
        private StubGitHubIssueRepository(final List<GitHubIssueDto> issues) {

            this.issues = issues;

        }

        /**
         * 設定した Issue を返す<br>
         *
         * @return 設定した Issue
         */
        @Override
        public List<GitHubIssueDto> fetchAllIssues() {

            final List<GitHubIssueDto> result = this.issues;
            return result;

        }

    }

    /**
     * テスト用の JSON ファイルの読み書き<br>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubCarryoverDataRepository implements CarryoverDataRepository {

        /**
         * 読み込み結果として返す解析結果
         */
        private final Map<Integer, CarryoverIssueDto> issues;

        /**
         * 保存された解析結果
         */
        private final List<CarryoverIssueDto> savedIssues = new ArrayList<>();

        /**
         * 保存された集計
         */
        private CarryoverSummaryDto savedSummary;

        /**
         * コンストラクタ<br>
         *
         * @param issues
         *               読み込み結果として返す解析結果
         */
        private StubCarryoverDataRepository(final Map<Integer, CarryoverIssueDto> issues) {

            this.issues = issues;

        }

        /**
         * 設定した解析結果を返す<br>
         *
         * @return 設定した解析結果
         */
        @Override
        public Map<Integer, CarryoverIssueDto> loadIssues() {

            final Map<Integer, CarryoverIssueDto> result = this.issues;
            return result;

        }

        /**
         * 解析結果を記録する<br>
         *
         * @param issue
         *              解析結果
         */
        @Override
        public void saveIssue(final CarryoverIssueDto issue) {

            this.savedIssues.add(issue);

        }

        /**
         * 集計を記録する<br>
         *
         * @param summary
         *                集計
         */
        @Override
        public void saveSummary(final CarryoverSummaryDto summary) {

            this.savedSummary = summary;

        }

        /**
         * 保存された解析結果を返す<br>
         *
         * @return 保存された解析結果
         */
        private List<CarryoverIssueDto> getSavedIssues() {

            final List<CarryoverIssueDto> result = this.savedIssues;
            return result;

        }

        /**
         * 保存された集計を返す<br>
         *
         * @return 保存された集計
         */
        private CarryoverSummaryDto getSavedSummary() {

            final CarryoverSummaryDto result = this.savedSummary;
            return result;

        }

    }

    /**
     * テスト用の標準時間の設定の読み込み<br>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubDefaultMinutesRepository implements DefaultMinutesRepository {

        /**
         * 返す標準時間
         */
        private final Map<String, Double> minutesByName;

        /**
         * コンストラクタ<br>
         *
         * @param minutesByName
         *                      返す標準時間
         */
        private StubDefaultMinutesRepository(final Map<String, Double> minutesByName) {

            this.minutesByName = minutesByName;

        }

        /**
         * 設定した標準時間を返す<br>
         *
         * @return 設定した標準時間
         */
        @Override
        public Map<String, Double> load() {

            final Map<String, Double> result = this.minutesByName;
            return result;

        }

    }

}
