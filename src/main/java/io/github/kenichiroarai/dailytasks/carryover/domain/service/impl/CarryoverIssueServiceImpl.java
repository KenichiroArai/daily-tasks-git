package io.github.kenichiroarai.dailytasks.carryover.domain.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import io.github.kenichiroarai.dailytasks.carryover.domain.converter.CarryoverDtoConverter;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.service.CarryoverIssueService;
import io.github.kenichiroarai.dailytasks.carryover.repository.CarryoverDataRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.DefaultMinutesRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;

/**
 * 持ち越しのデータの取得・保存サービスの実装<br>
 * <p>
 * repository 層の DTO と domain 層のモデルを {@link CarryoverDtoConverter} で変換する。データの取得元と保存先は、repository 層の型（接続設定の
 * DTO・パス）に変換して渡す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Service
public class CarryoverIssueServiceImpl implements CarryoverIssueService {

    /**
     * GitHub の Issue の取得
     */
    private final GitHubIssueRepository gitHubIssueRepository;

    /**
     * JSON ファイルの読み書き
     */
    private final CarryoverDataRepository carryoverDataRepository;

    /**
     * 標準時間の設定の読み込み
     */
    private final DefaultMinutesRepository defaultMinutesRepository;

    /**
     * DTO とモデルの変換
     */
    private final CarryoverDtoConverter converter;

    /**
     * コンストラクタ<br>
     *
     * @param gitHubIssueRepository
     *                                 GitHub の Issue の取得
     * @param carryoverDataRepository
     *                                 JSON ファイルの読み書き
     * @param defaultMinutesRepository
     *                                 標準時間の設定の読み込み
     * @param converter
     *                                 DTO とモデルの変換
     */
    public CarryoverIssueServiceImpl(final GitHubIssueRepository gitHubIssueRepository,
        final CarryoverDataRepository carryoverDataRepository, final DefaultMinutesRepository defaultMinutesRepository,
        final CarryoverDtoConverter converter) {

        this.gitHubIssueRepository = gitHubIssueRepository;
        this.carryoverDataRepository = carryoverDataRepository;
        this.defaultMinutesRepository = defaultMinutesRepository;
        this.converter = converter;

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
    public List<DailyTaskIssue> fetchAllIssues(final CarryoverSource source) throws IOException {

        final GitHubSettingsDto    settings = this.converter.toGitHubSettingsDto(source);
        final List<DailyTaskIssue> result   = this.gitHubIssueRepository.fetchAllIssues(settings).stream()
            .map(this.converter::toDailyTaskIssue).toList();
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
    public DefaultMinutes loadDefaultMinutes(final CarryoverSource source) throws IOException {

        final Map<String, Double> minutesByName = this.defaultMinutesRepository.load(source.getDefaultMinutesFile());
        final DefaultMinutes      result        = this.converter.toDefaultMinutes(minutesByName);
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
    public Map<Integer, CarryoverIssue> loadIssues(final CarryoverSource source) throws IOException {

        final Map<Integer, CarryoverIssue> result = new TreeMap<>();

        for (final Map.Entry<Integer, CarryoverIssueDto> entry : this.carryoverDataRepository
            .loadIssues(source.getDataDir()).entrySet()) {

            result.put(entry.getKey(), this.converter.toCarryoverIssue(entry.getValue()));

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
    public void saveIssue(final CarryoverSource source, final CarryoverIssue issue) throws IOException {

        this.carryoverDataRepository.saveIssue(source.getDataDir(), this.converter.toCarryoverIssueDto(issue));

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
    public void saveSummary(final CarryoverSource source, final CarryoverSummary summary) throws IOException {

        this.carryoverDataRepository.saveSummary(source.getDataDir(), this.converter.toCarryoverSummaryDto(summary));

    }

}
