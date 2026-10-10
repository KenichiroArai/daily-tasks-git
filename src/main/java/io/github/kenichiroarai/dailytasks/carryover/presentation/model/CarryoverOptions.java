package io.github.kenichiroarai.dailytasks.carryover.presentation.model;

/**
 * 持ち越しの収集コマンドのオプション<br>
 * <p>
 * コマンドライン引数を解析した結果を持つ。各オプションの既定値は false とする。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class CarryoverOptions {

    /**
     * 全件モードか
     */
    private boolean full;

    /**
     * 使い方を表示するか
     */
    private boolean help;

    /**
     * コンストラクタ<br>
     * <p>
     * 全件モード・使い方の表示はいずれも false で作成する。
     * </p>
     */
    public CarryoverOptions() {

        this.full = false;
        this.help = false;

    }

    /**
     * 全件モードかを返す<br>
     *
     * @return true：全件モード、false：差分モード
     */
    public boolean isFull() {

        final boolean result = this.full;
        return result;

    }

    /**
     * 使い方を表示するかを返す<br>
     *
     * @return true：使い方を表示する、false：表示しない
     */
    public boolean isHelp() {

        final boolean result = this.help;
        return result;

    }

    /**
     * 全件モードかを設定する<br>
     *
     * @param full
     *             true：全件モード、false：差分モード
     */
    public void setFull(final boolean full) {

        this.full = full;

    }

    /**
     * 使い方を表示するかを設定する<br>
     *
     * @param help
     *             true：使い方を表示する、false：表示しない
     */
    public void setHelp(final boolean help) {

        this.help = help;

    }

}
