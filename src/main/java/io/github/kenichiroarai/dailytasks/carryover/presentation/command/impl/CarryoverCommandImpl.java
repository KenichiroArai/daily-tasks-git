package io.github.kenichiroarai.dailytasks.carryover.presentation.command.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;
import io.github.kenichiroarai.dailytasks.carryover.presentation.command.CarryoverCommand;
import io.github.kenichiroarai.dailytasks.carryover.presentation.config.CarryoverProperties;
import io.github.kenichiroarai.dailytasks.carryover.presentation.model.CarryoverOptions;

/**
 * 持ち越しの収集コマンドの実装<br>
 * <p>
 * 入力の窓口として、設定ファイル（application.properties）の値とコマンドライン引数から設定を作り、application 層へ引き継ぐ。Spring Boot の起動後に
 * {@link CommandLineRunner} として実行される。リポジトリのルートをカレントディレクトリとして実行する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Component
public class CarryoverCommandImpl implements CarryoverCommand {

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
     * 設定ファイルの値から application 層に引き継ぐ設定を作る<br>
     *
     * @param properties
     *                   設定ファイルの値
     *
     * @return 持ち越しの収集・集計の設定
     */
    private static CarryoverSettings createSettings(final CarryoverProperties properties) {

        final Path dataDir            = Path.of(properties.getDataDir());
        final Path defaultMinutesFile = Path.of(properties.getDefaultMinutesFile());

        final CarryoverSettings result = new CarryoverSettings(properties.getRepository(), properties.getToken(),
            dataDir, defaultMinutesFile, properties.getRecentCount());
        return result;

    }

    /**
     * 持ち越しの収集・集計サービス
     */
    private final CarryoverService carryoverService;

    /**
     * 設定ファイルの値
     */
    private final CarryoverProperties properties;

    /**
     * メッセージの取得
     */
    private final MessageProvider messageProvider;

    /**
     * 使い方の出力先
     */
    private final PrintStream out;

    /**
     * コンストラクタ<br>
     * <p>
     * 使い方は標準出力に出す。
     * </p>
     *
     * @param carryoverService
     *                         持ち越しの収集・集計サービス
     * @param properties
     *                         設定ファイルの値
     * @param messageProvider
     *                         メッセージの取得
     */
    @Autowired
    public CarryoverCommandImpl(final CarryoverService carryoverService, final CarryoverProperties properties,
        final MessageProvider messageProvider) {

        this(carryoverService, properties, messageProvider, System.out);

    }

    /**
     * 使い方の出力先を指定するコンストラクタ<br>
     *
     * @param carryoverService
     *                         持ち越しの収集・集計サービス
     * @param properties
     *                         設定ファイルの値
     * @param messageProvider
     *                         メッセージの取得
     * @param out
     *                         使い方の出力先
     */
    public CarryoverCommandImpl(final CarryoverService carryoverService, final CarryoverProperties properties,
        final MessageProvider messageProvider, final PrintStream out) {

        this.carryoverService = carryoverService;
        this.properties = properties;
        this.messageProvider = messageProvider;
        this.out = out;

    }

    /**
     * Spring Boot の起動後にコマンドを実行する<br>
     *
     * @param args
     *             コマンドライン引数
     *
     * @throws IOException
     *                                  取得、読み込みまたは書き込みに失敗した場合
     * @throws IllegalArgumentException
     *                                  不明な引数が指定された場合
     */
    @Override
    public void run(final String... args) throws IOException {

        /* 引数の解析 */
        final CarryoverOptions options = this.parseArgs(args);

        /* 使い方の表示 */
        if (options.isHelp()) {

            final String usage = this.messageProvider.get(CarryoverCommandImpl.MSG_USAGE);
            this.out.println(usage);
            return;

        }

        /* 収集の実行 */
        final CarryoverSettings settings = CarryoverCommandImpl.createSettings(this.properties);
        this.carryoverService.collect(settings, options.isFull());

    }

    /**
     * コマンドライン引数を解析してオプションを返す<br>
     *
     * @param args
     *             コマンドライン引数
     *
     * @return 持ち越しの収集コマンドのオプション
     *
     * @throws IllegalArgumentException
     *                                  不明な引数が指定された場合
     */
    private CarryoverOptions parseArgs(final String[] args) {

        final CarryoverOptions result = new CarryoverOptions();

        for (final String arg : args) {

            switch (arg) {

                case CarryoverCommandImpl.ARG_FULL -> result.setFull(true);

                case CarryoverCommandImpl.ARG_HELP, CarryoverCommandImpl.ARG_SHORT_HELP -> result.setHelp(true);

                default -> {

                    final String template = this.messageProvider.get(CarryoverCommandImpl.MSG_UNKNOWN_ARGUMENT);
                    final String message  = String.format(template, arg);
                    throw new IllegalArgumentException(message);

                }

            }

        }

        return result;

    }

}
