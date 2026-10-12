/**
 * 持ち越し機能の JSON ファイルの形<br>
 * <p>
 * Issue ごとの JSON（docs/data/issues/NNNN.json）と画面用の集計の JSON（docs/data/summary.json）の形を表す。JSON の形（Jackson
 * のアノテーション）はこのパッケージだけが持ち、domain 層には漏らさない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.repository.dto.json;
