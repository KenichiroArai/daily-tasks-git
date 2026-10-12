/**
 * 持ち越し機能の共通ロジック層<br>
 * <p>
 * application 層のロジック（データの取得・保存、Issue 本文の解析、日別の集計）を logic.impl パッケージで実装する。repository 層に求める外部 API と DAO を
 * repository パッケージ（api・dao）、受け渡しの DTO を dto パッケージに置き、repository 層がそれを実装する。参照してよいのは application 層（と infrastructure
 * 層）だけで、application 層のモデルと自層の DTO の変換も logic パッケージで行う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.domain;
