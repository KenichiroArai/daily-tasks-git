package io.github.kenichiroarai.dailytasks.testutil;

import java.nio.charset.StandardCharsets;

import org.springframework.context.support.ResourceBundleMessageSource;

import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.MessageLogic;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.impl.MessageLogicImpl;

/**
 * テスト用のメッセージの取得の生成<br>
 * <p>
 * Spring Boot を起動せずに、本番と同じ messages.properties を読む {@link MessageLogic} を作る。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public final class MessageLogicTestUtil {

    /**
     * messages.properties を読むメッセージの取得を作る<br>
     *
     * @return メッセージの取得
     */
    public static MessageLogic create() {

        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(false);

        final MessageLogic result = new MessageLogicImpl(messageSource);
        return result;

    }

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private MessageLogicTestUtil() {

        // 処理なし

    }

}
