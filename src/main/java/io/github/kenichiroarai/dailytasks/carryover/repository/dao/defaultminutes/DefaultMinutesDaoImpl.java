package io.github.kenichiroarai.dailytasks.carryover.repository.dao.defaultminutes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import io.github.kenichiroarai.dailytasks.carryover.domain.repository.dao.DefaultMinutesDao;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.MessageLogic;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * 項目ごとの標準時間の設定ファイル（例: config/default-minutes.json）を読み込む DAO の実装<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Repository
public class DefaultMinutesDaoImpl implements DefaultMinutesDao {

    /** ロガー */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMinutesDaoImpl.class);

    /** メッセージのキー：設定ファイルがない */
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

    /** JSON の変換 */
    private final JsonMapper jsonMapper;

    /** メッセージの取得 */
    private final MessageLogic messageLogic;

    /**
     * コンストラクタ<br>
     *
     * @param jsonMapper
     *                     JSON の変換
     * @param messageLogic
     *                     メッセージの取得
     */
    public DefaultMinutesDaoImpl(final JsonMapper jsonMapper, final MessageLogic messageLogic) {

        this.jsonMapper = jsonMapper;
        this.messageLogic = messageLogic;

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

            final String message = this.messageLogic.get(DefaultMinutesDaoImpl.MSG_FILE_NOT_FOUND);
            DefaultMinutesDaoImpl.LOGGER.warn(message, configFile);
            return result;

        }

        try {

            result = this.jsonMapper.readValue(configFile.toFile(), DefaultMinutesDaoImpl.DEFAULT_MINUTES_TYPE);

        } catch (final JacksonException e) {

            throw new IOException(e);

        }

        return result;

    }

}
