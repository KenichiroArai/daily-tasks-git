package io.github.kenichiroarai.dailytasks.carryover.repository.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 持ち越し項目の件数と残り時間の集計値の DTO<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@JsonPropertyOrder({
    "count", "minutes", "checkedCount", "checkedMinutes"
})
public class ItemStatDto {

    /**
     * 件数
     */
    private final int count;

    /**
     * 残り時間（分）
     */
    private final double minutes;

    /**
     * チェック済みの件数
     */
    private final int checkedCount;

    /**
     * チェック済みの残り時間（分）
     */
    private final double checkedMinutes;

    /**
     * コンストラクタ<br>
     *
     * @param count
     *                       件数
     * @param minutes
     *                       残り時間（分）
     * @param checkedCount
     *                       チェック済みの件数
     * @param checkedMinutes
     *                       チェック済みの残り時間（分）
     */
    public ItemStatDto(final int count, final double minutes, final int checkedCount, final double checkedMinutes) {

        this.count = count;
        this.minutes = minutes;
        this.checkedCount = checkedCount;
        this.checkedMinutes = checkedMinutes;

    }

    /**
     * チェック済みの件数を返す<br>
     *
     * @return チェック済みの件数
     */
    public int getCheckedCount() {

        final int result = this.checkedCount;
        return result;

    }

    /**
     * チェック済みの残り時間（分）を返す<br>
     *
     * @return チェック済みの残り時間（分）
     */
    public double getCheckedMinutes() {

        final double result = this.checkedMinutes;
        return result;

    }

    /**
     * 件数を返す<br>
     *
     * @return 件数
     */
    public int getCount() {

        final int result = this.count;
        return result;

    }

    /**
     * 残り時間（分）を返す<br>
     *
     * @return 残り時間（分）
     */
    public double getMinutes() {

        final double result = this.minutes;
        return result;

    }

}
