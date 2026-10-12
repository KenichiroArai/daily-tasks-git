/**
 * 持ち越し機能のデータアクセス層<br>
 * <p>
 * domain 層の外部 API と DAO を実装する。外部 API（GitHub の Issue の取得）は api パッケージに提供元ごと、ファイルや DB へのアクセス（JSON
 * ファイルの読み書き、標準時間の読み込み）は dao パッケージに用途ごとに置く。この層の内部の型は dto パッケージ（JSON の形は dto.json パッケージ）、DTO と
 * JSON の形の変換は logic パッケージに置く。参照してよいのは domain 層（と infrastructure 層）だけで、どの層からも参照されない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.repository;
