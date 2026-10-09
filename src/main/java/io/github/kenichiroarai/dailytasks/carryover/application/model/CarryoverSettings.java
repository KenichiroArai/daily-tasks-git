package io.github.kenichiroarai.dailytasks.carryover.application.model;

import java.nio.file.Path;

/**
 * 持ち越しの収集・集計の設定<br>
 * <p>
 * application 層が受け取る設定。トークンはログに出さないよう、{@link #toString()} に含めない。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverSettings {

    /**
     * 対象リポジトリ（owner/name）
     */
    private final String repository;

    /**
     * GitHub API のトークン。未指定の場合は null
     */
    private final String token;

    /**
     * 出力先のディレクトリ（例: docs/data）
     */
    private final Path dataDir;

    /**
     * 標準時間の設定ファイル（例: config/default-minutes.json）
     */
    private final Path defaultMinutesFile;

    /**
     * コンストラクタ<br>
     *
     * @param repository
     *                           対象リポジトリ（owner/name）
     * @param token
     *                           GitHub API のトークン。未指定の場合は null
     * @param dataDir
     *                           出力先のディレクトリ（例: docs/data）
     * @param defaultMinutesFile
     *                           標準時間の設定ファイル（例: config/default-minutes.json）
     */
    public CarryoverSettings(final String repository, final String token, final Path dataDir,
        final Path defaultMinutesFile) {

        this.repository = repository;
        this.token = token;
        this.dataDir = dataDir;
        this.defaultMinutesFile = defaultMinutesFile;

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
     * @return GitHub API のトークン。未指定の場合は null
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
    public Path getDataDir() {

        final Path result = this.dataDir;
        return result;

    }

    /**
     * 標準時間の設定ファイルを返す<br>
     *
     * @return 標準時間の設定ファイル
     */
    public Path getDefaultMinutesFile() {

        final Path result = this.defaultMinutesFile;
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

        final String result = String.format("CarryoverSettings[repository=%s, dataDir=%s, defaultMinutesFile=%s]",
            this.repository, this.dataDir, this.defaultMinutesFile);
        return result;

    }

}
