package io.github.kenichiroarai.dailytasks.carryover.presentation.command;

import java.io.IOException;
import java.io.PrintStream;

import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;

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
public class CarryoverCommand {

    /**
     * 使い方
     */
    static final String USAGE = """
        使い方: java -jar daily-tasks-0.1.0.jar [--full] [--help]
          （引数なし）  差分モード。最新 10 件の Issue は毎回解析し直し、それ以外は保存済みの JSON と更新日時が同じなら解析しない
          --full        全件モード。#1 から最新まで解析し直す
          --help, -h    この使い方を表示する
        環境変数 GITHUB_TOKEN が設定されている場合は GitHub API の認証に使う""";

    /**
     * 持ち越しの収集・集計サービス
     */
    private final CarryoverService carryoverService;

    /**
     * 使い方の出力先
     */
    private final PrintStream out;

    /**
     * コンストラクタ<br>
     *
     * @param carryoverService
     *                         持ち越しの収集・集計サービス
     * @param out
     *                         使い方の出力先
     */
    public CarryoverCommand(final CarryoverService carryoverService, final PrintStream out) {

        this.carryoverService = carryoverService;
        this.out = out;

    }

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
    public int execute(final String[] args) throws IOException {

        int result = 0;

        /* 引数の解釈 */
        boolean full = false;

        for (final String arg : args) {

            switch (arg) {

                case "--full" -> full = true;

                case "--help", "-h" -> {

                    this.out.println(CarryoverCommand.USAGE);
                    return result;

                }

                default -> throw new IllegalArgumentException(String.format("不明な引数です: %s", arg));

            }

        }

        /* 収集の実行 */
        result = this.carryoverService.collect(full);
        return result;

    }

}
