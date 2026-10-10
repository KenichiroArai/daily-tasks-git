package io.github.kenichiroarai.dailytasks.carryover.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * 項目ごとの標準時間の設定を読み込むリポジトリ<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface DefaultMinutesRepository {

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @param configFile
     *                   設定ファイルのパス（例: config/default-minutes.json）
     *
     * @return 項目名と標準時間（分）の対応。設定がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    Map<String, Double> load(Path configFile) throws IOException;

}
