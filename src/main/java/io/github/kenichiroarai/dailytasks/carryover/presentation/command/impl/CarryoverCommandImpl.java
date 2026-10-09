package io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.application.service.impl.CarryoverServiceImpl;
import io.github.kenichiroarai.dailytasks.carryover.presentation.command.CarryoverCommand;

/**
 * 持ち越しの収集コマンドの実装<br>
 * <p>
 * 入力の窓口として、環境変数と既定値から設定を作り、application 層へ引き継ぐ。リポジトリのルートをカレントディレクトリとして実行する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverCommandImpl implements CarryoverCommand {

    /**
     * 使い方
     */
    private static final String USAGE = """
        使い方: java -jar daily-tasks-0.1.0.jar [--full] [--help]
          （引数なし）  差分モード。最新 10 件の Issue は毎回解析し直し、それ以外は保存済みの JSON と更新日時が同じなら解析しない
          --full        全件モード。#1 から最新まで解析し直す
          --help, -h    この使い方を表示する
        環境変数 GITHUB_TOKEN が設定されている場合は GitHub API の認証に使う""";

    /**
     * GitHub API のトークンを設定する環境変数名
     */
    private static final String TOKEN_ENV = "GITHUB_TOKEN";

    /**
     * 対象リポジトリ
     */
    private static final String REPOSITORY = "KenichiroArai/daily-tasks-git";

    /**
     * 出力先のディレクトリ
     */
    private static final Path DATA_DIR = Path.of("docs", "data");

    /**
     * 標準時間の設定ファイル
     */
    private static final Path DEFAULT_MINUTES_FILE = Path.of("config", "default-minutes.json");

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
     * <p>
     * 環境変数 GITHUB_TOKEN と既定値から設定を作り、application 層の実装を生成する。
     * </p>
     *
     * @param out
     *            使い方の出力先
     *
     * @throws IOException
     *                     標準時間の読み込みに失敗した場合
     */
    public CarryoverCommandImpl(final PrintStream out) throws IOException {

        this(new CarryoverServiceImpl(
            CarryoverCommandImpl.createSettings(System.getenv(CarryoverCommandImpl.TOKEN_ENV))), out);

    }

    /**
     * サービスを指定するコンストラクタ<br>
     *
     * @param carryoverService
     *                         持ち越しの収集・集計サービス
     * @param out
     *                         使い方の出力先
     */
    public CarryoverCommandImpl(final CarryoverService carryoverService, final PrintStream out) {

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
    @Override
    public int execute(final String[] args) throws IOException {

        int result = 0;

        /* 引数の解釈 */
        boolean full = false;

        for (final String arg : args) {

            switch (arg) {

                case "--full" -> full = true;

                case "--help", "-h" -> {

                    this.out.println(CarryoverCommandImpl.USAGE);
                    return result;

                }

                default -> throw new IllegalArgumentException(String.format("不明な引数です: %s", arg));

            }

        }

        /* 収集の実行 */
        result = this.carryoverService.collect(full);
        return result;

    }

    /**
     * application 層に引き継ぐ設定を作る<br>
     *
     * @param token
     *              GitHub API のトークン。未指定の場合は null
     *
     * @return 持ち越しの収集・集計の設定
     */
    private static CarryoverSettings createSettings(final String token) {

        final CarryoverSettings result = new CarryoverSettings(CarryoverCommandImpl.REPOSITORY, token,
            CarryoverCommandImpl.DATA_DIR, CarryoverCommandImpl.DEFAULT_MINUTES_FILE);
        return result;

    }

}
