package io.github.kenichiroarai.dailytasks.carryover.domain.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import io.github.kenichiroarai.dailytasks.carryover.repository.github.GitHubIssueRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.github.impl.GitHubIssueRepositoryImpl;
import io.github.kenichiroarai.dailytasks.carryover.repository.impl.CarryoverDataRepositoryImpl;
import io.github.kenichiroarai.dailytasks.carryover.repository.impl.DefaultMinutesRepositoryImpl;

/**
 * 持ち越しのデータの取得・保存サービスの実装<br>
 * <p>
 * repository 層の DTO と domain 層のモデルを {@link CarryoverDtoConverter} で変換する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
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
     * コンストラクタ<br>
     * <p>
     * データの取得元と保存先から repository 層の実装を生成する。
     * </p>
     *
     * @param source
     *               データの取得元と保存先
     */
    public CarryoverIssueServiceImpl(final CarryoverSource source) {

        this(new GitHubIssueRepositoryImpl(CarryoverDtoConverter.toGitHubSettingsDto(source)),
            new CarryoverDataRepositoryImpl(source.getDataDir()),
            new DefaultMinutesRepositoryImpl(source.getDefaultMinutesFile()));

    }

    /**
     * repository を指定するコンストラクタ<br>
     *
     * @param gitHubIssueRepository
     *                                 GitHub の Issue の取得
     * @param carryoverDataRepository
     *                                 JSON ファイルの読み書き
     * @param defaultMinutesRepository
     *                                 標準時間の設定の読み込み
     */
    public CarryoverIssueServiceImpl(final GitHubIssueRepository gitHubIssueRepository,
        final CarryoverDataRepository carryoverDataRepository, final DefaultMinutesRepository defaultMinutesRepository) {

        this.gitHubIssueRepository = gitHubIssueRepository;
        this.carryoverDataRepository = carryoverDataRepository;
        this.defaultMinutesRepository = defaultMinutesRepository;

    }

    /**
     * すべての日々のタスク Issue を取得する<br>
     *
     * @return 日々のタスク Issue（作成順）
     *
     * @throws IOException
     *                     取得に失敗した場合
     */
    @Override
    public List<DailyTaskIssue> fetchAllIssues() throws IOException {

        final List<DailyTaskIssue> result = this.gitHubIssueRepository.fetchAllIssues().stream()
            .map(CarryoverDtoConverter::toDailyTaskIssue).toList();
        return result;

    }

    /**
     * 保存済みの持ち越しの解析結果を読み込む<br>
     *
     * @return Issue 番号と解析結果の対応。保存されていない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public Map<Integer, CarryoverIssue> loadIssues() throws IOException {

        final Map<Integer, CarryoverIssue> result = new TreeMap<>();

        for (final Map.Entry<Integer, CarryoverIssueDto> entry : this.carryoverDataRepository.loadIssues()
            .entrySet()) {

            result.put(entry.getKey(), CarryoverDtoConverter.toCarryoverIssue(entry.getValue()));

        }

        return result;

    }

    /**
     * 持ち越しの解析結果を保存する<br>
     *
     * @param issue
     *              持ち越しの解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveIssue(final CarryoverIssue issue) throws IOException {

        this.carryoverDataRepository.saveIssue(CarryoverDtoConverter.toCarryoverIssueDto(issue));

    }

    /**
     * 画面用の集計を保存する<br>
     *
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveSummary(final CarryoverSummary summary) throws IOException {

        this.carryoverDataRepository.saveSummary(CarryoverDtoConverter.toCarryoverSummaryDto(summary));

    }

    /**
     * 項目ごとの標準時間を読み込む<br>
     *
     * @return 項目ごとの標準時間。設定がない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public DefaultMinutes loadDefaultMinutes() throws IOException {

        final DefaultMinutes result = CarryoverDtoConverter.toDefaultMinutes(this.defaultMinutesRepository.load());
        return result;

    }

}
