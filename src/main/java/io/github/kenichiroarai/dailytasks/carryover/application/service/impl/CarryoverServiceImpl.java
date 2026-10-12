package io.github.kenichiroarai.dailytasks.carryover.application.service.impl;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.kenichiroarai.dailytasks.carryover.application.logic.CarryoverAggregateLogic;
import io.github.kenichiroarai.dailytasks.carryover.application.logic.CarryoverIssueLogic;
import io.github.kenichiroarai.dailytasks.carryover.application.logic.CarryoverParseLogic;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSourceModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailyTaskIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DefaultMinutesModel;
import io.github.kenichiroarai.dailytasks.carryover.application.types.MinutesSourceTypes;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.MessageLogic;
import io.github.kenichiroarai.dailytasks.carryover.presentation.model.CarryoverPropertiesModel;
import io.github.kenichiroarai.dailytasks.carryover.presentation.service.CarryoverService;

/**
 * 持ち越しの収集・集計サービスの実装<br>
 * <p>
 * presentation 層のサービスを実装する。application 層のインタフェース（実装は domain 層）だけを使って、取得・解析・保存・集計の流れを組み立てる。
 * 設定は呼び出しごとに受け取り、application 層のデータの取得元に変換して渡す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Service
public class CarryoverServiceImpl implements CarryoverService {

    /** ロガー */
    private static final Logger LOGGER = LoggerFactory.getLogger(CarryoverServiceImpl.class);

    /** メッセージのキー：収集の結果 */
    private static final String MSG_COLLECT_RESULT = "carryover.service.collectResult";

    /** メッセージのキー：残り時間の取得元ごとの行数 */
    private static final String MSG_MINUTES_SOURCE_RESULT = "carryover.service.minutesSourceResult";

    /** メッセージのキー：全件モードの名前 */
    private static final String MSG_MODE_FULL = "carryover.service.modeFull";

    /** メッセージのキー：差分モードの名前 */
    private static final String MSG_MODE_DIFF = "carryover.service.modeDiff";

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
    private static long countBySource(final Map<Integer, CarryoverIssueModel> issues, final MinutesSourceTypes source) {

        final long result = issues.values().stream().flatMap(issue -> issue.getItems().stream())
            .filter(item -> item.getMinutesSource() == source).count();
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
    private static boolean needsUpdate(final boolean full, final CarryoverIssueModel stored,
        final DailyTaskIssueModel remoteIssue, final int recentFrom) {

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
    private static int recentFrom(final List<DailyTaskIssueModel> issues, final int count) {

        final int result = issues.stream().map(DailyTaskIssueModel::getNumber).sorted(Comparator.reverseOrder()).limit(count)
            .min(Comparator.naturalOrder()).orElse(0);
        return result;

    }

    /**
     * presentation 層から引き継いだ設定を application 層のデータの取得元に変換する<br>
     *
     * @param properties
     *                   持ち越しの収集の設定
     *
     * @return データの取得元と保存先
     */
    private static CarryoverSourceModel toSource(final CarryoverPropertiesModel properties) {

        final CarryoverSourceModel result = new CarryoverSourceModel(properties.getRepository(), properties.getToken(),
            properties.getDataDir(), properties.getDefaultMinutesFile());
        return result;

    }

    /** 持ち越しのデータの取得・保存 */
    private final CarryoverIssueLogic carryoverIssueLogic;

    /** Issue 本文の解析 */
    private final CarryoverParseLogic carryoverParseLogic;

    /** 日別の集計 */
    private final CarryoverAggregateLogic carryoverAggregateLogic;

    /** メッセージの取得 */
    private final MessageLogic messageLogic;

    /**
     * コンストラクタ<br>
     *
     * @param carryoverIssueLogic
     *                                持ち越しのデータの取得・保存
     * @param carryoverParseLogic
     *                                Issue 本文の解析
     * @param carryoverAggregateLogic
     *                                日別の集計
     * @param messageLogic
     *                                メッセージの取得
     */
    public CarryoverServiceImpl(final CarryoverIssueLogic carryoverIssueLogic,
        final CarryoverParseLogic carryoverParseLogic, final CarryoverAggregateLogic carryoverAggregateLogic,
        final MessageLogic messageLogic) {

        this.carryoverIssueLogic = carryoverIssueLogic;
        this.carryoverParseLogic = carryoverParseLogic;
        this.carryoverAggregateLogic = carryoverAggregateLogic;
        this.messageLogic = messageLogic;

    }

    /**
     * Issue を取得して解析し、Issue ごとの JSON と画面用の集計を出力する<br>
     * <p>
     * 差分モードでは、Issue 番号が大きい順の最新の指定件数（設定の recentCount）を除き、保存済みの JSON と更新日時が同じ Issue は解析しない。
     * </p>
     *
     * @param properties
     *                   持ち越しの収集の設定
     * @param full
     *                   true：全件モード（すべての Issue を解析し直す）、false：差分モード
     *
     * @return 解析して保存した Issue の件数
     *
     * @throws IOException
     *                     取得、読み込みまたは書き込みに失敗した場合
     */
    @Override
    public int collect(final CarryoverPropertiesModel properties, final boolean full) throws IOException {

        int result = 0;

        /* 設定の変換と標準時間の読み込み */
        final CarryoverSourceModel source         = CarryoverServiceImpl.toSource(properties);
        final DefaultMinutesModel  defaultMinutes = this.carryoverIssueLogic.loadDefaultMinutes(source);

        /* 保存済みの解析結果の読み込み */
        final Map<Integer, CarryoverIssueModel> stored = this.carryoverIssueLogic.loadIssues(source);
        final Map<Integer, CarryoverIssueModel> all    = new TreeMap<>(stored);

        /* Issue の取得と解析 */
        final List<DailyTaskIssueModel> remoteIssues = this.carryoverIssueLogic.fetchAllIssues(source);
        final int                       recentFrom   = CarryoverServiceImpl.recentFrom(remoteIssues,
            properties.getRecentCount());

        for (final DailyTaskIssueModel remoteIssue : remoteIssues) {

            final Integer number = remoteIssue.getNumber();

            if (!CarryoverServiceImpl.needsUpdate(full, stored.get(number), remoteIssue, recentFrom)) {

                continue;

            }

            final CarryoverIssueModel parsed = this.carryoverParseLogic.parse(remoteIssue, defaultMinutes);
            this.carryoverIssueLogic.saveIssue(source, parsed);
            all.put(number, parsed);
            result++;

        }

        /* 画面用の集計の出力 */
        final CarryoverSummaryModel summary = this.carryoverAggregateLogic.aggregate(all.values());
        this.carryoverIssueLogic.saveSummary(source, summary);

        /* 結果のログ */
        final String collectMessage = this.messageLogic.get(CarryoverServiceImpl.MSG_COLLECT_RESULT);
        final String modeName       = this.modeName(full);
        CarryoverServiceImpl.LOGGER.info(collectMessage, modeName, remoteIssues.size(), result,
            summary.getDays().size());

        final String minutesSourceMessage = this.messageLogic.get(CarryoverServiceImpl.MSG_MINUTES_SOURCE_RESULT);
        final long   defaultCount         = CarryoverServiceImpl.countBySource(all, MinutesSourceTypes.DEFAULT);
        final long   unknownCount         = CarryoverServiceImpl.countBySource(all, MinutesSourceTypes.UNKNOWN);
        CarryoverServiceImpl.LOGGER.info(minutesSourceMessage, defaultCount, unknownCount);

        return result;

    }

    /**
     * ログに出すモードの名前を返す<br>
     *
     * @param full
     *             全件モードか
     *
     * @return モードの名前（全件 / 差分）
     */
    private String modeName(final boolean full) {

        String result = this.messageLogic.get(CarryoverServiceImpl.MSG_MODE_DIFF);

        if (!full) {

            return result;

        }

        result = this.messageLogic.get(CarryoverServiceImpl.MSG_MODE_FULL);
        return result;

    }

}
