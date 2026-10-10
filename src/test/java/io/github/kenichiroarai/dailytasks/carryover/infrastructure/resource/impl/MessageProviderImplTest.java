package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;

import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;
import io.github.kenichiroarai.dailytasks.testutil.MessageProviderTestUtil;

/**
 * {@link MessageProviderImpl} のテスト<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings({
    "nls", "static-method"
})
public class MessageProviderImplTest {

    /**
     * get メソッドのテスト - 正常系:書式の埋め込み位置をそのまま返す
     */
    @Test
    public void testGet_normalPlaceholder() {

        /* 期待値の定義 */
        final String expectedMessage = "不明な引数です: %s";

        /* 準備 */
        final MessageProvider testTarget = MessageProviderTestUtil.create();

        /* テスト対象の実行 */
        final String testResult = testTarget.get("carryover.command.unknownArgument");

        /* 検証の準備 */
        final String actualMessage = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "メッセージが一致しません");

    }

    /**
     * get メソッドのテスト - 準正常系:キーが見つからない場合
     */
    @Test
    public void testGet_semiUnknownKey() {

        /* 期待値の定義 */
        final String expectedMessage = "No message found under code 'unknown.key' for locale ''.";

        /* 準備 */
        final MessageProvider testTarget = MessageProviderTestUtil.create();

        /* テスト対象の実行 */
        final NoSuchMessageException testException = Assertions.assertThrows(NoSuchMessageException.class,
            () -> testTarget.get("unknown.key"));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

}
