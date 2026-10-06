package io.github.kenichiroarai.dailytasks.testutil;

import org.junit.jupiter.api.Assertions;

/**
 * テスト用のログメッセージの検証<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public final class LogAssertions {

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private LogAssertions() {

        // 処理なし

    }

    /**
     * ログメッセージを 1 行ずつ検証し、件数も検証する<br>
     *
     * @param expectedMsgs
     *                     期待するログメッセージ
     * @param actualMsgs
     *                     実際のログメッセージ
     */
    public static void assertMessages(final String[] expectedMsgs, final String[] actualMsgs) {

        // ログのチェック
        final int verMsgLength = Math.min(expectedMsgs.length, actualMsgs.length);

        for (int i = 0; i < verMsgLength; i++) {

            Assertions.assertEquals(expectedMsgs[i], actualMsgs[i],
                String.format("メッセージが一致しません: %s", expectedMsgs[i]));

        }

        // ログの数のチェック
        Assertions.assertEquals(expectedMsgs.length, actualMsgs.length, "ログの数が一致しません");

    }

}
