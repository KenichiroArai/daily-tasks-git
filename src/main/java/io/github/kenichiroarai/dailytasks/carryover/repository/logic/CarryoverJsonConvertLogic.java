package io.github.kenichiroarai.dailytasks.carryover.repository.logic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.ItemStatDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverIssueJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverItemJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverSummaryJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.DailySummaryJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.ItemStatJsonDto;

/**
 * domain 層の DTO と JSON の形の型を相互に変換する<br>
 * <p>
 * JSON の形は repository 層だけが持つため、domain 層から受け取った DTO をこの層で JSON の形の型に変換する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@Component
@SuppressWarnings("static-method")
public class CarryoverJsonConvertLogic {

    /**
     * 持ち越し項目を DTO に変換する<br>
     *
     * @param json
     *             JSON の形の持ち越し項目
     *
     * @return 持ち越し項目の DTO
     */
    private static CarryoverItemDto toCarryoverItemDto(final CarryoverItemJsonDto json) {

        final CarryoverItemDto result = new CarryoverItemDto(json.getName(), json.getOriginDate(), json.isChecked(),
            json.getMinutes(), json.getMinutesSource(), json.getSection(), json.getRaw());
        return result;

    }

    /**
     * 持ち越し項目を JSON の形に変換する<br>
     *
     * @param dto
     *            持ち越し項目の DTO
     *
     * @return JSON の形の持ち越し項目
     */
    private static CarryoverItemJsonDto toCarryoverItemJson(final CarryoverItemDto dto) {

        final CarryoverItemJsonDto result = new CarryoverItemJsonDto(dto.getName(), dto.getOriginDate(), dto.isChecked(),
            dto.getMinutes(), dto.getMinutesSource(), dto.getSection(), dto.getRaw());
        return result;

    }

    /**
     * 日別の集計を JSON の形に変換する<br>
     *
     * @param dto
     *            日別の集計の DTO
     *
     * @return JSON の形の日別の集計
     */
    private static DailySummaryJsonDto toDailySummaryJson(final DailySummaryDto dto) {

        final DailySummaryJsonDto result = new DailySummaryJsonDto(dto.getDate(), dto.getIssue(), dto.getDeclaredCount(),
            CarryoverJsonConvertLogic.toItemStatJson(dto.getTotal()), CarryoverJsonConvertLogic.toItemStatJsons(dto.getByItem()),
            CarryoverJsonConvertLogic.toItemStatJsons(dto.getByOriginMonth()));
        return result;

    }

    /**
     * 集計値を JSON の形に変換する<br>
     *
     * @param dto
     *            集計値の DTO
     *
     * @return JSON の形の集計値
     */
    private static ItemStatJsonDto toItemStatJson(final ItemStatDto dto) {

        final ItemStatJsonDto result
            = new ItemStatJsonDto(dto.getCount(), dto.getMinutes(), dto.getCheckedCount(), dto.getCheckedMinutes());
        return result;

    }

    /**
     * キーごとの集計値を JSON の形に変換する<br>
     *
     * @param dtos
     *             キーごとの集計値の DTO
     *
     * @return キーごとの JSON の形の集計値（並び順は元のまま）
     */
    private static Map<String, ItemStatJsonDto> toItemStatJsons(final Map<String, ItemStatDto> dtos) {

        final Map<String, ItemStatJsonDto> result = new LinkedHashMap<>();

        for (final Map.Entry<String, ItemStatDto> entry : dtos.entrySet()) {

            result.put(entry.getKey(), CarryoverJsonConvertLogic.toItemStatJson(entry.getValue()));

        }

        return result;

    }

    /**
     * Issue ごとの解析結果を DTO に変換する<br>
     *
     * @param json
     *             JSON の形の解析結果
     *
     * @return 解析結果の DTO
     */
    public CarryoverIssueDto toCarryoverIssueDto(final CarryoverIssueJsonDto json) {

        final List<CarryoverItemDto> items  = json.getItems().stream().map(CarryoverJsonConvertLogic::toCarryoverItemDto)
            .toList();
        final CarryoverIssueDto      result = new CarryoverIssueDto(json.getNumber(), json.getTitle(), json.getDate(),
            json.getState(), json.getUpdatedAt(), json.getSections(), json.getDeclaredCount(), json.getCount(),
            json.getMinutes(), items);
        return result;

    }

    /**
     * Issue ごとの解析結果を JSON の形に変換する<br>
     *
     * @param dto
     *            解析結果の DTO
     *
     * @return JSON の形の解析結果
     */
    public CarryoverIssueJsonDto toCarryoverIssueJson(final CarryoverIssueDto dto) {

        final List<CarryoverItemJsonDto> items  = dto.getItems().stream().map(CarryoverJsonConvertLogic::toCarryoverItemJson)
            .toList();
        final CarryoverIssueJsonDto      result = new CarryoverIssueJsonDto(dto.getNumber(), dto.getTitle(), dto.getDate(),
            dto.getState(), dto.getUpdatedAt(), dto.getSections(), dto.getDeclaredCount(), dto.getCount(),
            dto.getMinutes(), items);
        return result;

    }

    /**
     * 画面用の集計を JSON の形に変換する<br>
     *
     * @param dto
     *            画面用の集計の DTO
     *
     * @return JSON の形の画面用の集計
     */
    public CarryoverSummaryJsonDto toCarryoverSummaryJson(final CarryoverSummaryDto dto) {

        final List<DailySummaryJsonDto> days   = dto.getDays().stream().map(CarryoverJsonConvertLogic::toDailySummaryJson)
            .toList();
        final CarryoverSummaryJsonDto   result = new CarryoverSummaryJsonDto(dto.getLatestIssue(), dto.getItems(), days);
        return result;

    }

}
