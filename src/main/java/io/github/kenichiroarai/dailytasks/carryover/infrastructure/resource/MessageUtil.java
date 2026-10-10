package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * クラスパス上のメッセージ（リソースバンドル）の取得<br>
 * <p>
 * 業務を知らない汎用のユーティリティ。バンドル名とキーは呼び出し側が指定する。プロパティファイルは UTF-8 で読み込まれる。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public final class MessageUtil {

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private MessageUtil() {

        // 処理なし

    }

    /**
     * メッセージを取得する<br>
     *
     * @param bundleName
     *                   バンドル名（例: messages）
     * @param key
     *                   キー
     *
     * @return メッセージ
     *
     * @throws java.util.MissingResourceException
     *                                            バンドルまたはキーが見つからない場合
     */
    public static String get(final String bundleName, final String key) {

        final ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.ROOT);
        final String result = bundle.getString(key);
        return result;

    }

}
