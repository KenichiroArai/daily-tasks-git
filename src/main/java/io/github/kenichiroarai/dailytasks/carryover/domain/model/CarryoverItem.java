package io.github.kenichiroarai.dailytasks.carryover.domain.model;

/**
 * 持ち越し項目<br>
 * <p>
 * Issue 本文の「持ち越し」（旧「繰り越し」「負債」）セクションにある 1 行を表す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class CarryoverItem {

    /**
     * 項目名（正規化済み）
     */
    private final String name;

    /**
     * 持ち越し元の日付（yyyy-MM-dd）。日付がない行は null
     */
    private final String originDate;

    /**
     * チェック済みか
     */
    private final boolean checked;

    /**
     * 残り時間（分）
     */
    private final double minutes;

    /**
     * 残り時間の取得元
     */
    private final MinutesSource minutesSource;

    /**
     * セクション名
     */
    private final String section;

    /**
     * 元の行
     */
    private final String raw;

    /**
     * コンストラクタ<br>
     *
     * @param name
     *                      項目名（正規化済み）
     * @param originDate
     *                      持ち越し元の日付（yyyy-MM-dd）。日付がない行は null
     * @param checked
     *                      チェック済みか
     * @param minutes
     *                      残り時間（分）
     * @param minutesSource
     *                      残り時間の取得元
     * @param section
     *                      セクション名
     * @param raw
     *                      元の行
     */
    public CarryoverItem(final String name, final String originDate, final boolean checked, final double minutes,
        final MinutesSource minutesSource, final String section, final String raw) {

        this.name = name;
        this.originDate = originDate;
        this.checked = checked;
        this.minutes = minutes;
        this.minutesSource = minutesSource;
        this.section = section;
        this.raw = raw;

    }

    /**
     * 項目名（正規化済み）を返す<br>
     *
     * @return 項目名
     */
    public String getName() {

        final String result = this.name;
        return result;

    }

    /**
     * 持ち越し元の日付を返す<br>
     *
     * @return 持ち越し元の日付（yyyy-MM-dd）。日付がない行は null
     */
    public String getOriginDate() {

        final String result = this.originDate;
        return result;

    }

    /**
     * チェック済みかを返す<br>
     *
     * @return true：チェック済み、false：未チェック
     */
    public boolean isChecked() {

        final boolean result = this.checked;
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

    /**
     * 残り時間の取得元を返す<br>
     *
     * @return 残り時間の取得元
     */
    public MinutesSource getMinutesSource() {

        final MinutesSource result = this.minutesSource;
        return result;

    }

    /**
     * セクション名を返す<br>
     *
     * @return セクション名
     */
    public String getSection() {

        final String result = this.section;
        return result;

    }

    /**
     * 元の行を返す<br>
     *
     * @return 元の行
     */
    public String getRaw() {

        final String result = this.raw;
        return result;

    }

}
