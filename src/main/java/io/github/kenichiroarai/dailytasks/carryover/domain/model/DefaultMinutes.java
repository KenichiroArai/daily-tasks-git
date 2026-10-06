package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.Map;

/**
 * 項目ごとの標準時間<br>
 * <p>
 * 時間表記のない持ち越し項目の残り時間を補完するために使う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class DefaultMinutes {

    /**
     * 項目名と標準時間（分）の対応
     */
    private final Map<String, Double> minutesByName;

    /**
     * コンストラクタ<br>
     *
     * @param minutesByName
     *                      項目名と標準時間（分）の対応
     */
    public DefaultMinutes(final Map<String, Double> minutesByName) {

        this.minutesByName = Map.copyOf(minutesByName);

    }

    /**
     * 項目の標準時間を返す<br>
     *
     * @param name
     *             項目名
     *
     * @return 標準時間（分）。未登録の場合は null
     */
    public Double find(final String name) {

        final Double result = this.minutesByName.get(name);
        return result;

    }

}
