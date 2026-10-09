/**
 * 持ち越し機能のデータアクセス層<br>
 * <p>
 * JSON ファイル（Issue ごとの解析結果、集計、標準時間）の読み書きのインタフェースを置く。実装は impl パッケージ、入出力の DTO は dto パッケージ、 GitHub API へのアクセスは github パッケージに置く。domain 層のモデルは参照しない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.repository;
