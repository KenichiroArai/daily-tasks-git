package io.github.kenichiroarai.dailytasks.testutil;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * テスト用のログ取得<br>
 * <p>
 * 対象クラスのロガーに Logback の {@link ListAppender} を追加し、出力されたログメッセージを取得する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public final class LogCapture implements AutoCloseable {

    /**
     * 対象のロガー
     */
    private final Logger logger;

    /**
     * ログの取得先
     */
    private final ListAppender<ILoggingEvent> listAppender;

    /**
     * コンストラクタ<br>
     *
     * @param target
     *               ログを取得する対象クラス
     */
    public LogCapture(final Class<?> target) {

        this.logger = (Logger) LoggerFactory.getLogger(target);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(this.listAppender);

    }

    /**
     * ログの取得を終了する<br>
     */
    @Override
    public void close() {

        this.logger.detachAppender(this.listAppender);
        this.listAppender.stop();

    }

    /**
     * 取得したログメッセージを返す<br>
     *
     * @return ログメッセージ（出力順）
     */
    public String[] getMessages() {

        final String[] result
            = this.listAppender.list.stream().map(ILoggingEvent::getFormattedMessage).toArray(String[]::new);
        return result;

    }

}
