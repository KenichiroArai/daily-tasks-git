/**
 * 持ち越し機能の domain 層が repository 層に求めるデータアクセス<br>
 * <p>
 * 外部 API（GitHub の Issue の取得）を api パッケージ、ファイルや DB へのアクセス（JSON ファイルの読み書き、標準時間の読み込み）を dao
 * パッケージに置く。実装は repository 層に置く。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
package io.github.kenichiroarai.dailytasks.carryover.domain.repository;
