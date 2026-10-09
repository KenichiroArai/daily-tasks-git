package io.github.kenichiroarai.dailytasks.carryover.presentation.command;

import java.io.IOException;

/**
 * 持ち越しの収集コマンド<br>
 * <p>
 * 引数：
 * </p>
 * <ul>
 * <li>なし：差分モード</li>
 * <li>{@code --full}：全件モード</li>
 * <li>{@code --help} / {@code -h}：使い方を表示</li>
 * </ul>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverCommand {

    /**
     * コマンドを実行する<br>
     *
     * @param args
     *             コマンドライン引数
     *
     * @return 解析して保存した Issue の件数。使い方を表示した場合は 0
     *
     * @throws IOException
     *                                  取得、読み込みまたは書き込みに失敗した場合
     * @throws IllegalArgumentException
     *                                  不明な引数が指定された場合
     */
    int execute(String[] args) throws IOException;

}
