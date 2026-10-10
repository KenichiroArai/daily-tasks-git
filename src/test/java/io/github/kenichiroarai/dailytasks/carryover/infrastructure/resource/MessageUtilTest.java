package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource;

import java.lang.reflect.Constructor;
import java.util.MissingResourceException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link MessageUtil} のテスト<br>
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
public class MessageUtilTest {

    /**
     * MessageUtil コンストラクタのテスト - 正常系:private コンストラクタでインスタンスを生成できる場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testMessageUtil_normalPrivateConstructor() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final Constructor<MessageUtil> testConstructor = MessageUtil.class.getDeclaredConstructor();
        testConstructor.setAccessible(true);

        /* テスト対象の実行 */
        final MessageUtil testResult = testConstructor.newInstance();

        /* 検証の準備 */
        final Object actualInstance = testResult;

        /* 検証の実施 */
        Assertions.assertInstanceOf(MessageUtil.class, actualInstance, "インスタンスの型が一致しません");

    }

    /**
     * get メソッドのテスト - 正常系:キーに対応するメッセージを UTF-8 で取得する場合
     */
    @Test
    public void testGet_normalUtf8() {

        /* 期待値の定義 */
        final String expectedMessage = "テストの値";

        /* 準備 */

        /* テスト対象の実行 */
        final String testResult = MessageUtil.get("resource-util-test", "test.key");

        /* 検証の準備 */
        final String actualMessage = testResult;

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "メッセージが一致しません");

    }

    /**
     * get メソッドのテスト - 準正常系:キーが見つからない場合
     */
    @Test
    public void testGet_semiKeyNotFound() {

        /* 期待値の定義 */
        final String expectedKey = "not.found";

        /* 準備 */

        /* テスト対象の実行 */
        final MissingResourceException testException = Assertions.assertThrows(MissingResourceException.class,
            () -> MessageUtil.get("resource-util-test", "not.found"));

        /* 検証の準備 */
        final String actualKey = testException.getKey();

        /* 検証の実施 */
        Assertions.assertEquals(expectedKey, actualKey, "キーが一致しません");

    }

    /**
     * get メソッドのテスト - 準正常系:バンドルが見つからない場合
     */
    @Test
    public void testGet_semiBundleNotFound() {

        /* 期待値の定義 */
        final String expectedClassName = "not-found_";

        /* 準備 */

        /* テスト対象の実行 */
        final MissingResourceException testException = Assertions.assertThrows(MissingResourceException.class,
            () -> MessageUtil.get("not-found", "test.key"));

        /* 検証の準備 */
        final String actualClassName = testException.getClassName();

        /* 検証の実施 */
        Assertions.assertEquals(expectedClassName, actualClassName, "バンドル名が一致しません");

    }

}
