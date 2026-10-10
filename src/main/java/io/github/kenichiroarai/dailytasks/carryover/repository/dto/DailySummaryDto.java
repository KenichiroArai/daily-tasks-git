package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 日別の持ち越しの集計の DTO<br>
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
public class DailySummaryDto {

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
    private final ItemStatDto total;

    /**
     * 項目ごとの集計（キーは項目名）
     */
    private final Map<String, ItemStatDto> byItem;

    /**
     * 持ち越し元の月ごとの集計（キーは yyyy-MM）
     */
    private final Map<String, ItemStatDto> byOriginMonth;

    /**
     * コンストラクタ<br>
     * <p>
     * 集計の並び順は引数の順を保つ。
     * </p>
     *
     * @param date
     *                      その日の日付（yyyy-MM-dd）
     * @param issue
     *                      Issue 番号
     * @param declaredCount
     *                      本文の「残：N」の値。記載がない場合は null
     * @param total
     *                      全体の集計
     * @param byItem
     *                      項目ごとの集計（キーは項目名）
     * @param byOriginMonth
     *                      持ち越し元の月ごとの集計（キーは yyyy-MM）
     */
    public DailySummaryDto(final String date, final int issue, final Integer declaredCount, final ItemStatDto total,
        final Map<String, ItemStatDto> byItem, final Map<String, ItemStatDto> byOriginMonth) {

        this.date = date;
        this.issue = issue;
        this.declaredCount = declaredCount;
        this.total = total;
        this.byItem = Collections.unmodifiableMap(new LinkedHashMap<>(byItem));
        this.byOriginMonth = Collections.unmodifiableMap(new LinkedHashMap<>(byOriginMonth));

    }

    /**
     * 項目ごとの集計を返す<br>
     *
     * @return 項目ごとの集計（キーは項目名）
     */
    public Map<String, ItemStatDto> getByItem() {

        final Map<String, ItemStatDto> result = this.byItem;
        return result;

    }

    /**
     * 持ち越し元の月ごとの集計を返す<br>
     *
     * @return 持ち越し元の月ごとの集計（キーは yyyy-MM）
     */
    public Map<String, ItemStatDto> getByOriginMonth() {

        final Map<String, ItemStatDto> result = this.byOriginMonth;
        return result;

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
     * 本文の「残：N」の値を返す<br>
     *
     * @return 「残：N」の値。記載がない場合は null
     */
    public Integer getDeclaredCount() {

        final Integer result = this.declaredCount;
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
     * 全体の集計を返す<br>
     *
     * @return 全体の集計
     */
    public ItemStatDto getTotal() {

        final ItemStatDto result = this.total;
        return result;

    }

}
