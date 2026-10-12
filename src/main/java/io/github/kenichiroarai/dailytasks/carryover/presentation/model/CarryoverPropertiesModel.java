package io.github.kenichiroarai.dailytasks.carryover.presentation.model;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 持ち越しの収集の設定<br>
 * <p>
 * application.properties の {@code carryover.*} を Spring Boot がコンストラクタで設定する。設定ファイルの値を受け取るのは presentation
 * 層のこのクラスだけとし、presentation 層が application 層に引き継ぐ設定としてもそのまま使う。パスはコンストラクタで 1 回だけ変換して保持する。トークンはログに出さないよう、{@link #toString()}
 * に含めない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@ConfigurationProperties(prefix = "carryover")
public class CarryoverPropertiesModel {

    /* オリジナル */

    /** 対象リポジトリ（owner/name） */
    private final String repository;

    /** GitHub API のトークン。未設定の場合は空文字 */
    private final String token;

    /** 出力先のディレクトリ（例: docs/data） */
    private final String dataDir;

    /** 標準時間の設定ファイル（例: config/default-minutes.json） */
    private final String defaultMinutesFile;

    /** 差分モードで更新日時に関係なく解析し直す最新の Issue の件数 */
    private final int recentCount;

    /* 変換後 */

    /** 出力先のディレクトリのパス */
    private final Path dataDirPath;

    /** 標準時間の設定ファイルのパス */
    private final Path defaultMinutesFilePath;

    /**
     * コンストラクタ<br>
     *
     * @param repository
     *                           対象リポジトリ（owner/name）
     * @param token
     *                           GitHub API のトークン。未設定の場合は空文字
     * @param dataDir
     *                           出力先のディレクトリ（例: docs/data）
     * @param defaultMinutesFile
     *                           標準時間の設定ファイル（例: config/default-minutes.json）
     * @param recentCount
     *                           差分モードで更新日時に関係なく解析し直す最新の Issue の件数
     */
    public CarryoverPropertiesModel(final String repository, final String token, final String dataDir,
        final String defaultMinutesFile, final int recentCount) {

        /* オリジナル */
        this.repository = repository;
        this.token = token;
        this.dataDir = dataDir;
        this.defaultMinutesFile = defaultMinutesFile;
        this.recentCount = recentCount;

        /* 変換後 */
        this.dataDirPath = Path.of(dataDir);
        this.defaultMinutesFilePath = Path.of(defaultMinutesFile);

    }

    /**
     * 出力先のディレクトリを返す<br>
     *
     * @return 出力先のディレクトリ
     */
    public Path getDataDir() {

        final Path result = this.dataDirPath;
        return result;

    }

    /**
     * 標準時間の設定ファイルを返す<br>
     *
     * @return 標準時間の設定ファイル
     */
    public Path getDefaultMinutesFile() {

        final Path result = this.defaultMinutesFilePath;
        return result;

    }

    /**
     * 差分モードで更新日時に関係なく解析し直す最新の Issue の件数を返す<br>
     *
     * @return 差分モードで更新日時に関係なく解析し直す最新の Issue の件数
     */
    public int getRecentCount() {

        final int result = this.recentCount;
        return result;

    }

    /**
     * 対象リポジトリを返す<br>
     *
     * @return 対象リポジトリ（owner/name）
     */
    public String getRepository() {

        final String result = this.repository;
        return result;

    }

    /**
     * GitHub API のトークンを返す<br>
     *
     * @return GitHub API のトークン。未設定の場合は空文字
     */
    public String getToken() {

        final String result = this.token;
        return result;

    }

    /**
     * 文字列表現を返す<br>
     * <p>
     * トークンは含めない。
     * </p>
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {

        final String result
            = String.format("CarryoverPropertiesModel[repository=%s, dataDir=%s, defaultMinutesFile=%s, recentCount=%d]",
                this.repository, this.dataDir, this.defaultMinutesFile, Integer.valueOf(this.recentCount));
        return result;

    }

}
