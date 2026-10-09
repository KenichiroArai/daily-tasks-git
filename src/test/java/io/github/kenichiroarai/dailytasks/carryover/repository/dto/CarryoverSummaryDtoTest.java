package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link CarryoverSummaryDto} のテスト<br>
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
public class CarryoverSummaryDtoTest {

    /**
     * テスト対象を作成する<br>
     *
     * @return テスト対象
     */
    private static CarryoverSummaryDto createTarget() {

        final ItemStatDto total = new ItemStatDto(1, 30, 0, 0);
        final DailySummaryDto day = new DailySummaryDto("2026-03-24", 175, null, total, Map.of("英語", total), Map.of());
        final CarryoverSummaryDto result = new CarryoverSummaryDto(374, List.of("英語"), List.of(day));
        return result;

    }

    /**
     * getLatestIssue メソッドのテスト - 正常系:最新の Issue 番号を返す場合
     */
    @Test
    public void testGetLatestIssue_normalValue() {

        /* 期待値の定義 */
        final int expectedLatestIssue = 374;

        /* 準備 */
        final CarryoverSummaryDto testTarget = CarryoverSummaryDtoTest.createTarget();

        /* テスト対象の実行 */
        final int testResult = testTarget.getLatestIssue();

        /* 検証の準備 */
        final int actualLatestIssue = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedLatestIssue, actualLatestIssue, "最新の Issue 番号が一致しません");

    }

    /**
     * getItems メソッドのテスト - 正常系:項目名を返す場合
     */
    @Test
    public void testGetItems_normalValue() {

        /* 期待値の定義 */
        final List<String> expectedItems = List.of("英語");

        /* 準備 */
        final CarryoverSummaryDto testTarget = CarryoverSummaryDtoTest.createTarget();

        /* テスト対象の実行 */
        final List<String> testResult = testTarget.getItems();

        /* 検証の準備 */
        final List<String> actualItems = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedItems, actualItems, "項目名が一致しません");

    }

    /**
     * getDays メソッドのテスト - 正常系:日別の集計を返す場合
     */
    @Test
    public void testGetDays_normalValue() {

        /* 期待値の定義 */
        final List<String> expectedDays = List.of("2026-03-24");

        /* 準備 */
        final CarryoverSummaryDto testTarget = CarryoverSummaryDtoTest.createTarget();

        /* テスト対象の実行 */
        final List<DailySummaryDto> testResult = testTarget.getDays();

        /* 検証の準備 */
        final List<String> actualDays = testResult.stream().map(DailySummaryDto::getDate).toList();

        /* 検証の実施 */
        Assertions.assertEquals(expectedDays, actualDays, "日別の集計が一致しません");

    }

}