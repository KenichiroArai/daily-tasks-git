/**
 * 持ち越し機能の共通ロジック・ドメインモデル層<br>
 * <p>
 * モデル、Issue 本文の解析、日別の集計、データの取得・保存サービスを置く。 参照してよいのは repository 層（と infrastructure 層）だけで、repository の DTO とモデルの変換もこの層で行う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.domain;
