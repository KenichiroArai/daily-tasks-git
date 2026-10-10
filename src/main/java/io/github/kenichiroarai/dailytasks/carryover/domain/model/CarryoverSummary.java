package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.List;

/**
 * 画面用の持ち越しの集計<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class CarryoverSummary {

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
    private final List<DailySummary> days;

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
    public CarryoverSummary(final int latestIssue, final List<String> items, final List<DailySummary> days) {

        this.latestIssue = latestIssue;
        this.items = List.copyOf(items);
        this.days = List.copyOf(days);

    }

    /**
     * 日別の集計を返す<br>
     *
     * @return 日別の集計（日付順）
     */
    public List<DailySummary> getDays() {

        final List<DailySummary> result = this.days;
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
     * 集計に含めた最新の Issue 番号を返す<br>
     *
     * @return 最新の Issue 番号。Issue がない場合は 0
     */
    public int getLatestIssue() {

        final int result = this.latestIssue;
        return result;

    }

}
