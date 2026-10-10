package io.github.kenichiroarai.dailytasks.carryover.presentation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 持ち越しの収集の設定ファイルの値<br>
 * <p>
 * application.properties の {@code carryover.*} を Spring Boot がコンストラクタで設定する。設定ファイルの値を受け取るのは presentation
 * 層のこのクラスだけとし、下の層へは境界ごとに下の層の型に変換して引き継ぐ。トークンはログに出さないよう、{@link #toString()} に含めない。
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
public class CarryoverProperties {

    /**
     * 対象リポジトリ（owner/name）
     */
    private final String repository;

    /**
     * GitHub API のトークン。未設定の場合は空文字
     */
    private final String token;

    /**
     * 出力先のディレクトリ（例: docs/data）
     */
    private final String dataDir;

    /**
     * 標準時間の設定ファイル（例: config/default-minutes.json）
     */
    private final String defaultMinutesFile;

    /**
     * 差分モードで更新日時に関係なく解析し直す最新の Issue の件数
     */
    private final int recentCount;

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
    public CarryoverProperties(final String repository, final String token, final String dataDir,
        final String defaultMinutesFile, final int recentCount) {

        this.repository = repository;
        this.token = token;
        this.dataDir = dataDir;
        this.defaultMinutesFile = defaultMinutesFile;
        this.recentCount = recentCount;

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
     * 出力先のディレクトリを返す<br>
     *
     * @return 出力先のディレクトリ
     */
    public String getDataDir() {

        final String result = this.dataDir;
        return result;

    }

    /**
     * 標準時間の設定ファイルを返す<br>
     *
     * @return 標準時間の設定ファイル
     */
    public String getDefaultMinutesFile() {

        final String result = this.defaultMinutesFile;
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
     * 文字列表現を返す<br>
     * <p>
     * トークンは含めない。
     * </p>
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {

        final String result = String.format(
            "CarryoverProperties[repository=%s, dataDir=%s, defaultMinutesFile=%s, recentCount=%d]", this.repository,
            this.dataDir, this.defaultMinutesFile, Integer.valueOf(this.recentCount));
        return result;

    }

}
