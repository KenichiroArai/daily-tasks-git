package io.github.kenichiroarai.dailytasks.carryover.repository.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;
import io.github.kenichiroarai.dailytasks.carryover.repository.DefaultMinutesRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * 項目ごとの標準時間の設定ファイル（例: config/default-minutes.json）を読み込むリポジトリの実装<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Repository
public class DefaultMinutesRepositoryImpl implements DefaultMinutesRepository {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMinutesRepositoryImpl.class);

    /**
     * メッセージのキー：設定ファイルがない
     */
    private static final String MSG_FILE_NOT_FOUND = "carryover.defaultMinutes.fileNotFound";

    /**
     * 設定ファイルの型（項目名と標準時間（分）の対応）
     * <p>
     * 無名クラスが外側のインスタンスを持たないよう、static の定数にする。
     * </p>
     */
    private static final TypeReference<Map<String, Double>> DEFAULT_MINUTES_TYPE = new TypeReference<>() {
        // 型情報の保持のみ
    };

    /**
     * JSON の変換
     */
    private final JsonMapper jsonMapper;

    /**
     * メッセージの取得
     */
    private final MessageProvider messageProvider;

    /**
     * コンストラクタ<br>
     *
     * @param jsonMapper
     *                        JSON の変換
     * @param messageProvider
     *                        メッセージの取得
     */
    public DefaultMinutesRepositoryImpl(final JsonMapper jsonMapper, final MessageProvider messageProvider) {

        this.jsonMapper = jsonMapper;
        this.messageProvider = messageProvider;

    }

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @param configFile
     *                   設定ファイルのパス（例: config/default-minutes.json）
     *
     * @return 項目名と標準時間（分）の対応。設定ファイルがない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public Map<String, Double> load(final Path configFile) throws IOException {

        Map<String, Double> result = Map.of();

        if (!Files.isRegularFile(configFile)) {

            final String message = this.messageProvider.get(DefaultMinutesRepositoryImpl.MSG_FILE_NOT_FOUND);
            DefaultMinutesRepositoryImpl.LOGGER.warn(message, configFile);
            return result;

        }

        try {

            result = this.jsonMapper.readValue(configFile.toFile(),
                DefaultMinutesRepositoryImpl.DEFAULT_MINUTES_TYPE);

        } catch (final JacksonException e) {

            throw new IOException(e);

        }

        return result;

    }

}
