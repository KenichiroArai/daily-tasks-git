package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 画面用の持ち越しの集計の DTO<br>
 * <p>
 * 画面用の集計の JSON（docs/data/summary.json）の形を表す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@JsonPropertyOrder({
    "latestIssue", "items", "days"
})
public class CarryoverSummaryDto {

    /**
     * 集計に含めた最新の Issue 番号。Issue がない場合は 0
     */
    private final int latestIssue;

    /**
     * 項目名（延べ件数の多い順）
     */
    private final List<String> items;

    /**
     * 日別の集計（日付順）
     */
    private final List<DailySummaryDto> days;

    /**
     * コンストラクタ<br>
     *
     * @param latestIssue
     *                    集計に含めた最新の Issue 番号。Issue がない場合は 0
     * @param items
     *                    項目名（延べ件数の多い順）
     * @param days
     *                    日別の集計（日付順）
     */
    public CarryoverSummaryDto(final int latestIssue, final List<String> items, final List<DailySummaryDto> days) {

        this.latestIssue = latestIssue;
        this.items = List.copyOf(items);
        this.days = List.copyOf(days);

    }

    /**
     * 集計に含めた最新の Issue 番号を返す<br>
     *
     * @return 最新の Issue 番号。Issue がない場合は 0
     */
    public int getLatestIssue() {

        final int result = this.latestIssue;
        return result;

    }

    /**
     * 項目名を返す<br>
     *
     * @return 項目名（延べ件数の多い順）
     */
    public List<String> getItems() {

        final List<String> result = this.items;
        return result;

    }

    /**
     * 日別の集計を返す<br>
     *
     * @return 日別の集計（日付順）
     */
    public List<DailySummaryDto> getDays() {

        final List<DailySummaryDto> result = this.days;
        return result;

    }

}
