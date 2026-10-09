package io.github.kenichiroarai.dailytasks.carryover.repository.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverItemDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.DailySummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.ItemStatDto;
import io.github.kenichiroarai.dailytasks.testutil.ReflectionTestUtil;

/**
 * {@link CarryoverDataRepositoryImpl} のテスト<br>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverDataRepositoryImplTest {

    /**
     * テスト用の一時ディレクトリ
     */
    @TempDir
    Path tempDir;

    /**
     * テスト用の解析結果を作成する<br>
     *
     * @param number
     *               Issue 番号
     *
     * @return 解析結果
     */
    private static CarryoverIssueDto createIssue(final int number) {

        final CarryoverItemDto item = new CarryoverItemDto("国語", "2026-06-18", true, 8.5, "parsed", "持ち越し",
            "- [x] 国語2026/06/18（残り時間：8.5分）");
        final CarryoverIssueDto result = new CarryoverIssueDto(number, "2026年10月06日のタスク", "2026-10-06", "open",
            "2026-10-06T14:23:08Z", List.of("持ち越し"), Integer.valueOf(1), 1, 8.5, List.of(item));
        return result;

    }

    /**
     * private の write メソッドを呼び出す<br>
     *
     * @param target
     *               テスト対象
     * @param path
     *               書き込み先
     * @param value
     *               書き込む値
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    private static void write(final CarryoverDataRepositoryImpl target, final Path path, final Object value)
        throws Exception {

        ReflectionTestUtil.invoke(target, "write", new Class<?>[] {
            Path.class, Object.class
        }, path, value);

    }

    /**
     * loadIssues メソッドのテスト - 正常系:保存済みの JSON を読み込む場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoadIssues_normalSaved() throws IOException {

        /* 期待値の定義 */
        final Set<Integer> expectedNumbers = Set.of(Integer.valueOf(1), Integer.valueOf(371));
        final double expectedMinutes = 8.5;
        final String expectedMinutesSource = "parsed";

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);
        testTarget.saveIssue(CarryoverDataRepositoryImplTest.createIssue(1));
        testTarget.saveIssue(CarryoverDataRepositoryImplTest.createIssue(371));
        Files.writeString(this.tempDir.resolve("issues").resolve("memo.txt"), "対象外");

        /* テスト対象の実行 */
        final Map<Integer, CarryoverIssueDto> testResult = testTarget.loadIssues();

        /* 検証の準備 */
        final Set<Integer> actualNumbers = testResult.keySet();
        final CarryoverItemDto actualItem = testResult.get(Integer.valueOf(371)).getItems().get(0);
        final double actualMinutes = actualItem.getMinutes();
        final String actualMinutesSource = actualItem.getMinutesSource();
        final boolean actualChecked = actualItem.isChecked();

        /* 検証の実施 */
        Assertions.assertEquals(expectedNumbers, actualNumbers, "Issue 番号が一致しません");
        Assertions.assertEquals(expectedMinutes, actualMinutes, "残り時間が一致しません");
        Assertions.assertEquals(expectedMinutesSource, actualMinutesSource, "残り時間の取得元が一致しません");
        Assertions.assertTrue(actualChecked, "チェック済みになっていません");

    }

    /**
     * loadIssues メソッドのテスト - 準正常系:ディレクトリがない場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoadIssues_semiNoDirectory() throws IOException {

        /* 期待値の定義 */

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir.resolve("none"));

        /* テスト対象の実行 */
        final Map<Integer, CarryoverIssueDto> testResult = testTarget.loadIssues();

        /* 検証の準備 */
        final boolean actualEmpty = testResult.isEmpty();

        /* 検証の実施 */
        Assertions.assertTrue(actualEmpty, "空になる必要があります");

    }

    /**
     * loadIssues メソッドのテスト - 異常系:JSON が不正な場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testLoadIssues_errorInvalidJson() throws IOException {

        /* 期待値の定義 */

        /* 準備 */
        final Path testIssuesDir = Files.createDirectories(this.tempDir.resolve("issues"));
        Files.writeString(testIssuesDir.resolve("0001.json"), "{不正");
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);

        /* テスト対象の実行 */
        final IOException testException = Assertions.assertThrows(IOException.class, testTarget::loadIssues);

        /* 検証の準備 */
        final IOException actualException = testException;

        /* 検証の実施 */
        Assertions.assertInstanceOf(JsonProcessingException.class, actualException, "例外の型が一致しません");

    }

    /**
     * saveIssue メソッドのテスト - 正常系:4 桁ゼロ埋めのファイル名で LF 改行の JSON を保存する場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testSaveIssue_normalFileName() throws IOException {

        /* 期待値の定義 */
        final String expectedFirstLines = "{\n  \"number\": 7,\n  \"title\": \"2026年10月06日のタスク\",\n";

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);

        /* テスト対象の実行 */
        testTarget.saveIssue(CarryoverDataRepositoryImplTest.createIssue(7));

        /* 検証の準備 */
        final String actualJson = Files.readString(this.tempDir.resolve("issues").resolve("0007.json"),
            StandardCharsets.UTF_8);
        final boolean actualStartsWith = actualJson.startsWith(expectedFirstLines);
        final boolean actualHasCr = actualJson.contains("\r");
        final boolean actualEndsWithLf = actualJson.endsWith("}\n");

        /* 検証の実施 */
        Assertions.assertTrue(actualStartsWith, "JSON の先頭が一致しません: " + actualJson);
        Assertions.assertFalse(actualHasCr, "改行は LF である必要があります");
        Assertions.assertTrue(actualEndsWithLf, "末尾は改行である必要があります");

    }

    /**
     * saveSummary メソッドのテスト - 正常系:画面用の集計を保存する場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testSaveSummary_normalSave() throws IOException {

        /* 期待値の定義 */
        final String expectedJson = "{\n  \"latestIssue\": 371,\n  \"items\": [\n    \"国語\"\n  ],\n  \"days\": [ ]\n}\n";

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);

        /* テスト対象の実行 */
        testTarget.saveSummary(new CarryoverSummaryDto(371, List.of("国語"), List.of()));

        /* 検証の準備 */
        final String actualJson = Files.readString(this.tempDir.resolve("summary.json"), StandardCharsets.UTF_8);

        /* 検証の実施 */
        Assertions.assertEquals(expectedJson, actualJson, "JSON が一致しません");

    }

    /**
     * saveSummary メソッドのテスト - 正常系:日別の集計を指定した順で保存する場合
     *
     * @throws IOException
     *                     入出力エラーが発生した場合
     */
    @Test
    public void testSaveSummary_normalDays() throws IOException {

        /* 期待値の定義 */
        final String expectedJson = """
            {
              "latestIssue": 175,
              "items": [ ],
              "days": [
                {
                  "date": "2026-03-24",
                  "issue": 175,
                  "declaredCount": null,
                  "total": {
                    "count": 1,
                    "minutes": 30.0,
                    "checkedCount": 0,
                    "checkedMinutes": 0.0
                  },
                  "byItem": {
                    "英語": {
                      "count": 1,
                      "minutes": 30.0,
                      "checkedCount": 0,
                      "checkedMinutes": 0.0
                    },
                    "国語": {
                      "count": 0,
                      "minutes": 0.0,
                      "checkedCount": 0,
                      "checkedMinutes": 0.0
                    }
                  },
                  "byOriginMonth": { }
                }
              ]
            }
            """;

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);
        final ItemStatDto testStat = new ItemStatDto(1, 30, 0, 0);
        final ItemStatDto testEmptyStat = new ItemStatDto(0, 0, 0, 0);
        final Map<String, ItemStatDto> testByItem = new LinkedHashMap<>();
        testByItem.put("英語", testStat);
        testByItem.put("国語", testEmptyStat);
        final DailySummaryDto testDay = new DailySummaryDto("2026-03-24", 175, null, testStat, testByItem, Map.of());

        /* テスト対象の実行 */
        testTarget.saveSummary(new CarryoverSummaryDto(175, List.of(), List.of(testDay)));

        /* 検証の準備 */
        final String actualJson = Files.readString(this.tempDir.resolve("summary.json"), StandardCharsets.UTF_8);

        /* 検証の実施 */
        Assertions.assertEquals(expectedJson, actualJson, "JSON が一致しません");

    }

    /**
     * write メソッドのテスト - 正常系:親ディレクトリを作成して書き込む場合
     *
     * @throws Exception
     *                   例外が発生した場合
     */
    @Test
    public void testWrite_normalCreateParent() throws Exception {

        /* 期待値の定義 */
        final String expectedJson = "[\n  1\n]\n";

        /* 準備 */
        final CarryoverDataRepositoryImpl testTarget = new CarryoverDataRepositoryImpl(this.tempDir);
        final Path testPath = this.tempDir.resolve("a").resolve("b").resolve("c.json");

        /* テスト対象の実行 */
        CarryoverDataRepositoryImplTest.write(testTarget, testPath, List.of(Integer.valueOf(1)));

        /* 検証の準備 */
        final String actualJson = Files.readString(testPath, StandardCharsets.UTF_8);

        /* 検証の実施 */
        Assertions.assertEquals(expectedJson, actualJson, "JSON が一致しません");

    }

}
