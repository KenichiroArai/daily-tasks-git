/**
 * 持ち越し機能のデータアクセス層<br>
 * <p>
 * domain 層のリポジトリ（JSON ファイルの読み書き、標準時間の読み込み、GitHub の Issue の取得）を実装する。JSON ファイルの実装は impl パッケージ、JSON
 * の形は json パッケージ、DTO と JSON の形の変換は converter パッケージ、GitHub API へのアクセスは github パッケージに置く。参照してよいのは domain
 * 層（と infrastructure 層）だけで、どの層からも参照されない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.repository;
