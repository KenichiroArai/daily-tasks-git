package io.github.kenichiroarai.dailytasks.carryover.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverIssue;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.CarryoverSummary;

/**
 * 持ち越しの JSON ファイル（Issue ごとの解析結果と画面用の集計）を読み書きする<br>
 * <p>
 * 出力は OS に関係なく改行を LF に統一し、差分が安定するようにする。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public class CarryoverDataRepository {

    /**
     * Issue ごとの JSON を置くディレクトリ名
     */
    private static final String ISSUES_DIR = "issues";

    /**
     * 画面用の集計の JSON ファイル名
     */
    private static final String SUMMARY_FILE = "summary.json";

    /**
     * JSON ファイルの拡張子
     */
    private static final String JSON_EXTENSION = ".json";

    /**
     * 出力先のディレクトリ（例: docs/data）
     */
    private final Path dataDir;

    /**
     * JSON の変換
     */
    private final ObjectMapper objectMapper;

    /**
     * JSON の出力
     */
    private final ObjectWriter objectWriter;

    /**
     * コンストラクタ<br>
     *
     * @param dataDir
     *                出力先のディレクトリ（例: docs/data）
     */
    public CarryoverDataRepository(final Path dataDir) {

        this.dataDir = dataDir;
        this.objectMapper = new ObjectMapper();

        /* 改行を LF に統一した整形出力 */
        final DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
        final DefaultPrettyPrinter printer = new DefaultPrettyPrinter()
            .withSeparators(Separators.createDefaultInstance().withObjectFieldValueSpacing(Separators.Spacing.AFTER))
            .withObjectIndenter(indenter).withArrayIndenter(indenter);
        this.objectWriter = this.objectMapper.writer(printer);

    }

    /**
     * 保存済みの Issue ごとの解析結果を読み込む<br>
     *
     * @return Issue 番号と解析結果の対応。ディレクトリがない場合は空
     *
     * @throws IOException
     *                     読み込みに失敗した場合
     */
    public Map<Integer, CarryoverIssue> loadIssues() throws IOException {

        final Map<Integer, CarryoverIssue> result = new TreeMap<>();
        final Path issuesDir = this.dataDir.resolve(CarryoverDataRepository.ISSUES_DIR);

        if (!Files.isDirectory(issuesDir)) {

            return result;

        }

        try (Stream<Path> paths = Files.list(issuesDir)) {

            for (final Path path : paths.filter(p -> p.getFileName().toString().endsWith(JSON_EXTENSION)).toList()) {

                final CarryoverIssue issue = this.objectMapper.readValue(path.toFile(), CarryoverIssue.class);
                result.put(Integer.valueOf(issue.getNumber()), issue);

            }

        }

        return result;

    }

    /**
     * Issue ごとの解析結果を保存する<br>
     * <p>
     * ファイル名は Issue 番号を 4 桁でゼロ埋めした名前（例: 0001.json）とする。
     * </p>
     *
     * @param issue
     *              解析結果
     *
     * @throws IOException
     *                     書き込みに失敗した場合
     */
    public void saveIssue(final CarryoverIssue issue) throws IOException {

        final Path path = this.dataDir.resolve(CarryoverDataRepository.ISSUES_DIR)
            .resolve(String.format("%04d%s", issue.getNumber(), CarryoverDataRepository.JSON_EXTENSION));
        this.write(path, issue);

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
    public void saveSummary(final CarryoverSummary summary) throws IOException {

        final Path path = this.dataDir.resolve(CarryoverDataRepository.SUMMARY_FILE);
        this.write(path, summary);

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
     *                     書き込みに失敗した場合
     */
    private void write(final Path path, final Object value) throws IOException {

        Files.createDirectories(path.getParent());
        final String json = this.objectWriter.writeValueAsString(value) + "\n";
        Files.writeString(path, json, StandardCharsets.UTF_8);

    }

}
