package io.github.kenichiroarai.dailytasks.carryover.repository.dao.carryoverdata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverIssueDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.dto.CarryoverSummaryDto;
import io.github.kenichiroarai.dailytasks.carryover.domain.repository.dao.CarryoverDataDao;
import io.github.kenichiroarai.dailytasks.carryover.repository.dto.json.CarryoverIssueJsonDto;
import io.github.kenichiroarai.dailytasks.carryover.repository.logic.CarryoverJsonConvertLogic;
import tools.jackson.core.JacksonException;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.core.util.Separators;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

/**
 * 持ち越しの JSON ファイル（Issue ごとの解析結果と画面用の集計）を読み書きする DAO の実装<br>
 * <p>
 * domain 層の DAO を実装する。受け取った DTO は {@link CarryoverJsonConvertLogic} で JSON の形の型に変換して読み書きする。出力は OS
 * に関係なく改行を LF に統一し、差分が安定するようにする。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
@Repository
public class CarryoverDataDaoImpl implements CarryoverDataDao {

    /** Issue ごとの JSON を置くディレクトリ名 */
    private static final String ISSUES_DIR = "issues";

    /** 画面用の集計の JSON ファイル名 */
    private static final String SUMMARY_FILE = "summary.json";

    /** JSON ファイルの拡張子 */
    private static final String JSON_EXTENSION = ".json";

    /** JSON の変換 */
    private final JsonMapper jsonMapper;

    /** JSON の出力 */
    private final ObjectWriter objectWriter;

    /** DTO と JSON の形の変換 */
    private final CarryoverJsonConvertLogic jsonConvertLogic;

    /**
     * コンストラクタ<br>
     *
     * @param jsonMapper
     *                         JSON の変換
     * @param jsonConvertLogic
     *                         DTO と JSON の形の変換
     */
    public CarryoverDataDaoImpl(final JsonMapper jsonMapper, final CarryoverJsonConvertLogic jsonConvertLogic) {

        this.jsonMapper = jsonMapper;
        this.jsonConvertLogic = jsonConvertLogic;

        /* 改行を LF に統一した整形出力 */
        final DefaultIndenter      indenter = new DefaultIndenter("  ", "\n");
        final DefaultPrettyPrinter printer  = new DefaultPrettyPrinter()
            .withSeparators(Separators.createDefaultInstance().withObjectNameValueSpacing(Separators.Spacing.AFTER))
            .withObjectIndenter(indenter).withArrayIndenter(indenter);
        this.objectWriter = this.jsonMapper.writer().with(printer);

    }

    /**
     * 保存済みの Issue ごとの解析結果を読み込む<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     *
     * @return Issue 番号と解析結果の対応。ディレクトリがない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    @Override
    public Map<Integer, CarryoverIssueDto> loadIssues(final Path dataDir) throws IOException {

        final Map<Integer, CarryoverIssueDto> result    = new TreeMap<>();
        final Path                            issuesDir = dataDir.resolve(CarryoverDataDaoImpl.ISSUES_DIR);

        if (!Files.isDirectory(issuesDir)) {

            return result;

        }

        try (Stream<Path> paths = Files.list(issuesDir)) {

            for (final Path path : paths
                .filter(p -> p.getFileName().toString().endsWith(CarryoverDataDaoImpl.JSON_EXTENSION))
                .toList()) {

                final CarryoverIssueDto issue = this.jsonConvertLogic.toCarryoverIssueDto(this.readIssue(path));
                result.put(issue.getNumber(), issue);

            }

        }

        return result;

    }

    /**
     * Issue ごとの解析結果の JSON ファイルを読み込む<br>
     *
     * @param path
     *             JSON ファイル
     *
     * @return 解析結果
     *
     * @throws IOException
     *                     読み込みまたは JSON の変換に失敗した場合
     */
    private CarryoverIssueJsonDto readIssue(final Path path) throws IOException {

        CarryoverIssueJsonDto result = null;

        try {

            result = this.jsonMapper.readValue(path.toFile(), CarryoverIssueJsonDto.class);

        } catch (final JacksonException e) {

            throw new IOException(e);

        }

        return result;

    }

    /**
     * Issue ごとの解析結果を保存する<br>
     * <p>
     * ファイル名は Issue 番号を 4 桁でゼロ埋めした名前（例: 0001.json）とする。
     * </p>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     * @param issue
     *                解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveIssue(final Path dataDir, final CarryoverIssueDto issue) throws IOException {

        final Path path = dataDir.resolve(CarryoverDataDaoImpl.ISSUES_DIR)
            .resolve(String.format("%04d%s", issue.getNumber(), CarryoverDataDaoImpl.JSON_EXTENSION));
        this.write(path, this.jsonConvertLogic.toCarryoverIssueJson(issue));

    }

    /**
     * 画面用の集計を保存する<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     * @param summary
     *                画面用の集計
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    @Override
    public void saveSummary(final Path dataDir, final CarryoverSummaryDto summary) throws IOException {

        final Path path = dataDir.resolve(CarryoverDataDaoImpl.SUMMARY_FILE);
        this.write(path, this.jsonConvertLogic.toCarryoverSummaryJson(summary));

    }

    /**
     * オブジェクトを JSON ファイルに書き込む<br>
     *
     * @param path
     *              出力先
     * @param value
     *              出力するオブジェクト
     *
     * @throws IOException
     *                     書き込みまたは JSON の変換に失敗した場合
     */
    private void write(final Path path, final Object value) throws IOException {

        String json = null;

        try {

            json = this.objectWriter.writeValueAsString(value) + "\n";

        } catch (final JacksonException e) {

            throw new IOException(e);

        }

        Files.createDirectories(path.getParent());
        Files.writeString(path, json, StandardCharsets.UTF_8);

    }

}
