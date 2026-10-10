package io.github.kenichiroarai.dailytasks.carryover.presentation.command;

import org.springframework.boot.CommandLineRunner;

/**
 * 持ち越しの収集コマンド<br>
 * <p>
 * Spring Boot の起動後に {@link CommandLineRunner#run(String...)} として実行される。
 * </p>
 * <p>
 * 引数：
 * </p>
 * <ul>
 * <li>なし：差分モード</li>
 * <li>{@code --full}：全件モード</li>
 * <li>{@code --help} / {@code -h}：使い方を表示</li>
 * </ul>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public interface CarryoverCommand extends CommandLineRunner {
    // メソッドは CommandLineRunner から継承する
}
