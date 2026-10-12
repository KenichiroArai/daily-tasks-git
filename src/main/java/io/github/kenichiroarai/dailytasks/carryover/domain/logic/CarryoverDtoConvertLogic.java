package io.github.kenichiroarai.dailytasks.carryover.domain.logic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverItemModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSourceModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.CarryoverSummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailySummaryModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DailyTaskIssueModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.DefaultMinutesModel;
import io.github.kenichiroarai.dailytasks.carryover.application.model.ItemStatModel;
import io.github.kenichiroarai.dailytasks.carryover.application.types.MinutesSourceTypes;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.GitHubSettingsDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.ItemStatDto;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.logic.MessageLogic;

/**
 * application 層のモデルと domain 層の DTO を相互に変換する<br>
 * <p>
 * domain 層は repository 層を参照しないため、application 層のモデルを自層の DTO に変換してから repository 層に渡す。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
@SuppressWarnings({
    "static-method", "nls"
})
public class CarryoverDtoConvertLogic {

    /** メッセージのキー：不明な残り時間の取得元 */
    private static final String MSG_UNKNOWN_MINUTES_SOURCE = "carryover.minutesSource.unknownValue";

    /**
     * 持ち越し項目を保存用の DTO に変換する<br>
     *
     * @param item
     *             持ち越し項目
     *
     * @return 保存用の DTO
     */
    private static CarryoverItemDto toCarryoverItemDto(final CarryoverItemModel item) {

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
    private static DailySummaryDto toDailySummaryDto(final DailySummaryModel day) {

        final DailySummaryDto result = new DailySummaryDto(day.getDate(), day.getIssue(), day.getDeclaredCount(),
            CarryoverDtoConvertLogic.toItemStatDto(day.getTotal()), CarryoverDtoConvertLogic.toItemStatDtos(day.getByItem()),
            CarryoverDtoConvertLogic.toItemStatDtos(day.getByOriginMonth()));
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
    private static ItemStatDto toItemStatDto(final ItemStatModel stat) {

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
    private static Map<String, ItemStatDto> toItemStatDtos(final Map<String, ItemStatModel> stats) {

        final Map<String, ItemStatDto> result = new LinkedHashMap<>();

        for (final Map.Entry<String, ItemStatModel> entry : stats.entrySet()) {

            result.put(entry.getKey(), CarryoverDtoConvertLogic.toItemStatDto(entry.getValue()));

        }

        return result;

    }

    /** メッセージの取得 */
    private final MessageLogic messageLogic;

    /**
     * コンストラクタ<br>
     *
     * @param messageLogic
     *                     メッセージの取得
     */
    public CarryoverDtoConvertLogic(final MessageLogic messageLogic) {

        this.messageLogic = messageLogic;

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
    public CarryoverIssueModel toCarryoverIssue(final CarryoverIssueDto dto) {

        final List<CarryoverItemModel> items  = dto.getItems().stream().map(this::toCarryoverItem).toList();
        final CarryoverIssueModel      result = new CarryoverIssueModel(dto.getNumber(), dto.getTitle(), dto.getDate(),
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
    public CarryoverIssueDto toCarryoverIssueDto(final CarryoverIssueModel issue) {

        final List<CarryoverItemDto> items  = issue.getItems().stream().map(CarryoverDtoConvertLogic::toCarryoverItemDto)
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
    private CarryoverItemModel toCarryoverItem(final CarryoverItemDto dto) {

        CarryoverItemModel result = null;

        final MinutesSourceTypes minutesSource = MinutesSourceTypes.fromValue(dto.getMinutesSource());

        if (minutesSource == null) {

            final String template = this.messageLogic.get(CarryoverDtoConvertLogic.MSG_UNKNOWN_MINUTES_SOURCE);
            final String message  = String.format(template, dto.getMinutesSource());
            throw new IllegalArgumentException(message);

        }

        result = new CarryoverItemModel(dto.getName(), dto.getOriginDate(), dto.isChecked(), dto.getMinutes(), minutesSource,
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
    public CarryoverSummaryDto toCarryoverSummaryDto(final CarryoverSummaryModel summary) {

        final List<DailySummaryDto> days   = summary.getDays().stream().map(CarryoverDtoConvertLogic::toDailySummaryDto)
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
    public DailyTaskIssueModel toDailyTaskIssue(final GitHubIssueDto dto) {

        final DailyTaskIssueModel result
            = new DailyTaskIssueModel(dto.getNumber(), dto.getTitle(), dto.getState(), dto.getUpdatedAt(), dto.getBody());
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
    public DefaultMinutesModel toDefaultMinutes(final Map<String, Double> minutesByName) {

        final DefaultMinutesModel result = new DefaultMinutesModel(minutesByName);
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
    public GitHubSettingsDto toGitHubSettingsDto(final CarryoverSourceModel source) {

        final GitHubSettingsDto result = new GitHubSettingsDto(source.getRepository(), source.getToken());
        return result;

    }

}
