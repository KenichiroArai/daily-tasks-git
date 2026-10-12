/**
 * 持ち越し機能の application 層のロジック<br>
 * <p>
 * application 層が domain 層に求めるロジック（データの取得・保存、Issue 本文の解析、日別の集計）のインタフェースを置く。実装は domain 層に置く。
 * ロジックはサービスを呼ばず、個々のメソッドがサービスなどから何度も呼ばれる。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.application.logic;
