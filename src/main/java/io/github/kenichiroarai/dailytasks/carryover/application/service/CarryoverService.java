package io.github.kenichiroarai.dailytasks.carryover.application.service;

import java.io.IOException;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;

/**
 * 持ち越しの収集・集計サービス<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverService {

    /**
     * Issue を取得して解析し、Issue ごとの JSON と画面用の集計を出力する<br>
     * <p>
     * 差分モードでは、Issue 番号が大きい順の最新の指定件数（設定の recentCount）を除き、保存済みの JSON と更新日時が同じ Issue は解析しない。
     * </p>
     *
     * @param settings
     *                 持ち越しの収集・集計の設定
     * @param full
     *                 true：全件モード（すべての Issue を解析し直す）、false：差分モード
     *
     * @return 解析して保存した Issue の件数
     *
     * @throws IOException
     *                     取得、読み込みまたは書き込みに失敗した場合
     */
    int collect(CarryoverSettings settings, boolean full) throws IOException;

}
