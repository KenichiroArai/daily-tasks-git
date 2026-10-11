/**
 * 持ち越し機能のパッケージ<br>
 * <p>
 * GitHub Issue から持ち越しを収集・解析・集計し、JSON に出力する。 presentation、application、domain、infrastructure、repository の 5 層で構成する。処理の流れは
 * presentation → application → domain → repository の順で、依存はその逆（依存性逆転の原則）とする。呼ぶ側の層がインタフェースと受け渡しの型を自層の種類ごとの
 * パッケージ（service、model など）に置き、呼ばれる側の層がそれを impl パッケージで実装する。参照はすぐ上の層だけとする。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover;
