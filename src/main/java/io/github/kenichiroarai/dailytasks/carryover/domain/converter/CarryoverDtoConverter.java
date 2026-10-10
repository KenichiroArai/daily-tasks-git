package io.github.kenichiroarai.dailytasks.carryover.domain.converter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverItem;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSource;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailySummary;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DailyTaskIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.ItemStat;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.MinutesSource;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.resource.MessageProvider;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.ItemStatDto;

/**
 * repository 層の DTO と domain 層のモデルを相互に変換する<br>
 * <p>
 * repository 層はモデルを知らないため、変換は両方を知る domain 層で行う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
public class CarryoverDtoConverter {

    /**
     * メッセージのキー：不明な残り時間の取得元
     */
    private static final String MSG_UNKNOWN_MINUTES_SOURCE = "carryover.minutesSource.unknownValue";

    /**
     * 持ち越し項目を保存用の DTO に変換する<br>
     *
     * @param item
     *             持ち越し項目
     *
     * @return 保存用の DTO
     */
    private static CarryoverItemDto toCarryoverItemDto(final CarryoverItem item) {

        final CarryoverItemDto result = new CarryoverItemDto(item.getName(), item.getOriginDate(), item.isChecked(),
            item.getMinutes(), item.getMinutesSource().getValue(), item.getSection(), item.getRaw());
        return result;

    }

    /**
     * 日別の集計を保存用の DTO に変換する<br>
     *
     * @param day
     *            日別の集計
     *
     * @return 保存用の DTO
     */
    private static DailySummaryDto toDailySummaryDto(final DailySummary day) {

        final DailySummaryDto result = new DailySummaryDto(day.getDate(), day.getIssue(), day.getDeclaredCount(),
            CarryoverDtoConverter.toItemStatDto(day.getTotal()), CarryoverDtoConverter.toItemStatDtos(day.getByItem()),
            CarryoverDtoConverter.toItemStatDtos(day.getByOriginMonth()));
        return result;

    }

    /**
     * 集計値を保存用の DTO に変換する<br>
     *
     * @param stat
     *             集計値
     *
     * @return 保存用の DTO
     */
    private static ItemStatDto toItemStatDto(final ItemStat stat) {

        final ItemStatDto result
            = new ItemStatDto(stat.getCount(), stat.getMinutes(), stat.getCheckedCount(), stat.getCheckedMinutes());
        return result;

    }

    /**
     * キーごとの集計値を保存用の DTO に変換する<br>
     *
     * @param stats
     *              キーごとの集計値
     *
     * @return キーごとの保存用の DTO（並び順は元のまま）
     */
    private static Map<String, ItemStatDto> toItemStatDtos(final Map<String, ItemStat> stats) {

        final Map<String, ItemStatDto> result = new LinkedHashMap<>();

        for (final Map.Entry<String, ItemStat> entry : stats.entrySet()) {

            result.put(entry.getKey(), CarryoverDtoConverter.toItemStatDto(entry.getValue()));

        }

        return result;

    }

    /**
     * メッセージの取得
     */
    private final MessageProvider messageProvider;

    /**
     * コンストラクタ<br>
     *
     * @param messageProvider
     *                        メッセージの取得
     */
    public CarryoverDtoConverter(final MessageProvider messageProvider) {

        this.messageProvider = messageProvider;

    }

    /**
     * 保存済みの解析結果を持ち越しの解析結果に変換する<br>
     *
     * @param dto
     *            保存済みの解析結果
     *
     * @return 持ち越しの解析結果
     *
     * @throws IllegalArgumentException
     *                                  残り時間の取得元が不明な場合
     */
    public CarryoverIssue toCarryoverIssue(final CarryoverIssueDto dto) {

        final List<CarryoverItem> items  = dto.getItems().stream().map(this::toCarryoverItem).toList();
        final CarryoverIssue      result = new CarryoverIssue(dto.getNumber(), dto.getTitle(), dto.getDate(),
            dto.getState(), dto.getUpdatedAt(), dto.getSections(), dto.getDeclaredCount(), items);
        return result;

    }

    /**
     * 持ち越しの解析結果を保存用の DTO に変換する<br>
     *
     * @param issue
     *              持ち越しの解析結果
     *
     * @return 保存用の DTO
     */
    public CarryoverIssueDto toCarryoverIssueDto(final CarryoverIssue issue) {

        final List<CarryoverItemDto> items  = issue.getItems().stream().map(CarryoverDtoConverter::toCarryoverItemDto)
            .toList();
        final CarryoverIssueDto      result = new CarryoverIssueDto(issue.getNumber(), issue.getTitle(),
            issue.getDate(), issue.getState(), issue.getUpdatedAt(), issue.getSections(), issue.getDeclaredCount(),
            issue.getCount(), issue.getMinutes(), items);
        return result;

    }

    /**
     * 保存済みの持ち越し項目を持ち越し項目に変換する<br>
     *
     * @param dto
     *            保存済みの持ち越し項目
     *
     * @return 持ち越し項目
     *
     * @throws IllegalArgumentException
     *                                  残り時間の取得元が不明な場合
     */
    private CarryoverItem toCarryoverItem(final CarryoverItemDto dto) {

        CarryoverItem result = null;

        final MinutesSource minutesSource = MinutesSource.fromValue(dto.getMinutesSource());

        if (minutesSource == null) {

            final String template = this.messageProvider.get(CarryoverDtoConverter.MSG_UNKNOWN_MINUTES_SOURCE);
            final String message  = String.format(template, dto.getMinutesSource());
            throw new IllegalArgumentException(message);

        }

        result = new CarryoverItem(dto.getName(), dto.getOriginDate(), dto.isChecked(), dto.getMinutes(), minutesSource,
            dto.getSection(), dto.getRaw());
        return result;

    }

    /**
     * 画面用の集計を保存用の DTO に変換する<br>
     *
     * @param summary
     *                画面用の集計
     *
     * @return 保存用の DTO
     */
    public CarryoverSummaryDto toCarryoverSummaryDto(final CarryoverSummary summary) {

        final List<DailySummaryDto> days   = summary.getDays().stream().map(CarryoverDtoConverter::toDailySummaryDto)
            .toList();
        final CarryoverSummaryDto   result = new CarryoverSummaryDto(summary.getLatestIssue(), summary.getItems(),
            days);
        return result;

    }

    /**
     * GitHub API から取得した Issue を日々のタスク Issue に変換する<br>
     *
     * @param dto
     *            GitHub API から取得した Issue
     *
     * @return 日々のタスク Issue
     */
    public DailyTaskIssue toDailyTaskIssue(final GitHubIssueDto dto) {

        final DailyTaskIssue result
            = new DailyTaskIssue(dto.getNumber(), dto.getTitle(), dto.getState(), dto.getUpdatedAt(), dto.getBody());
        return result;

    }

    /**
     * 項目名と標準時間の対応を項目ごとの標準時間に変換する<br>
     *
     * @param minutesByName
     *                      項目名と標準時間（分）の対応
     *
     * @return 項目ごとの標準時間
     */
    public DefaultMinutes toDefaultMinutes(final Map<String, Double> minutesByName) {

        final DefaultMinutes result = new DefaultMinutes(minutesByName);
        return result;

    }

    /**
     * データの取得元から GitHub API の接続設定に変換する<br>
     *
     * @param source
     *               データの取得元と保存先
     *
     * @return GitHub API の接続設定
     */
    public GitHubSettingsDto toGitHubSettingsDto(final CarryoverSource source) {

        final GitHubSettingsDto result = new GitHubSettingsDto(source.getRepository(), source.getToken());
        return result;

    }

}
