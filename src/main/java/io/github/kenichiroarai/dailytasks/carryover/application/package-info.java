/**
 * 持ち越し機能のユースケース・業務ロジック層<br>
 * <p>
 * presentation 層のサービスを実装し、収集・解析・集計の流れを組み立てる。domain 層に求めるロジック（データの取得・保存、解析、集計）を logic
 * パッケージ、業務モデルを model パッケージに置き、domain 層がそれを実装する。参照してよいのは presentation 層（と infrastructure 層）だけである。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.application;
