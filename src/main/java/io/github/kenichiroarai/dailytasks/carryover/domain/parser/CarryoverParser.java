package io.github.kenichiroarai.dailytasks.carryover.domain.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;

/**
 * 日々のタスク Issue から持ち越し項目を解析する<br>
 * <p>
 * 対象セクションは「負債」「繰り越し」「持ち越し」。未チェック・チェック済みの両方を対象とする。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverParser {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CarryoverParser.class);

    /**
     * 対象セクション名
     */
    private static final Set<String> TARGET_SECTIONS = Set.of("負債", "繰り越し", "持ち越し");

    /**
     * 項目名が空の場合の名前
     */
    private static final String UNKNOWN_NAME = "不明";

    /**
     * タイトルの日付（YYYY年MM月DD日）
     */
    private static final Pattern TITLE_DATE = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日");

    /**
     * 見出し（## 見出し）
     */
    private static final Pattern HEADING = Pattern.compile("^##(?!#)\\s*(.*?)\\s*$");

    /**
     * 残数（残：N）
     */
    private static final Pattern DECLARED_COUNT = Pattern.compile("^\\s*残\\s*[：:]\\s*(\\d+)\\s*$");

    /**
     * チェックボックスの行（- [ ] 内容 / - [x] 内容）
     */
    private static final Pattern CHECKBOX = Pattern.compile("^\\s*[-*]\\s*\\[([ xX])\\]\\s*(.*?)\\s*$");

    /**
     * 持ち越し元の日付を含む内容（項目名YYYY/MM/DD残り）
     */
    private static final Pattern DATED_CONTENT = Pattern.compile("^(.*?)(\\d{4})/(\\d{1,2})/(\\d{1,2})(.*)$");

    /**
     * 括弧内の時間表記
     */
    private static final Pattern PARENTHESES = Pattern.compile("[（(]([^）)]*)[）)]");

    /**
     * 残り時間の表記（残り時間：N分 / 残りN分 / 先行分残りN分）
     */
    private static final Pattern REMAINING_MINUTES = Pattern
        .compile("残り(?:時間)?\\s*[：:]?\\s*(\\d+(?:\\.\\d+)?)\\s*分");

    /**
     * 時間だけの表記（N分）
     */
    private static final Pattern SIMPLE_MINUTES = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*分\\s*$");

    /**
     * 項目名の末尾の括弧書き
     */
    private static final Pattern TRAILING_PARENTHESES = Pattern.compile("[（(][^（()）]*[）)]$");

    /**
     * 項目名の末尾の開き括弧
     */
    private static final Pattern TRAILING_OPEN_PARENTHESES = Pattern.compile("[（(]+$");

    /**
     * 項目ごとの標準時間
     */
    private final DefaultMinutes defaultMinutes;

    /**
     * コンストラクタ<br>
     *
     * @param defaultMinutes
     *                       項目ごとの標準時間
     */
    public CarryoverParser(final DefaultMinutes defaultMinutes) {

        this.defaultMinutes = defaultMinutes;

    }

    /**
     * Issue を解析する<br>
     *
     * @param issue
     *              日々のタスク Issue
     *
     * @return 持ち越しの解析結果
     */
    public CarryoverIssue parse(final DailyTaskIssue issue) {

        CarryoverIssue result = null;

        /* 日付の取得 */
        final String date = CarryoverParser.parseTitleDate(issue.getTitle());

        if (date == null) {

            CarryoverParser.LOGGER.warn("#{} タイトルから日付を取得できません: {}", issue.getNumber(), issue.getTitle());

        }

        /* 本文の解析 */
        final List<String> sections = new ArrayList<>();
        final List<CarryoverItem> items = new ArrayList<>();
        Integer declaredCount = null;
        String currentSection = "";

        for (final String line : CarryoverParser.splitLines(issue.getBody())) {

            // 見出しの場合は対象セクションかを判定する
            final Matcher headingMatcher = CarryoverParser.HEADING.matcher(line);

            if (headingMatcher.matches()) {

                currentSection = headingMatcher.group(1);
                CarryoverParser.addSection(sections, currentSection);
                continue;

            }

            if (!CarryoverParser.TARGET_SECTIONS.contains(currentSection)) {

                continue;

            }

            // 残数の行
            final Matcher declaredMatcher = CarryoverParser.DECLARED_COUNT.matcher(line);

            if (declaredMatcher.matches()) {

                declaredCount = Integer.valueOf(declaredMatcher.group(1));
                continue;

            }

            // チェックボックスの行
            final Matcher checkboxMatcher = CarryoverParser.CHECKBOX.matcher(line);

            if (!checkboxMatcher.matches()) {

                CarryoverParser.warnUnexpectedLine(issue.getNumber(), line);
                continue;

            }

            final boolean checked = !" ".equals(checkboxMatcher.group(1));
            items.add(this.parseItem(issue.getNumber(), currentSection, checked, checkboxMatcher.group(2), line));

        }

        /* 残数の検証 */
        CarryoverParser.verifyDeclaredCount(issue.getNumber(), declaredCount, items.size());

        result = new CarryoverIssue(issue.getNumber(), issue.getTitle(), date, issue.getState(), issue.getUpdatedAt(),
            sections, declaredCount, items);
        return result;

    }

    /**
     * 持ち越し項目の内容を解析する<br>
     *
     * @param number
     *                Issue 番号
     * @param section
     *                セクション名
     * @param checked
     *                チェック済みか
     * @param content
     *                チェックボックスの後ろの内容
     * @param raw
     *                元の行
     *
     * @return 持ち越し項目
     */
    CarryoverItem parseItem(final int number, final String section, final boolean checked, final String content,
        final String raw) {

        CarryoverItem result = null;

        /* 項目名・持ち越し元の日付・残りの部分の分割 */
        String namePart = content;
        String originDate = null;
        String rest = content;
        final Matcher datedMatcher = CarryoverParser.DATED_CONTENT.matcher(content);

        if (datedMatcher.matches()) {

            namePart = datedMatcher.group(1);
            originDate = CarryoverParser.formatDate(datedMatcher.group(2), datedMatcher.group(3),
                datedMatcher.group(4));
            rest = datedMatcher.group(5);

        } else {

            CarryoverParser.LOGGER.warn("#{} 持ち越し元の日付がありません: {}", number, raw);

        }

        final String name = CarryoverParser.normalizeName(namePart);

        if (UNKNOWN_NAME.equals(name)) {

            CarryoverParser.LOGGER.warn("#{} 項目名を取得できません: {}", number, raw);

        }

        /* 残り時間の取得 */
        final Double parsedMinutes = CarryoverParser.parseMinutes(rest);

        if (parsedMinutes != null) {

            result = new CarryoverItem(name, originDate, checked, parsedMinutes.doubleValue(), MinutesSource.PARSED,
                section, raw);
            return result;

        }

        if (!rest.isBlank()) {

            CarryoverParser.LOGGER.warn("#{} 時間表記を解釈できないため標準時間で補完します: {}", number, raw);

        }

        /* 標準時間での補完 */
        final Double defaultValue = this.defaultMinutes.find(name);

        if (defaultValue == null) {

            CarryoverParser.LOGGER.warn("#{} 標準時間が未登録のため 0 分とします: {}", number, name);
            result = new CarryoverItem(name, originDate, checked, 0, MinutesSource.UNKNOWN, section, raw);
            return result;

        }

        result = new CarryoverItem(name, originDate, checked, defaultValue.doubleValue(), MinutesSource.DEFAULT,
            section, raw);
        return result;

    }

    /**
     * タイトルから日付を取得する<br>
     *
     * @param title
     *              Issue タイトル
     *
     * @return 日付（yyyy-MM-dd）。取得できない場合は null
     */
    static String parseTitleDate(final String title) {

        String result = null;

        if (title == null) {

            return result;

        }

        final Matcher matcher = CarryoverParser.TITLE_DATE.matcher(title);

        if (!matcher.find()) {

            return result;

        }

        result = CarryoverParser.formatDate(matcher.group(1), matcher.group(2), matcher.group(3));
        return result;

    }

    /**
     * 時間表記から残り時間（分）を取得する<br>
     * <p>
     * 括弧内の「残り時間：N分」「残りN分」「先行分残りN分」「N分」を解釈する。
     * </p>
     *
     * @param text
     *             持ち越し元の日付より後ろの部分
     *
     * @return 残り時間（分）。解釈できない場合は null
     */
    static Double parseMinutes(final String text) {

        Double result = null;

        /* 括弧内の取得 */
        final Matcher parenthesesMatcher = CarryoverParser.PARENTHESES.matcher(text);

        if (!parenthesesMatcher.find()) {

            return result;

        }

        final String inner = parenthesesMatcher.group(1);

        /* 残り時間の表記 */
        final Matcher remainingMatcher = CarryoverParser.REMAINING_MINUTES.matcher(inner);

        if (remainingMatcher.find()) {

            result = Double.valueOf(remainingMatcher.group(1));
            return result;

        }

        /* 時間だけの表記 */
        final Matcher simpleMatcher = CarryoverParser.SIMPLE_MINUTES.matcher(inner);

        if (!simpleMatcher.matches()) {

            return result;

        }

        result = Double.valueOf(simpleMatcher.group(1));
        return result;

    }

    /**
     * 項目名を正規化する<br>
     * <p>
     * 末尾の括弧書き（例: 「（15分）」）や閉じられていない開き括弧を取り除く。
     * </p>
     *
     * @param name
     *             項目名
     *
     * @return 正規化した項目名。空になる場合は「不明」
     */
    static String normalizeName(final String name) {

        String result = name.strip();
        String previous;

        do {

            previous = result;
            result = CarryoverParser.TRAILING_PARENTHESES.matcher(result).replaceAll("").strip();
            result = CarryoverParser.TRAILING_OPEN_PARENTHESES.matcher(result).replaceAll("").strip();

        } while (!result.equals(previous));

        if (!result.isEmpty()) {

            return result;

        }

        result = CarryoverParser.UNKNOWN_NAME;
        return result;

    }

    /**
     * 本文を行に分割する<br>
     *
     * @param body
     *             Issue 本文。null の場合は空とみなす
     *
     * @return 行
     */
    static List<String> splitLines(final String body) {

        List<String> result = List.of();

        if (body == null) {

            return result;

        }

        result = List.of(body.split("\\R", -1));
        return result;

    }

    /**
     * 対象セクションの場合にセクション名を追加する<br>
     *
     * @param sections
     *                 対象セクション名の一覧
     * @param section
     *                 セクション名
     */
    static void addSection(final List<String> sections, final String section) {

        if (!CarryoverParser.TARGET_SECTIONS.contains(section)) {

            return;

        }

        if (sections.contains(section)) {

            return;

        }

        sections.add(section);

    }

    /**
     * 対象セクション内の想定外の行を警告する<br>
     * <p>
     * 空行と区切り線（---）は警告しない。
     * </p>
     *
     * @param number
     *               Issue 番号
     * @param line
     *               行
     */
    static void warnUnexpectedLine(final int number, final String line) {

        final String stripped = line.strip();

        if (stripped.isEmpty()) {

            return;

        }

        if (stripped.matches("^-{3,}$")) {

            return;

        }

        CarryoverParser.LOGGER.warn("#{} 想定外の行です: {}", number, line);

    }

    /**
     * 本文の残数と解析件数の食い違いを警告する<br>
     *
     * @param number
     *                      Issue 番号
     * @param declaredCount
     *                      本文の「残：N」の値。記載がない場合は null
     * @param parsedCount
     *                      解析件数
     */
    static void verifyDeclaredCount(final int number, final Integer declaredCount, final int parsedCount) {

        if (declaredCount == null) {

            return;

        }

        if (declaredCount.intValue() == parsedCount) {

            return;

        }

        CarryoverParser.LOGGER.warn("#{} 残数と解析件数が一致しません: 残={}, 解析={}", number, declaredCount, parsedCount);

    }

    /**
     * 年・月・日を yyyy-MM-dd 形式にする<br>
     *
     * @param year
     *              年
     * @param month
     *              月
     * @param day
     *              日
     *
     * @return yyyy-MM-dd 形式の日付
     */
    static String formatDate(final String year, final String month, final String day) {

        final String result = String.format("%04d-%02d-%02d", Integer.valueOf(year), Integer.valueOf(month),
            Integer.valueOf(day));
        return result;

    }

}
