package io.github.kenichiroarai.dailytasks.carryover.domain.service.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.converter.CarryoverDtoConverter;
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
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;

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
         * 最後に受け取った出力先のディレクトリ
         */
        private Path dataDir;

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
         * 最後に受け取った出力先のディレクトリを返す<br>
         *
         * @return 最後に受け取った出力先のディレクトリ
         */
        private Path getDataDir() {

            final Path result = this.dataDir;
            return result;

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

        /**
         * 出力先のディレクトリを記録し、設定した解析結果を返す<br>
         *
         * @param targetDir
         *                  出力先のディレクトリ
         *
         * @return 設定した解析結果
         */
        @Override
        public Map<Integer, CarryoverIssueDto> loadIssues(final Path targetDir) {

            this.dataDir = targetDir;
            final Map<Integer, CarryoverIssueDto> result = this.issues;
            return result;

        }

        /**
         * 出力先のディレクトリと解析結果を記録する<br>
         *
         * @param targetDir
         *                  出力先のディレクトリ
         * @param issue
         *                  解析結果
         */
        @Override
        public void saveIssue(final Path targetDir, final CarryoverIssueDto issue) {

            this.dataDir = targetDir;
            this.savedIssues.add(issue);

        }

        /**
         * 出力先のディレクトリと集計を記録する<br>
         *
         * @param targetDir
         *                  出力先のディレクトリ
         * @param summary
         *                  集計
         */
        @Override
        public void saveSummary(final Path targetDir, final CarryoverSummaryDto summary) {

            this.dataDir = targetDir;
            this.savedSummary = summary;

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
         * 受け取った設定ファイルのパス
         */
        private Path configFile;

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
         * 受け取った設定ファイルのパスを返す<br>
         *
         * @return 受け取った設定ファイルのパス
         */
        private Path getConfigFile() {

            final Path result = this.configFile;
            return result;

        }

        /**
         * 設定ファイルのパスを記録し、設定した標準時間を返す<br>
         *
         * @param file
         *             設定ファイルのパス
         *
         * @return 設定した標準時間
         */
        @Override
        public Map<String, Double> load(final Path file) {

            this.configFile = file;
            final Map<String, Double> result = this.minutesByName;
            return result;

        }

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
         * 受け取った接続設定
         */
        private GitHubSettingsDto settings;

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
         * 接続設定を記録し、設定した Issue を返す<br>
         *
         * @param gitHubSettings
         *                       接続設定
         *
         * @return 設定した Issue
         */
        @Override
        public List<GitHubIssueDto> fetchAllIssues(final GitHubSettingsDto gitHubSettings) {

            this.settings = gitHubSettings;
            final List<GitHubIssueDto> result = this.issues;
            return result;

        }

        /**
         * 受け取った接続設定を返す<br>
         *
         * @return 受け取った接続設定
         */
        private GitHubSettingsDto getSettings() {

            final GitHubSettingsDto result = this.settings;
            return result;

        }

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
     * テスト対象を作成する<br>
     *
     * @param gitHubIssueRepository
     *                                 GitHub の Issue の取得
     * @param carryoverDataRepository
     *                                 JSON ファイルの読み書き
     * @param defaultMinutesRepository
     *                                 標準時間の設定の読み込み
     *
     * @return テスト対象
     */
    private static CarryoverIssueServiceImpl createTarget(final GitHubIssueRepository gitHubIssueRepository,
        final CarryoverDataRepository carryoverDataRepository,
        final DefaultMinutesRepository defaultMinutesRepository) {

        final CarryoverIssueServiceImpl result
            = new CarryoverIssueServiceImpl(gitHubIssueRepository, carryoverDataRepository, defaultMinutesRepository,
                new CarryoverDtoConverter(MessageProviderTestUtil.create()));
        return result;

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
        final StubGitHubIssueRepository testGitHub = new StubGitHubIssueRepository(
            List.of(new GitHubIssueDto(371, "2026年10月06日のタスク", "open", "2026-10-06T14:23:08Z", "本文")));
        final CarryoverIssueServiceImpl testTarget = CarryoverIssueServiceImplTest.createTarget(testGitHub,
            new StubCarryoverDataRepository(Map.of()), new StubDefaultMinutesRepository(Map.of()));

        /* テスト対象の実行 */
        final List<DailyTaskIssue> testResult = testTarget.fetchAllIssues(CarryoverIssueServiceImplTest.createSource());

        /* 検証の準備 */
        final String actualTitle = testResult.get(0).getTitle();

        /* 検証の実施 */
        Assertions.assertEquals(expectedTitle, actualTitle, "タイトルが一致しません");

    }

    /**
     * fetchAllIssues メソッドのテスト - 正常系:データの取得元を接続設定に変換して渡す場合
     *
     * @throws Exception
     *                   取得に失敗した場合
     */
    @Test
    public void testFetchAllIssues_normalSettings() throws Exception {

        /* 期待値の定義 */
        final String expectedRepository = "owner/repo";
        final String expectedToken      = "test-token";

        /* 準備 */
        final StubGitHubIssueRepository testGitHub = new StubGitHubIssueRepository(List.of());
        final CarryoverIssueServiceImpl testTarget = CarryoverIssueServiceImplTest.createTarget(testGitHub,
            new StubCarryoverDataRepository(Map.of()), new StubDefaultMinutesRepository(Map.of()));

        /* テスト対象の実行 */
        testTarget.fetchAllIssues(CarryoverIssueServiceImplTest.createSource());

        /* 検証の準備 */
        final String actualRepository = testGitHub.getSettings().getRepository();
        final String actualToken      = testGitHub.getSettings().getToken();

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "対象リポジトリが一致しません");
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");

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
        final Double expectedMinutes    = 15.0;
        final Path   expectedConfigFile = Path.of("config", "default-minutes.json");

        /* 準備 */
        final StubDefaultMinutesRepository testDefaultMinutes = new StubDefaultMinutesRepository(
            Map.of("音楽", 15.0));
        final CarryoverIssueServiceImpl    testTarget         = CarryoverIssueServiceImplTest.createTarget(
            new StubGitHubIssueRepository(List.of()), new StubCarryoverDataRepository(Map.of()), testDefaultMinutes);

        /* テスト対象の実行 */
        final DefaultMinutes testResult = testTarget.loadDefaultMinutes(CarryoverIssueServiceImplTest.createSource());

        /* 検証の準備 */
        final Double actualMinutes    = testResult.find("音楽");
        final Path   actualConfigFile = testDefaultMinutes.getConfigFile();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "標準時間が一致しません");
        Assertions.assertEquals(expectedConfigFile, actualConfigFile, "標準時間の設定ファイルが一致しません");

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
        final Path          expectedDataDir       = Path.of("docs", "data");

        /* 準備 */
        final CarryoverItemDto            testItem   = new CarryoverItemDto("国語", "2026-06-18", false, 0.0, "unknown",
            "持ち越し", "- [ ] 国語2026/06/18");
        final CarryoverIssueDto           testDto    = new CarryoverIssueDto(371, "2026年10月06日のタスク", "2026-10-06",
            "open", "2026-10-06T14:23:08Z", List.of("持ち越し"), null, 1, 0.0, List.of(testItem));
        final StubCarryoverDataRepository testData   = new StubCarryoverDataRepository(Map.of(371, testDto));
        final CarryoverIssueServiceImpl   testTarget = CarryoverIssueServiceImplTest.createTarget(
            new StubGitHubIssueRepository(List.of()), testData, new StubDefaultMinutesRepository(Map.of()));

        /* テスト対象の実行 */
        final Map<Integer, CarryoverIssue> testResult
            = testTarget.loadIssues(CarryoverIssueServiceImplTest.createSource());

        /* 検証の準備 */
        final MinutesSource actualMinutesSource = testResult.get(371).getItems().get(0).getMinutesSource();
        final Path          actualDataDir       = testData.getDataDir();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "出力先のディレクトリが一致しません");

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
        final Path   expectedDataDir       = Path.of("docs", "data");

        /* 準備 */
        final StubCarryoverDataRepository testData   = new StubCarryoverDataRepository(Map.of());
        final CarryoverIssueServiceImpl   testTarget = CarryoverIssueServiceImplTest.createTarget(
            new StubGitHubIssueRepository(List.of()), testData, new StubDefaultMinutesRepository(Map.of()));
        final CarryoverItem               testItem   = new CarryoverItem("音楽", "2026-08-18", false, 11.5,
            MinutesSource.PARSED, "持ち越し", "- [ ] 音楽2026/08/18（残り時間：11.5分）");
        final CarryoverIssue              testIssue  = new CarryoverIssue(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), 1, List.of(testItem));

        /* テスト対象の実行 */
        testTarget.saveIssue(CarryoverIssueServiceImplTest.createSource(), testIssue);

        /* 検証の準備 */
        final String actualMinutesSource = testData.getSavedIssues().get(0).getItems().get(0).getMinutesSource();
        final Path   actualDataDir       = testData.getDataDir();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "出力先のディレクトリが一致しません");

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
        final int  expectedLatestIssue = 371;
        final Path expectedDataDir     = Path.of("docs", "data");

        /* 準備 */
        final StubCarryoverDataRepository testData    = new StubCarryoverDataRepository(Map.of());
        final CarryoverIssueServiceImpl   testTarget  = CarryoverIssueServiceImplTest.createTarget(
            new StubGitHubIssueRepository(List.of()), testData, new StubDefaultMinutesRepository(Map.of()));
        final CarryoverSummary            testSummary = new CarryoverSummary(371, List.of(), List.of());

        /* テスト対象の実行 */
        testTarget.saveSummary(CarryoverIssueServiceImplTest.createSource(), testSummary);

        /* 検証の準備 */
        final int  actualLatestIssue = testData.getSavedSummary().getLatestIssue();
        final Path actualDataDir     = testData.getDataDir();

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");
        Assertions.assertEquals(expectedDataDir, actualDataDir, "出力先のディレクトリが一致しません");

    }

}
