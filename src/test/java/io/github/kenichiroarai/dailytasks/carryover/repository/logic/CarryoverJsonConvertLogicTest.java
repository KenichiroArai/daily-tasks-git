package io.github.kenichiroarai.dailytasks.carryover.repository.logic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.ItemStatDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverIssueJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverItemJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverSummaryJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.DailySummaryJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.ItemStatJsonDto;

/**
 * {@link CarryoverJsonConvertLogic} のテスト<br>
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
public class CarryoverJsonConvertLogicTest {

    /**
     * 解析結果の DTO を作成する<br>
     *
     * @return 解析結果の DTO
     */
    private static CarryoverIssueDto createIssueDto() {

        final CarryoverItemDto  item   = new CarryoverItemDto("音楽", "2026-08-18", true, 11.5, "parsed", "持ち越し",
            "- [x] 音楽2026/08/18（残り時間：11.5分）");
        final CarryoverIssueDto result = new CarryoverIssueDto(371, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), 1, 1, 11.5, List.of(item));
        return result;

    }

    /**
     * JSON の形の解析結果を作成する<br>
     *
     * @return JSON の形の解析結果
     */
    private static CarryoverIssueJsonDto createIssueJson() {

        final CarryoverItemJsonDto  item   = new CarryoverItemJsonDto("国語", "2026-06-18", false, 15.0, "default", "持ち越し",
            "- [ ] 国語2026/06/18");
        final CarryoverIssueJsonDto result = new CarryoverIssueJsonDto(370, "2026年10月05日のタスク", "2026-10-05", "closed",
            "2026-10-05T10:00:00Z", List.of("持ち越し"), 1, 1, 15.0, List.of(item));
        return result;

    }

    /**
     * 画面用の集計の DTO を作成する<br>
     *
     * @return 画面用の集計の DTO
     */
    private static CarryoverSummaryDto createSummaryDto() {

        final ItemStatDto              total         = new ItemStatDto(2, 26.5, 1, 11.5);
        final Map<String, ItemStatDto> byItem        = new LinkedHashMap<>();
        final Map<String, ItemStatDto> byOriginMonth = new LinkedHashMap<>();
        byItem.put("音楽", new ItemStatDto(1, 11.5, 1, 11.5));
        byItem.put("国語", new ItemStatDto(1, 15.0, 0, 0.0));
        byOriginMonth.put("2026-08", new ItemStatDto(1, 11.5, 1, 11.5));
        final DailySummaryDto     day    = new DailySummaryDto("2026-10-06", 371, 2, total, byItem, byOriginMonth);
        final CarryoverSummaryDto result = new CarryoverSummaryDto(371, List.of("音楽", "国語"), List.of(day));
        return result;

    }

    /**
     * toCarryoverIssueDto メソッドのテスト - 正常系:Issue の項目を引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssueDto_normalIssue() {

        /* 期待値の定義 */
        final String expectedUpdatedAt = "2026-10-05T10:00:00Z";

        /* 準備 */
        final CarryoverIssueJsonDto testJson = CarryoverJsonConvertLogicTest.createIssueJson();

        /* テスト対象の実行 */
        final CarryoverIssueDto testResult = new CarryoverJsonConvertLogic().toCarryoverIssueDto(testJson);

        /* 検証の準備 */
        final String actualUpdatedAt = testResult.getUpdatedAt();

        /* 検証の実施 */
        Assertions.assertEquals(expectedUpdatedAt, actualUpdatedAt, "更新日時が一致しません");

    }

    /**
     * toCarryoverIssueDto メソッドのテスト - 正常系:持ち越し項目を引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssueDto_normalItem() {

        /* 期待値の定義 */
        final String expectedMinutesSource = "default";

        /* 準備 */
        final CarryoverIssueJsonDto testJson = CarryoverJsonConvertLogicTest.createIssueJson();

        /* テスト対象の実行 */
        final CarryoverIssueDto testResult = new CarryoverJsonConvertLogic().toCarryoverIssueDto(testJson);

        /* 検証の準備 */
        final String actualMinutesSource = testResult.getItems().get(0).getMinutesSource();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");

    }

    /**
     * toCarryoverIssueJson メソッドのテスト - 正常系:件数を引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssueJson_normalCount() {

        /* 期待値の定義 */
        final int expectedCount = 1;

        /* 準備 */
        final CarryoverIssueDto testDto = CarryoverJsonConvertLogicTest.createIssueDto();

        /* テスト対象の実行 */
        final CarryoverIssueJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverIssueJson(testDto);

        /* 検証の準備 */
        final int actualCount = testResult.getCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCount, actualCount, "件数が一致しません");

    }

    /**
     * toCarryoverIssueJson メソッドのテスト - 正常系:持ち越し項目を引き継ぐ場合
     */
    @Test
    public void testToCarryoverIssueJson_normalItem() {

        /* 期待値の定義 */
        final String expectedRaw = "- [x] 音楽2026/08/18（残り時間：11.5分）";

        /* 準備 */
        final CarryoverIssueDto testDto = CarryoverJsonConvertLogicTest.createIssueDto();

        /* テスト対象の実行 */
        final CarryoverIssueJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverIssueJson(testDto);

        /* 検証の準備 */
        final String actualRaw = testResult.getItems().get(0).getRaw();

        /* 検証の実施 */
        Assertions.assertEquals(expectedRaw, actualRaw, "元の行が一致しません");

    }

    /**
     * toCarryoverSummaryJson メソッドのテスト - 正常系:項目ごとの集計を並び順のまま変換する場合
     */
    @Test
    public void testToCarryoverSummaryJson_normalByItem() {

        /* 期待値の定義 */
        final List<String> expectedKeys = List.of("音楽", "国語");

        /* 準備 */
        final CarryoverSummaryDto testDto = CarryoverJsonConvertLogicTest.createSummaryDto();

        /* テスト対象の実行 */
        final CarryoverSummaryJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverSummaryJson(testDto);

        /* 検証の準備 */
        final List<String> actualKeys = List.copyOf(testResult.getDays().get(0).getByItem().keySet());

        /* 検証の実施 */
        Assertions.assertEquals(expectedKeys, actualKeys, "項目ごとの集計の並び順が一致しません");

    }

    /**
     * toCarryoverSummaryJson メソッドのテスト - 正常系:持ち越し元の月ごとの集計を変換する場合
     */
    @Test
    public void testToCarryoverSummaryJson_normalByOriginMonth() {

        /* 期待値の定義 */
        final double expectedCheckedMinutes = 11.5;

        /* 準備 */
        final CarryoverSummaryDto testDto = CarryoverJsonConvertLogicTest.createSummaryDto();

        /* テスト対象の実行 */
        final CarryoverSummaryJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverSummaryJson(testDto);

        /* 検証の準備 */
        final ItemStatJsonDto actualStat           = testResult.getDays().get(0).getByOriginMonth().get("2026-08");
        final double          actualCheckedMinutes = actualStat.getCheckedMinutes();

        /* 検証の実施 */
        Assertions.assertEquals(expectedCheckedMinutes, actualCheckedMinutes, "チェック済みの残り時間が一致しません");

    }

    /**
     * toCarryoverSummaryJson メソッドのテスト - 正常系:日別の集計の項目を引き継ぐ場合
     */
    @Test
    public void testToCarryoverSummaryJson_normalDay() {

        /* 期待値の定義 */
        final Integer expectedDeclaredCount = 2;

        /* 準備 */
        final CarryoverSummaryDto testDto = CarryoverJsonConvertLogicTest.createSummaryDto();

        /* テスト対象の実行 */
        final CarryoverSummaryJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverSummaryJson(testDto);

        /* 検証の準備 */
        final DailySummaryJsonDto actualDay           = testResult.getDays().get(0);
        final Integer             actualDeclaredCount = actualDay.getDeclaredCount();

        /* 検証の実施 */
        Assertions.assertEquals(expectedDeclaredCount, actualDeclaredCount, "残数が一致しません");

    }

    /**
     * toCarryoverSummaryJson メソッドのテスト - 正常系:最新の Issue 番号を引き継ぐ場合
     */
    @Test
    public void testToCarryoverSummaryJson_normalLatestIssue() {

        /* 期待値の定義 */
        final int expectedLatestIssue = 371;

        /* 準備 */
        final CarryoverSummaryDto testDto = CarryoverJsonConvertLogicTest.createSummaryDto();

        /* テスト対象の実行 */
        final CarryoverSummaryJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverSummaryJson(testDto);

        /* 検証の準備 */
        final int actualLatestIssue = testResult.getLatestIssue();

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * toCarryoverSummaryJson メソッドのテスト - 正常系:日別の全体の集計を変換する場合
     */
    @Test
    public void testToCarryoverSummaryJson_normalTotal() {

        /* 期待値の定義 */
        final double expectedMinutes = 26.5;

        /* 準備 */
        final CarryoverSummaryDto testDto = CarryoverJsonConvertLogicTest.createSummaryDto();

        /* テスト対象の実行 */
        final CarryoverSummaryJsonDto testResult = new CarryoverJsonConvertLogic().toCarryoverSummaryJson(testDto);

        /* 検証の準備 */
        final double actualMinutes = testResult.getDays().get(0).getTotal().getMinutes();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMinutes, actualMinutes, "全体の残り時間が一致しません");

    }

}
