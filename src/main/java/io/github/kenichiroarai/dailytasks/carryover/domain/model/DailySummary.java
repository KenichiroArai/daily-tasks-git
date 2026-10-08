package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 日別の持ち越しの集計<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@JsonPropertyOrder({
    "date", "issue", "declaredCount", "total", "byItem", "byOriginMonth"
})
public class DailySummary {

    /**
     * その日の日付（yyyy-MM-dd）
     */
    private final String date;

    /**
     * Issue 番号
     */
    private final int issue;

    /**
     * 本文の「残：N」の値。記載がない場合は null
     */
    private final Integer declaredCount;

    /**
     * 全体の集計
     */
    private final ItemStat total;

    /**
     * 項目ごとの集計
     */
    private final Map<String, ItemStat> byItem;

    /**
     * 持ち越し元の月（yyyy-MM）ごとの集計
     */
    private final Map<String, ItemStat> byOriginMonth;

    /**
     * コンストラクタ<br>
     *
     * @param date
     *                      その日の日付（yyyy-MM-dd）
     * @param issue
     *                      Issue 番号
     * @param declaredCount
     *                      本文の「残：N」の値。記載がない場合は null
     */
    public DailySummary(final String date, final int issue, final Integer declaredCount) {

        this.date = date;
        this.issue = issue;
        this.declaredCount = declaredCount;
        this.total = new ItemStat();
        this.byItem = new TreeMap<>();
        this.byOriginMonth = new TreeMap<>();

    }

    /**
     * 持ち越し項目を集計に加える<br>
     *
     * @param item
     *             持ち越し項目
     */
    public void add(final CarryoverItem item) {

        /* 全体と項目ごとの集計 */
        this.total.add(item);
        this.byItem.computeIfAbsent(item.getName(), _ -> new ItemStat()).add(item);

        // 持ち越し元の日付がない行は月ごとの集計に含めない
        if (item.getOriginDate() == null) {

            return;

        }

        /* 持ち越し元の月ごとの集計 */
        final String month = item.getOriginDate().substring(0, 7);
        this.byOriginMonth.computeIfAbsent(month, _ -> new ItemStat()).add(item);

    }

    /**
     * その日の日付を返す<br>
     *
     * @return その日の日付（yyyy-MM-dd）
     */
    public String getDate() {

        final String result = this.date;
        return result;

    }

    /**
     * Issue 番号を返す<br>
     *
     * @return Issue 番号
     */
    public int getIssue() {

        final int result = this.issue;
        return result;

    }

    /**
     * 本文の「残：N」の値を返す<br>
     *
     * @return 「残：N」の値。記載がない場合は null
     */
    public Integer getDeclaredCount() {

        final Integer result = this.declaredCount;
        return result;

    }

    /**
     * 全体の集計を返す<br>
     *
     * @return 全体の集計
     */
    public ItemStat getTotal() {

        final ItemStat result = this.total;
        return result;

    }

    /**
     * 項目ごとの集計を返す<br>
     *
     * @return 項目ごとの集計（キーは項目名）
     */
    public Map<String, ItemStat> getByItem() {

        final Map<String, ItemStat> result = this.byItem;
        return result;

    }

    /**
     * 持ち越し元の月ごとの集計を返す<br>
     *
     * @return 持ち越し元の月ごとの集計（キーは yyyy-MM）
     */
    public Map<String, ItemStat> getByOriginMonth() {

        final Map<String, ItemStat> result = this.byOriginMonth;
        return result;

    }

}
