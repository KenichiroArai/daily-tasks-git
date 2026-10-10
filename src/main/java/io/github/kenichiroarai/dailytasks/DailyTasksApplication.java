package io.github.kenichiroarai.dailytasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 起動クラス<br>
 * <p>
 * Spring Boot を起動するだけとする。各層の部品はコンポーネントスキャンで登録し、コンストラクタインジェクションで組み立てる。presentation 層のコマンド（CommandLineRunner）が起動後に実行される。
 * リポジトリのルートをカレントディレクトリとして実行する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class DailyTasksApplication {

    /**
     * エントリポイント<br>
     *
     * @param args
     *             コマンドライン引数（{@code --full}、{@code --help}）
     */
    public static void main(final String[] args) {

        /* Spring Boot の起動 */
        SpringApplication.run(DailyTasksApplication.class, args);

    }

    /**
     * コンストラクタ<br>
     * <p>
     * Spring が設定クラスとして生成する。
     * </p>
     */
    public DailyTasksApplication() {

        // 処理なし

    }

}
