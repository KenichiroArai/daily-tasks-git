package io.github.kenichiroarai.dailytasks.carryover.repository.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.kenichiroarai.dailytasks.carryover.repository.DefaultMinutesRepository;

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
public class DefaultMinutesRepositoryImpl implements DefaultMinutesRepository {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMinutesRepositoryImpl.class);

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
    public DefaultMinutesRepositoryImpl(final Path configFile) {

        this.configFile = configFile;

    }

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @return 項目名と標準時間（分）の対応。設定ファイルがない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public Map<String, Double> load() throws IOException {

        Map<String, Double> result = Map.of();

        if (!Files.isRegularFile(this.configFile)) {

            DefaultMinutesRepositoryImpl.LOGGER.warn("標準時間の設定ファイルがありません: {}", this.configFile);
            return result;

        }

        result = new ObjectMapper().readValue(this.configFile.toFile(), new TypeReference<Map<String, Double>>() {
            // 型情報の保持のみ
        });
        return result;

    }

}
