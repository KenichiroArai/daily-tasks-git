package io.github.kenichiroarai.dailytasks.carryover.domain.logic.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.application.logic.CarryoverIssueLogic;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSourceModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailyTaskIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DefaultMinutesModel;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.logic.CarryoverDtoConvertLogic;
import io.github.kenichiroarai.dailytasks.carryover.domain.repository.api.GitHubIssueApi;
import io.github.kenichiroarai.dailytasks.carryover.domain.repository.dao.CarryoverDataDao;
import io.github.kenichiroarai.dailytasks.carryover.domain.repository.dao.DefaultMinutesDao;

/**
 * 持ち越しのデータの取得・保存ロジックの実装<br>
 * <p>
 * application 層のデータの取得・保存ロジックを実装する。application 層のモデルと domain 層の DTO を {@link CarryoverDtoConvertLogic}
 * で変換し、domain 層の外部 API・DAO（実装は repository 層）に渡す。データの取得元と保存先は、domain 層の型（接続設定の DTO・パス）に変換して渡す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
public class CarryoverIssueLogicImpl implements CarryoverIssueLogic {

    /** GitHub の Issue の取得 */
    private final GitHubIssueApi gitHubIssueApi;

    /** JSON ファイルの読み書き */
    private final CarryoverDataDao carryoverDataDao;

    /** 標準時間の設定の読み込み */
    private final DefaultMinutesDao defaultMinutesDao;

    /** DTO とモデルの変換 */
    private final CarryoverDtoConvertLogic dtoConvertLogic;

    /**
     * コンストラクタ<br>
     *
     * @param gitHubIssueApi
     *                          GitHub の Issue の取得
     * @param carryoverDataDao
     *                          JSON ファイルの読み書き
     * @param defaultMinutesDao
     *                          標準時間の設定の読み込み
     * @param dtoConvertLogic
     *                          DTO とモデルの変換
     */
    public CarryoverIssueLogicImpl(final GitHubIssueApi gitHubIssueApi,
        final CarryoverDataDao carryoverDataDao, final DefaultMinutesDao defaultMinutesDao,
        final CarryoverDtoConvertLogic dtoConvertLogic) {

        this.gitHubIssueApi = gitHubIssueApi;
        this.carryoverDataDao = carryoverDataDao;
        this.defaultMinutesDao = defaultMinutesDao;
        this.dtoConvertLogic = dtoConvertLogic;

    }

    /**
     * すべての日々のタスク Issue を取得する<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return 日々のタスク Issue（作成順）
     *
     * @throws IOException
     *                     取得に失敗した場合
     */
    @Override
    public List<DailyTaskIssueModel> fetchAllIssues(final CarryoverSourceModel source) throws IOException {

        final GitHubSettingsDto         settings = this.dtoConvertLogic.toGitHubSettingsDto(source);
        final List<DailyTaskIssueModel> result   = this.gitHubIssueApi.fetchAllIssues(settings).stream()
            .map(this.dtoConvertLogic::toDailyTaskIssue).toList();
        return result;

    }

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return 項目ごとの標準時間。設定がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public DefaultMinutesModel loadDefaultMinutes(final CarryoverSourceModel source) throws IOException {

        final Map<String, Double> minutesByName = this.defaultMinutesDao.load(source.getDefaultMinutesFile());
        final DefaultMinutesModel result        = this.dtoConvertLogic.toDefaultMinutes(minutesByName);
        return result;

    }

    /**
     * 保存済みの持ち越しの解析結果を読み込む<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return Issue 番号と解析結果の対応。保存されていない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public Map<Integer, CarryoverIssueModel> loadIssues(final CarryoverSourceModel source) throws IOException {

        final Map<Integer, CarryoverIssueModel> result = new TreeMap<>();

        for (final Map.Entry<Integer, CarryoverIssueDto> entry : this.carryoverDataDao
            .loadIssues(source.getDataDir()).entrySet()) {

            result.put(entry.getKey(), this.dtoConvertLogic.toCarryoverIssue(entry.getValue()));

        }

        return result;

    }

    /**
     * 持ち越しの解析結果を保存する<br>
     *
     * @param source
     *               データの取得元と保存先
     * @param issue
     *               持ち越しの解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveIssue(final CarryoverSourceModel source, final CarryoverIssueModel issue) throws IOException {

        this.carryoverDataDao.saveIssue(source.getDataDir(), this.dtoConvertLogic.toCarryoverIssueDto(issue));

    }

    /**
     * 画面用の集計を保存する<br>
     *
     * @param source
     *                データの取得元と保存先
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveSummary(final CarryoverSourceModel source, final CarryoverSummaryModel summary) throws IOException {

        this.carryoverDataDao.saveSummary(source.getDataDir(), this.dtoConvertLogic.toCarryoverSummaryDto(summary));

    }

}
