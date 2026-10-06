package io.github.kenichiroarai.dailytasks.carryover.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;

/**
 * 項目ごとの標準時間の設定ファイル（例: config/default-minutes.json）を読み込む<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class DefaultMinutesRepository {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMinutesRepository.class);

    /**
     * 設定ファイルのパス
     */
    private final Path configFile;

    /**
     * コンストラクタ<br>
     *
     * @param configFile
     *                   設定ファイルのパス
     */
    public DefaultMinutesRepository(final Path configFile) {

        this.configFile = configFile;

    }

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @return 項目ごとの標準時間。設定ファイルがない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    public DefaultMinutes load() throws IOException {

        DefaultMinutes result = new DefaultMinutes(Map.of());

        if (!Files.isRegularFile(this.configFile)) {

            DefaultMinutesRepository.LOGGER.warn("標準時間の設定ファイルがありません: {}", this.configFile);
            return result;

        }

        final Map<String, Double> minutesByName = new ObjectMapper().readValue(this.configFile.toFile(),
            new TypeReference<Map<String, Double>>() {
                // 型情報の保持のみ
            });
        result = new DefaultMinutes(minutesByName);
        return result;

    }

}
