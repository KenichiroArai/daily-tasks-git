package io.github.kenichiroarai.dailytasks.carryover.domain.model;

import java.util.List;

/**
 * 持ち越しの解析結果（Issue 単位）<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverIssue {

    /**
     * Issue 番号
     */
    private final int number;

    /**
     * Issue タイトル
     */
    private final String title;

    /**
     * その日の日付（yyyy-MM-dd）。タイトルから取得できない場合は null
     */
    private final String date;

    /**
     * Issue の状態（open / closed）
     */
    private final String state;

    /**
     * Issue の更新日時（ISO-8601）
     */
    private final String updatedAt;

    /**
     * 本文にあった対象セクション名
     */
    private final List<String> sections;

    /**
     * 本文の「残：N」の値。記載がない場合は null
     */
    private final Integer declaredCount;

    /**
     * 持ち越し項目
     */
    private final List<CarryoverItem> items;

    /**
     * コンストラクタ<br>
     *
     * @param number
     *                      Issue 番号
     * @param title
     *                      Issue タイトル
     * @param date
     *                      その日の日付（yyyy-MM-dd）。タイトルから取得できない場合は null
     * @param state
     *                      Issue の状態（open / closed）
     * @param updatedAt
     *                      Issue の更新日時（ISO-8601）
     * @param sections
     *                      本文にあった対象セクション名
     * @param declaredCount
     *                      本文の「残：N」の値。記載がない場合は null
     * @param items
     *                      持ち越し項目
     */
    public CarryoverIssue(final int number, final String title, final String date, final String state,
        final String updatedAt, final List<String> sections, final Integer declaredCount,
        final List<CarryoverItem> items) {

        this.number = number;
        this.title = title;
        this.date = date;
        this.state = state;
        this.updatedAt = updatedAt;
        this.sections = List.copyOf(sections);
        this.declaredCount = declaredCount;
        this.items = List.copyOf(items);

    }

    /**
     * 持ち越し項目の件数を返す<br>
     *
     * @return 持ち越し項目の件数
     */
    public int getCount() {

        final int result = this.items.size();
        return result;

    }

    /**
     * その日の日付を返す<br>
     *
     * @return その日の日付（yyyy-MM-dd）。タイトルから取得できない場合は null
     */
    public String getDate() {

        final String result = this.date;
        return result;

    }

    /**
     * 本文の「残：N」の値を返す<br>
     *
     * @return 「残：N」の値。記載がない場合は null
     */
    public Integer getDeclaredCount() {

        final Integer result = this.declaredCount;
        return result;

    }

    /**
     * 持ち越し項目を返す<br>
     *
     * @return 持ち越し項目
     */
    public List<CarryoverItem> getItems() {

        final List<CarryoverItem> result = this.items;
        return result;

    }

    /**
     * 持ち越し項目の残り時間（分）の合計を返す<br>
     *
     * @return 残り時間（分）の合計
     */
    public double getMinutes() {

        final double result = this.items.stream().mapToDouble(CarryoverItem::getMinutes).sum();
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
     * 本文にあった対象セクション名を返す<br>
     *
     * @return 対象セクション名
     */
    public List<String> getSections() {

        final List<String> result = this.sections;
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

    /**
     * 対象セクションがあるかを返す<br>
     *
     * @return true：対象セクションがある、false：対象セクションがない
     */
    public boolean hasSection() {

        final boolean result = !this.sections.isEmpty();
        return result;

    }

    /**
     * オープン中かを返す<br>
     *
     * @return true：オープン中、false：クローズ済み
     */
    public boolean isOpen() {

        final boolean result = "open".equals(this.state);
        return result;

    }

}
