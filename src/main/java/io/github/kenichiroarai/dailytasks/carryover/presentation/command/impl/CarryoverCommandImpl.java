package io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.application.service.impl.CarryoverServiceImpl;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageUtil;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.PropertiesUtil;
import io.github.kenichiroarai.dailytasks.carryover.presentation.command.CarryoverCommand;

/**
 * 持ち越しの収集コマンドの実装<br>
 * <p>
 * 入力の窓口として、設定ファイル（application.properties）と環境変数から設定を作り、application 層へ引き継ぐ。リポジトリのルートをカレントディレクトリとして実行する。
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
     * 設定ファイルのリソース名
     */
    private static final String CONFIG_FILE = "application.properties";

    /**
     * 設定のキー：対象リポジトリ
     */
    private static final String KEY_REPOSITORY = "carryover.repository";

    /**
     * 設定のキー：GitHub API のトークンを設定する環境変数名
     */
    private static final String KEY_TOKEN_ENV = "carryover.tokenEnv";

    /**
     * 設定のキー：出力先のディレクトリ
     */
    private static final String KEY_DATA_DIR = "carryover.dataDir";

    /**
     * 設定のキー：標準時間の設定ファイル
     */
    private static final String KEY_DEFAULT_MINUTES_FILE = "carryover.defaultMinutesFile";

    /**
     * 設定のキー：差分モードで更新日時に関係なく解析し直す最新の Issue の件数
     */
    private static final String KEY_RECENT_COUNT = "carryover.recentCount";

    /**
     * メッセージのバンドル名
     */
    private static final String MESSAGES = "messages";

    /**
     * メッセージのキー：使い方
     */
    private static final String MSG_USAGE = "carryover.command.usage";

    /**
     * メッセージのキー：不明な引数
     */
    private static final String MSG_UNKNOWN_ARGUMENT = "carryover.command.unknownArgument";

    /**
     * 引数：全件モード
     */
    private static final String ARG_FULL = "--full";

    /**
     * 引数：使い方の表示
     */
    private static final String ARG_HELP = "--help";

    /**
     * 引数：使い方の表示（短縮形）
     */
    private static final String ARG_SHORT_HELP = "-h";

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
     * 設定ファイルと環境変数から設定を作り、application 層の実装を生成する。
     * </p>
     *
     * @param out
     *            使い方の出力先
     *
     * @throws IOException
     *                     設定ファイルまたは標準時間の読み込みに失敗した場合
     */
    public CarryoverCommandImpl(final PrintStream out) throws IOException {

        final Properties config = PropertiesUtil.load(CarryoverCommandImpl.CONFIG_FILE);
        final String tokenEnv = config.getProperty(CarryoverCommandImpl.KEY_TOKEN_ENV);
        final String token = System.getenv(tokenEnv);
        final CarryoverSettings settings = CarryoverCommandImpl.createSettings(config, token);
        final CarryoverService service = new CarryoverServiceImpl(settings);
        this(service, out);

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
        final boolean full = CarryoverCommandImpl.isFull(args);
        final boolean help = CarryoverCommandImpl.isHelp(args);

        /* 使い方の表示 */
        if (help) {

            final String usage = MessageUtil.get(CarryoverCommandImpl.MESSAGES, CarryoverCommandImpl.MSG_USAGE);
            this.out.println(usage);
            return result;

        }

        /* 収集の実行 */
        result = this.carryoverService.collect(full);
        return result;

    }

    /**
     * 引数に全件モードの指定があるかを返す<br>
     * <p>
     * 不明な引数がないことも検証する。
     * </p>
     *
     * @param args
     *             コマンドライン引数
     *
     * @return true：全件モード、false：差分モード
     *
     * @throws IllegalArgumentException
     *                                  不明な引数が指定された場合
     */
    private static boolean isFull(final String[] args) {

        boolean result = false;

        for (final String arg : args) {

            switch (arg) {

                case CarryoverCommandImpl.ARG_FULL -> result = true;

                case CarryoverCommandImpl.ARG_HELP, CarryoverCommandImpl.ARG_SHORT_HELP -> {

                    // 使い方の表示は isHelp で判定する

                }

                default -> {

                    final String template = MessageUtil.get(CarryoverCommandImpl.MESSAGES,
                        CarryoverCommandImpl.MSG_UNKNOWN_ARGUMENT);
                    final String message = String.format(template, arg);
                    throw new IllegalArgumentException(message);

                }

            }

        }

        return result;

    }

    /**
     * 引数に使い方の表示の指定があるかを返す<br>
     *
     * @param args
     *             コマンドライン引数
     *
     * @return true：使い方を表示する、false：表示しない
     */
    private static boolean isHelp(final String[] args) {

        final List<String> argList = List.of(args);
        final boolean result = argList.contains(CarryoverCommandImpl.ARG_HELP)
            || argList.contains(CarryoverCommandImpl.ARG_SHORT_HELP);
        return result;

    }

    /**
     * application 層に引き継ぐ設定を作る<br>
     *
     * @param config
     *               設定ファイルの内容
     * @param token
     *               GitHub API のトークン。未指定の場合は null
     *
     * @return 持ち越しの収集・集計の設定
     */
    private static CarryoverSettings createSettings(final Properties config, final String token) {

        final String repository = config.getProperty(CarryoverCommandImpl.KEY_REPOSITORY);
        final String dataDirValue = config.getProperty(CarryoverCommandImpl.KEY_DATA_DIR);
        final Path dataDir = Path.of(dataDirValue);
        final String defaultMinutesFileValue = config.getProperty(CarryoverCommandImpl.KEY_DEFAULT_MINUTES_FILE);
        final Path defaultMinutesFile = Path.of(defaultMinutesFileValue);
        final String recentCountValue = config.getProperty(CarryoverCommandImpl.KEY_RECENT_COUNT);
        final int recentCount = Integer.parseInt(recentCountValue);

        final CarryoverSettings result = new CarryoverSettings(repository, token, dataDir, defaultMinutesFile,
            recentCount);
        return result;

    }

}
