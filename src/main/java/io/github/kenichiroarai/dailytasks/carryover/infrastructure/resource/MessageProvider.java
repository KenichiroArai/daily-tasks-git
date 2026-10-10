package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource;

/**
 * メッセージ（messages.properties）の取得<br>
 * <p>
 * 業務を知らない汎用のユーティリティ。キーは呼び出し側が持つ。埋め込み位置（SLF4J の {}、String.format の %s / %d）はそのまま返す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface MessageProvider {

    /**
     * メッセージを取得する<br>
     *
     * @param key
     *            キー
     *
     * @return メッセージ
     *
     * @throws org.springframework.context.NoSuchMessageException
     *                                                            キーが見つからない場合
     */
    String get(String key);

}
