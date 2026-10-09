package io.github.kenichiroarai.dailytasks.carryover.application.service.impl;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSettings;
import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.CarryoverAggregator;
import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.impl.CarryoverAggregatorImpl;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.CarryoverParser;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.impl.CarryoverParserImpl;
import io.github.kenichiroarai.dailytasks.carryover.domain.service.CarryoverIssueService;
import io.github.kenichiroarai.dailytasks.carryover.domain.service.impl.CarryoverIssueServiceImpl;

/**
 * 持ち越しの収集・集計サービスの実装<br>
 * <p>
 * domain 層のインタフェースだけを使って、取得・解析・保存・集計の流れを組み立てる。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class CarryoverServiceImpl implements CarryoverService {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CarryoverServiceImpl.class);

    /**
     * 差分モードで更新日時に関係なく解析し直す最新の Issue の件数
     */
    private static final int RECENT_COUNT = 10;

    /**
     * 持ち越しのデータの取得・保存
     */
    private final CarryoverIssueService carryoverIssueService;

    /**
     * Issue 本文の解析
     */
    private final CarryoverParser carryoverParser;

    /**
     * 日別の集計
     */
    private final CarryoverAggregator carryoverAggregator;

    /**
     * コンストラクタ<br>
     * <p>
     * 設定を domain 層のデータの取得元に変換し、domain 層の実装を生成する。標準時間はこの時点で読み込む。
     * </p>
     *
     * @param settings
     *                 持ち越しの収集・集計の設定
     *
     * @throws IOException
     *                     標準時間の読み込みに失敗した場合
     */
    public CarryoverServiceImpl(final CarryoverSettings settings) throws IOException {

        this.carryoverIssueService = new CarryoverIssueServiceImpl(CarryoverServiceImpl.toSource(settings));
        this.carryoverParser = new CarryoverParserImpl(this.carryoverIssueService.loadDefaultMinutes());
        this.carryoverAggregator = new CarryoverAggregatorImpl();

    }

    /**
     * domain 層のサービスを指定するコンストラクタ<br>
     *
     * @param carryoverIssueService
     *                              持ち越しのデータの取得・保存
     * @param carryoverParser
     *                              Issue 本文の解析
     * @param carryoverAggregator
     *                              日別の集計
     */
    public CarryoverServiceImpl(final CarryoverIssueService carryoverIssueService,
        final CarryoverParser carryoverParser, final CarryoverAggregator carryoverAggregator) {

        this.carryoverIssueService = carryoverIssueService;
        this.carryoverParser = carryoverParser;
        this.carryoverAggregator = carryoverAggregator;

    }

    /**
     * Issue を取得して解析し、Issue ごとの JSON と画面用の集計を出力する<br>
     * <p>
     * 差分モードでは、Issue 番号が大きい順の最新 {@value #RECENT_COUNT} 件を除き、保存済みの JSON と更新日時が同じ Issue は解析しない。
     * </p>
     *
     * @param full
     *             true：全件モード（すべての Issue を解析し直す）、false：差分モード
     *
     * @return 解析して保存した Issue の件数
     *
     * @throws IOException
     *                     取得、読み込みまたは書き込みに失敗した場合
     */
    @Override
    public int collect(final boolean full) throws IOException {

        int result = 0;

        /* 保存済みの解析結果の読み込み */
        final Map<Integer, CarryoverIssue> stored = this.carryoverIssueService.loadIssues();
        final Map<Integer, CarryoverIssue> all = new TreeMap<>(stored);

        /* Issue の取得と解析 */
        final List<DailyTaskIssue> remoteIssues = this.carryoverIssueService.fetchAllIssues();
        final int recentFrom = CarryoverServiceImpl.recentFrom(remoteIssues, CarryoverServiceImpl.RECENT_COUNT);

        for (final DailyTaskIssue remoteIssue : remoteIssues) {

            final Integer number = Integer.valueOf(remoteIssue.getNumber());

            if (!CarryoverServiceImpl.needsUpdate(full, stored.get(number), remoteIssue, recentFrom)) {

                continue;

            }

            final CarryoverIssue parsed = this.carryoverParser.parse(remoteIssue);
            this.carryoverIssueService.saveIssue(parsed);
            all.put(number, parsed);
            result++;

        }

        /* 画面用の集計の出力 */
        final CarryoverSummary summary = this.carryoverAggregator.aggregate(all.values());
        this.carryoverIssueService.saveSummary(summary);

        /* 結果のログ */
        CarryoverServiceImpl.LOGGER.info("モード={}、取得={} 件、解析・保存={} 件、集計={} 日", full ? "全件" : "差分",
            remoteIssues.size(), result, summary.getDays().size());
        CarryoverServiceImpl.LOGGER.info("標準時間で補完した行={} 件、標準時間が未登録の行={} 件",
            CarryoverServiceImpl.countBySource(all, MinutesSource.DEFAULT),
            CarryoverServiceImpl.countBySource(all, MinutesSource.UNKNOWN));

        return result;

    }

    /**
     * application 層の設定を domain 層のデータの取得元に変換する<br>
     *
     * @param settings
     *                 持ち越しの収集・集計の設定
     *
     * @return データの取得元と保存先
     */
    private static CarryoverSource toSource(final CarryoverSettings settings) {

        final CarryoverSource result = new CarryoverSource(settings.getRepository(), settings.getToken(),
            settings.getDataDir(), settings.getDefaultMinutesFile());
        return result;

    }

    /**
     * Issue を解析し直す必要があるかを返す<br>
     *
     * @param full
     *                    全件モードか
     * @param stored
     *                    保存済みの解析結果。未保存の場合は null
     * @param remoteIssue
     *                    取得した Issue
     * @param recentFrom
     *                    更新日時に関係なく解析し直す Issue 番号の下限
     *
     * @return true：解析し直す、false：保存済みの解析結果を使う
     */
    private static boolean needsUpdate(final boolean full, final CarryoverIssue stored, final DailyTaskIssue remoteIssue,
        final int recentFrom) {

        boolean result = true;

        if (full) {

            return result;

        }

        if (stored == null) {

            return result;

        }

        if (remoteIssue.getNumber() >= recentFrom) {

            return result;

        }

        result = !remoteIssue.getUpdatedAt().equals(stored.getUpdatedAt());
        return result;

    }

    /**
     * Issue 番号が大きい順に指定件数を選んだときの、最小の Issue 番号を返す<br>
     *
     * @param issues
     *               取得した Issue
     * @param count
     *               件数
     *
     * @return 最小の Issue 番号。Issue がない場合は 0
     */
    private static int recentFrom(final List<DailyTaskIssue> issues, final int count) {

        final int result = issues.stream().map(issue -> Integer.valueOf(issue.getNumber()))
            .sorted(Comparator.reverseOrder()).limit(count).min(Comparator.naturalOrder()).orElse(Integer.valueOf(0))
            .intValue();
        return result;

    }

    /**
     * 残り時間の取得元ごとの行数を返す<br>
     *
     * @param issues
     *               解析結果
     * @param source
     *               残り時間の取得元
     *
     * @return 行数
     */
    private static long countBySource(final Map<Integer, CarryoverIssue> issues, final MinutesSource source) {

        final long result = issues.values().stream().flatMap(issue -> issue.getItems().stream())
            .filter(item -> item.getMinutesSource() == source).count();
        return result;

    }

}
