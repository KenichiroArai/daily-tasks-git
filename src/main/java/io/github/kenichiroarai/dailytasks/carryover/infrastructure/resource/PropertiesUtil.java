package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * クラスパス上のプロパティファイルの読み込み<br>
 * <p>
 * 業務を知らない汎用のユーティリティ。読み込むリソース名は呼び出し側が指定する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public final class PropertiesUtil {

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private PropertiesUtil() {

        // 処理なし

    }

    /**
     * クラスパス上のプロパティファイルを UTF-8 で読み込む<br>
     *
     * @param resourceName
     *                     リソース名（例: application.properties）
     *
     * @return 読み込んだプロパティ
     *
     * @throws IOException
     *                     リソースが見つからない場合、または読み込みに失敗した場合
     */
    public static Properties load(final String resourceName) throws IOException {

        final Properties result = new Properties();

        final ClassLoader classLoader = PropertiesUtil.class.getClassLoader();
        final InputStream stream = classLoader.getResourceAsStream(resourceName);

        if (stream == null) {

            throw new IOException(String.format("リソースが見つかりません: %s", resourceName));

        }

        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

            result.load(reader);

        }

        return result;

    }

}
