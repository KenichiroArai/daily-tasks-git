package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;

/**
 * Spring の {@link MessageSource} からメッセージを取得する実装<br>
 * <p>
 * 引数を渡さずに取得するため、メッセージ中の {}、%s、%d は書式として解釈されずにそのまま返る。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
public class MessageProviderImpl implements MessageProvider {

    /**
     * メッセージの取得元
     */
    private final MessageSource messageSource;

    /**
     * コンストラクタ<br>
     *
     * @param messageSource
     *                      メッセージの取得元
     */
    public MessageProviderImpl(final MessageSource messageSource) {

        this.messageSource = messageSource;

    }

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
    @Override
    public String get(final String key) {

        final String result = this.messageSource.getMessage(key, null, Locale.ROOT);
        return result;

    }

}
