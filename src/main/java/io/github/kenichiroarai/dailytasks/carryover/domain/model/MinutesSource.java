package io.github.kenichiroarai.dailytasks.carryover.domain.model;

/**
 * 残り時間の取得元<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public enum MinutesSource {

    /**
     * Issue 本文の時間表記から取得
     */
    PARSED("parsed"),

    /**
     * 項目ごとの標準時間で補完
     */
    DEFAULT("default"),

    /**
     * 標準時間が未登録のため 0 分で補完
     */
    UNKNOWN("unknown"),

    ;

    /**
     * 保存するときの値
     */
    private final String value;

    /**
     * コンストラクタ<br>
     *
     * @param value
     *              保存するときの値
     */
    private MinutesSource(final String value) {

        this.value = value;

    }

    /**
     * 保存するときの値から残り時間の取得元を返す<br>
     *
     * @param value
     *              保存するときの値（parsed / default / unknown）
     *
     * @return 残り時間の取得元
     *
     * @throws IllegalArgumentException
     *                                  該当する取得元がない場合
     */
    public static MinutesSource fromValue(final String value) {

        MinutesSource result = null;

        for (final MinutesSource source : MinutesSource.values()) {

            if (!source.value.equals(value)) {

                continue;

            }

            result = source;
            return result;

        }

        throw new IllegalArgumentException(String.format("不明な残り時間の取得元です: %s", value));

    }

    /**
     * 保存するときの値を返す<br>
     *
     * @return 保存するときの値
     */
    public String getValue() {

        final String result = this.value;
        return result;

    }

}
