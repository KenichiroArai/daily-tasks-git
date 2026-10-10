package io.github.kenichiroarai.dailytasks.carryover.domain.model;

/**
 * 日々のタスク Issue（取得したままの内容）<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class DailyTaskIssue {

    /**
     * Issue 番号
     */
    private final int number;

    /**
     * Issue タイトル
     */
    private final String title;

    /**
     * Issue の状態（open / closed）
     */
    private final String state;

    /**
     * Issue の更新日時（ISO-8601）
     */
    private final String updatedAt;

    /**
     * Issue 本文
     */
    private final String body;

    /**
     * コンストラクタ<br>
     *
     * @param number
     *                  Issue 番号
     * @param title
     *                  Issue タイトル
     * @param state
     *                  Issue の状態（open / closed）
     * @param updatedAt
     *                  Issue の更新日時（ISO-8601）
     * @param body
     *                  Issue 本文
     */
    public DailyTaskIssue(final int number, final String title, final String state, final String updatedAt,
        final String body) {

        this.number = number;
        this.title = title;
        this.state = state;
        this.updatedAt = updatedAt;
        this.body = body;

    }

    /**
     * Issue 本文を返す<br>
     *
     * @return Issue 本文
     */
    public String getBody() {

        final String result = this.body;
        return result;

    }

    /**
     * Issue 番号を返す<br>
     *
     * @return Issue 番号
     */
    public int getNumber() {

        final int result = this.number;
        return result;

    }

    /**
     * Issue の状態を返す<br>
     *
     * @return Issue の状態（open / closed）
     */
    public String getState() {

        final String result = this.state;
        return result;

    }

    /**
     * Issue タイトルを返す<br>
     *
     * @return Issue タイトル
     */
    public String getTitle() {

        final String result = this.title;
        return result;

    }

    /**
     * Issue の更新日時を返す<br>
     *
     * @return Issue の更新日時（ISO-8601）
     */
    public String getUpdatedAt() {

        final String result = this.updatedAt;
        return result;

    }

}
