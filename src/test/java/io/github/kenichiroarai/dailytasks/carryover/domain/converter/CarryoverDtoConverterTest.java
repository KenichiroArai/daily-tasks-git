package io.github.kenichiroarai.dailytasks.carryover.domain.converter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailySummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.ItemStatDto;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;

/**
 * {@link CarryoverDtoConverter} のテスト<br>
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
public class CarryoverDtoConverterTest {

    /**
     * 持ち越しの解析結果を作成する<br>
     *
     * @return 持ち越しの解析結果
     */
    private static CarryoverIssue createIssue() {

        final CarryoverItem  item   = new CarryoverItem("音楽", "2026-08-18", true, 11.5, MinutesSource.PARSED, "持ち越し",
            "- [x] 音楽2026/08/18（残り時間：11.5分）");
        final CarryoverIssue result = new CarryoverIssue(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), 1, List.of(item));
        return result;

    }

    /**
     * 保存済みの解析結果を作成する<br>
     *
     * @param minutesSource
     *                      残り時間の取得元の値
     *
     * @return 保存済みの解析結果
     */
    private static CarryoverIssueDto createIssueDto(final String minutesSource) {

        final CarryoverItemDto  item   = new CarryoverItemDto("国語", "2026-06-18", false, 15.0, minutesSource, "持ち越し",
            "- [ ] 国語2026/06/18");
        final CarryoverIssueDto result = new CarryoverIssueDto(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), 1, 1, 15.0, List.of(item));
        return result;

    }

    /**
     * 画面用の集計を作成する<br>
     *
     * @return 画面用の集計
     */
    private static CarryoverSummary createSummary() {

        final DailySummary day = new DailySummary("2026-10-06", 371, 1);
        day.add(CarryoverDtoConverterTest.createIssue().getItems().get(0));
        final CarryoverSummary result = new CarryoverSummary(371, List.of("音楽"), List.of(day));
        return result;

    }

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverDtoConverter createTarget() {

        final CarryoverDtoConverter result = new CarryoverDtoConverter(MessageProviderTestUtil.create());
        return result;

    }

    /**
     * toCarryoverIssue メソッドのテスト - 正常系:Issue の項目を引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssue_normalIssue() {

        /* 期待値の定義 */
        final String expectedUpdatedAt = "2026-10-06T14:23:08Z";

        /* 準備 */
        final CarryoverIssueDto testDto = CarryoverDtoConverterTest.createIssueDto("default");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = CarryoverDtoConverterTest.createTarget().toCarryoverIssue(testDto);

        /* 検証の準備 */
        final String actualUpdatedAt = testResult.getUpdatedAt();

        /* 検証の実施 */
        Assertions.assertEquals(expectedUpdatedAt, actualUpdatedAt, "更新日時が一致しません");

    }

    /**
     * toCarryoverIssue メソッドのテスト - 正常系:残り時間の取得元を値から変換する場合
     */
    @Test
    public void testToCarryoverIssue_normalMinutesSource() {

        /* 期待値の定義 */
        final MinutesSource expectedMinutesSource = MinutesSource.DEFAULT;

        /* 準備 */
        final CarryoverIssueDto testDto = CarryoverDtoConverterTest.createIssueDto("default");

        /* テスト対象の実行 */
        final CarryoverIssue testResult = CarryoverDtoConverterTest.createTarget().toCarryoverIssue(testDto);

        /* 検証の準備 */
        final MinutesSource actualMinutesSource = testResult.getItems().get(0).getMinutesSource();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * toCarryoverIssue メソッドのテスト - 準正常系:残り時間の取得元が不明な場合
     */
    @Test
    public void testToCarryoverIssue_semiUnknownMinutesSource() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な残り時間の取得元です: xxx";

        /* 準備 */
        final CarryoverIssueDto testDto = CarryoverDtoConverterTest.createIssueDto("xxx");

        /* テスト対象の実行 */
        final IllegalArgumentException testException = Assertions.assertThrows(IllegalArgumentException.class,
            () -> CarryoverDtoConverterTest.createTarget().toCarryoverIssue(testDto));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

    /**
     * toCarryoverIssueDto メソッドのテスト - 正常系:件数を集計して引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssueDto_normalCount() {

        /* 期待値の定義 */
        final int expectedCount = 1;

        /* 準備 */
        final CarryoverIssue testIssue = CarryoverDtoConverterTest.createIssue();

        /* テスト対象の実行 */
        final CarryoverIssueDto testResult = CarryoverDtoConverterTest.createTarget().toCarryoverIssueDto(testIssue);

        /* 検証の準備 */
        final int actualCount = testResult.getCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");

    }

    /**
     * toCarryoverIssueDto メソッドのテスト - 正常系:残り時間の取得元を保存するときの値に変換する場合
     */
    @Test
    public void testToCarryoverIssueDto_normalMinutesSource() {

        /* 期待値の定義 */
        final String expectedMinutesSource = "parsed";

        /* 準備 */
        final CarryoverIssue testIssue = CarryoverDtoConverterTest.createIssue();

        /* テスト対象の実行 */
        final CarryoverIssueDto testResult = CarryoverDtoConverterTest.createTarget().toCarryoverIssueDto(testIssue);

        /* 検証の準備 */
        final String actualMinutesSource = testResult.getItems().get(0).getMinutesSource();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * toCarryoverSummaryDto メソッドのテスト - 正常系:項目ごとの集計を変換する場合
     */
    @Test
    public void testToCarryoverSummaryDto_normalByItem() {

        /* 期待値の定義 */
        final int expectedCheckedCount = 1;

        /* 準備 */
        final CarryoverSummary testSummary = CarryoverDtoConverterTest.createSummary();

        /* テスト対象の実行 */
        final CarryoverSummaryDto testResult
            = CarryoverDtoConverterTest.createTarget().toCarryoverSummaryDto(testSummary);

        /* 検証の準備 */
        final DailySummaryDto actualDay          = testResult.getDays().get(0);
        final int             actualCheckedCount = actualDay.getByItem().get("音楽").getCheckedCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedCount, actualCheckedCount, "項目ごとのチェック済み件数が一致しません");

    }

    /**
     * toCarryoverSummaryDto メソッドのテスト - 正常系:持ち越し元の月ごとの集計を変換する場合
     */
    @Test
    public void testToCarryoverSummaryDto_normalByOriginMonth() {

        /* 期待値の定義 */
        final int expectedCount = 1;

        /* 準備 */
        final CarryoverSummary testSummary = CarryoverDtoConverterTest.createSummary();

        /* テスト対象の実行 */
        final CarryoverSummaryDto testResult
            = CarryoverDtoConverterTest.createTarget().toCarryoverSummaryDto(testSummary);

        /* 検証の準備 */
        final DailySummaryDto actualDay   = testResult.getDays().get(0);
        final int             actualCount = actualDay.getByOriginMonth().get("2026-08").getCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "持ち越し元の月ごとの件数が一致しません");

    }

    /**
     * toCarryoverSummaryDto メソッドのテスト - 正常系:最新の Issue 番号を引き継ぐ場合
     */
    @Test
    public void testToCarryoverSummaryDto_normalLatestIssue() {

        /* 期待値の定義 */
        final int expectedLatestIssue = 371;

        /* 準備 */
        final CarryoverSummary testSummary = CarryoverDtoConverterTest.createSummary();

        /* テスト対象の実行 */
        final CarryoverSummaryDto testResult
            = CarryoverDtoConverterTest.createTarget().toCarryoverSummaryDto(testSummary);

        /* 検証の準備 */
        final int actualLatestIssue = testResult.getLatestIssue();

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * toCarryoverSummaryDto メソッドのテスト - 正常系:日別の全体の集計を変換する場合
     */
    @Test
    public void testToCarryoverSummaryDto_normalTotal() {

        /* 期待値の定義 */
        final double expectedMinutes = 11.5;

        /* 準備 */
        final CarryoverSummary testSummary = CarryoverDtoConverterTest.createSummary();

        /* テスト対象の実行 */
        final CarryoverSummaryDto testResult
            = CarryoverDtoConverterTest.createTarget().toCarryoverSummaryDto(testSummary);

        /* 検証の準備 */
        final ItemStatDto actualTotal   = testResult.getDays().get(0).getTotal();
        final double      actualMinutes = actualTotal.getMinutes();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "全体の残り時間が一致しません");

    }

    /**
     * toDailyTaskIssue メソッドのテスト - 正常系:本文を引き継ぐ場合
     */
    @Test
    public void testToDailyTaskIssue_normalBody() {

        /* 期待値の定義 */
        final String expectedBody = "本文";

        /* 準備 */
        final GitHubIssueDto testDto = new GitHubIssueDto(371, "2026年10月06日のタスク", "open", "2026-10-06T14:23:08Z", "本文");

        /* テスト対象の実行 */
        final DailyTaskIssue testResult = CarryoverDtoConverterTest.createTarget().toDailyTaskIssue(testDto);

        /* 検証の準備 */
        final String actualBody = testResult.getBody();

        /* 検証の実施 */
        Assertions.assertEquals(expectedBody, actualBody, "本文が一致しません");

    }

    /**
     * toDailyTaskIssue メソッドのテスト - 正常系:Issue 番号を引き継ぐ場合
     */
    @Test
    public void testToDailyTaskIssue_normalNumber() {

        /* 期待値の定義 */
        final int expectedNumber = 371;

        /* 準備 */
        final GitHubIssueDto testDto = new GitHubIssueDto(371, "2026年10月06日のタスク", "open", "2026-10-06T14:23:08Z", "本文");

        /* テスト対象の実行 */
        final DailyTaskIssue testResult = CarryoverDtoConverterTest.createTarget().toDailyTaskIssue(testDto);

        /* 検証の準備 */
        final int actualNumber = testResult.getNumber();

        /* 検証の実施 */
        Assertions.assertEquals(expectedNumber, actualNumber, "Issue 番号が一致しません");

    }

    /**
     * toDefaultMinutes メソッドのテスト - 正常系:登録済みの標準時間を返す場合
     */
    @Test
    public void testToDefaultMinutes_normalFind() {

        /* 期待値の定義 */
        final Double expectedMinutes = 15.0;

        /* 準備 */
        final Map<String, Double> testMinutesByName = Map.of("音楽", 15.0);

        /* テスト対象の実行 */
        final DefaultMinutes testResult = CarryoverDtoConverterTest.createTarget().toDefaultMinutes(testMinutesByName);

        /* 検証の準備 */
        final Double actualMinutes = testResult.find("音楽");

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "標準時間が一致しません");

    }

    /**
     * toGitHubSettingsDto メソッドのテスト - 正常系:リポジトリ名を引き継ぐ場合
     */
    @Test
    public void testToGitHubSettingsDto_normalRepository() {

        /* 期待値の定義 */
        final String expectedRepository = "owner/repo";

        /* 準備 */
        final CarryoverSource testSource = new CarryoverSource("owner/repo", "test-token", Path.of("docs", "data"),
            Path.of("config", "default-minutes.json"));

        /* テスト対象の実行 */
        final GitHubSettingsDto testResult = CarryoverDtoConverterTest.createTarget().toGitHubSettingsDto(testSource);

        /* 検証の準備 */
        final String actualRepository = testResult.getRepository();

        /* 検証の実施 */
        Assertions.assertEquals(expectedRepository, actualRepository, "リポジトリ名が一致しません");

    }

    /**
     * toGitHubSettingsDto メソッドのテスト - 正常系:トークンを引き継ぐ場合
     */
    @Test
    public void testToGitHubSettingsDto_normalToken() {

        /* 期待値の定義 */
        final String expectedToken = "test-token";

        /* 準備 */
        final CarryoverSource testSource = new CarryoverSource("owner/repo", "test-token", Path.of("docs", "data"),
            Path.of("config", "default-minutes.json"));

        /* テスト対象の実行 */
        final GitHubSettingsDto testResult = CarryoverDtoConverterTest.createTarget().toGitHubSettingsDto(testSource);

        /* 検証の準備 */
        final String actualToken = testResult.getToken();

        /* 検証の実施 */
        Assertions.assertEquals(expectedToken, actualToken, "トークンが一致しません");

    }

}
