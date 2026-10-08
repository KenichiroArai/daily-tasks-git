package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;

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
     * JSON での値
     */
    private final String value;

    /**
     * コンストラクタ<br>
     *
     * @param value
     *              JSON での値
     */
    MinutesSource(final String value) {

        this.value = value;

    }

    /**
     * JSON での値を返す<br>
     *
     * @return JSON での値
     */
    @JsonValue
    public String getValue() {

        final String result = this.value;
        return result;

    }

}
