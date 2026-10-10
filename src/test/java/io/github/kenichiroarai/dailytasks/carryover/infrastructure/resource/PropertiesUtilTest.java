package io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link PropertiesUtil} のテスト<br>
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
public class PropertiesUtilTest {

    /**
     * PropertiesUtil コンストラクタのテスト - 正常系:private コンストラクタでインスタンスを生成できる場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testPropertiesUtil_normalPrivateConstructor() throws Exception {

        /* 期待値の定義 */

        /* 準備 */
        final Constructor<PropertiesUtil> testConstructor = PropertiesUtil.class.getDeclaredConstructor();
        testConstructor.setAccessible(true);

        /* テスト対象の実行 */
        final PropertiesUtil testResult = testConstructor.newInstance();

        /* 検証の準備 */
        final Object actualInstance = testResult;

        /* 検証の実施 */
        Assertions.assertInstanceOf(PropertiesUtil.class, actualInstance, "インスタンスの型が一致しません");

    }

    /**
     * load メソッドのテスト - 正常系:クラスパス上のプロパティファイルを UTF-8 で読み込む場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoad_normalUtf8() throws IOException {

        /* 期待値の定義 */
        final String expectedValue = "テストの値";

        /* 準備 */

        /* テスト対象の実行 */
        final Properties testResult = PropertiesUtil.load("resource-util-test.properties");

        /* 検証の準備 */
        final String actualValue = testResult.getProperty("test.key");

        /* 検証の実施 */
        Assertions.assertEquals(expectedValue, actualValue, "値が一致しません");

    }

    /**
     * load メソッドのテスト - 準正常系:リソースが見つからない場合
     */
    @Test
    public void testLoad_semiNotFound() {

        /* 期待値の定義 */
        final String expectedMessage = "リソースが見つかりません: not-found.properties";

        /* 準備 */

        /* テスト対象の実行 */
        final IOException testException = Assertions.assertThrows(IOException.class,
            () -> PropertiesUtil.load("not-found.properties"));

        /* 検証の準備 */
        final String actualMessage = testException.getMessage();

        /* 検証の実施 */
        Assertions.assertEquals(expectedMessage, actualMessage, "例外のメッセージが一致しません");

    }

}
