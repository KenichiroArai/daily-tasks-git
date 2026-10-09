package io.github.kenichiroarai.dailytasks;

import java.io.IOException;

import io.github.kenichiroarai.dailytasks.carryover.presentation.command.CarryoverCommand;
import io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl.CarryoverCommandImpl;

/**
 * 起動クラス<br>
 * <p>
 * presentation 層のコマンドを生成して実行するだけとする。設定値や部品の組み立ては各層が順に引き継ぐ。 リポジトリのルートをカレントディレクトリとして実行する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public final class DailyTasksApplication {

    /**
     * エントリポイント<br>
     *
     * @param args
     *             コマンドライン引数（{@link CarryoverCommand} を参照）
     *
     * @throws IOException
     *                     取得、読み込みまたは書き込みに失敗した場合
     */
    public static void main(final String[] args) throws IOException {

        /* コマンドの実行 */
        final CarryoverCommand command = new CarryoverCommandImpl(System.out);
        command.execute(args);

    }

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private DailyTasksApplication() {

        // 処理なし

    }

}
