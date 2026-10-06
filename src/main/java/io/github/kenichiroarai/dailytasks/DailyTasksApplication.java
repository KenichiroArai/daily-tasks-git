package io.github.kenichiroarai.dailytasks;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;

import io.github.kenichiroarai.dailytasks.carryover.application.service.CarryoverService;
import io.github.kenichiroarai.dailytasks.carryover.application.service.impl.CarryoverServiceImpl;
import io.github.kenichiroarai.dailytasks.carryover.domain.aggregator.CarryoverAggregator;
import io.github.kenichiroarai.dailytasks.carryover.domain.model.DefaultMinutes;
import io.github.kenichiroarai.dailytasks.carryover.domain.parser.CarryoverParser;
import io.github.kenichiroarai.dailytasks.carryover.infrastructure.github.GitHubIssueClient;
import io.github.kenichiroarai.dailytasks.carryover.presentation.command.CarryoverCommand;
import io.github.kenichiroarai.dailytasks.carryover.repository.CarryoverDataRepository;
import io.github.kenichiroarai.dailytasks.carryover.repository.DefaultMinutesRepository;

/**
 * 起動クラス<br>
 * <p>
 * リポジトリのルートをカレントディレクトリとして実行する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public final class DailyTasksApplication {

    /**
     * GitHub API のベース URL
     */
    static final String API_BASE_URL = "https://api.github.com";

    /**
     * 対象リポジトリ
     */
    static final String REPOSITORY = "KenichiroArai/daily-tasks-git";

    /**
     * 出力先のディレクトリ
     */
    static final Path DATA_DIR = Path.of("docs", "data");

    /**
     * 標準時間の設定ファイル
     */
    static final Path DEFAULT_MINUTES_FILE = Path.of("config", "default-minutes.json");

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private DailyTasksApplication() {

        // 処理なし

    }

    /**
     * エントリポイント<br>
     *
     * @param args
     *             コマンドライン引数（{@link CarryoverCommand} を参照）
     *
     * @throws IOException
     *                     取得、読み込みまたは書き込みに失敗した場合
     */
    public static void main(final String[] args) throws IOException {

        /* 部品の組み立て */
        final DefaultMinutes defaultMinutes = new DefaultMinutesRepository(DailyTasksApplication.DEFAULT_MINUTES_FILE)
            .load();
        final GitHubIssueClient gitHubIssueClient = new GitHubIssueClient(HttpClient.newHttpClient(),
            DailyTasksApplication.API_BASE_URL, DailyTasksApplication.REPOSITORY, System.getenv("GITHUB_TOKEN"));
        final CarryoverService carryoverService = new CarryoverServiceImpl(gitHubIssueClient,
            new CarryoverDataRepository(DailyTasksApplication.DATA_DIR), new CarryoverParser(defaultMinutes),
            new CarryoverAggregator());

        /* コマンドの実行 */
        new CarryoverCommand(carryoverService, System.out).execute(args);

    }

}
