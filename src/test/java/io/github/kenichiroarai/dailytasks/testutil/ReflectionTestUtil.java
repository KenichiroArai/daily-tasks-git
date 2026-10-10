package io.github.kenichiroarai.dailytasks.testutil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * テスト用のリフレクションの操作<br>
 * <p>
 * private メソッドの呼び出しや private フィールドの取得を、標準のリフレクション（{@code java.lang.reflect}）で行う。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings("nls")
public final class ReflectionTestUtil {

    /**
     * クラスとその親クラスからメソッドを探し、アクセスできるようにする<br>
     *
     * @param clazz
     *                       探し始めるクラス
     * @param name
     *                       メソッド名
     * @param parameterTypes
     *                       引数の型
     *
     * @return 見つかったメソッド
     *
     * @throws NoSuchMethodException
     *                               メソッドが見つからない場合
     */
    private static Method findMethod(final Class<?> clazz, final String name, final Class<?>[] parameterTypes)
        throws NoSuchMethodException {

        Method result = null;

        /* 親クラスへさかのぼって探す */
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {

            try {

                result = current.getDeclaredMethod(name, parameterTypes);
                break;

            } catch (@SuppressWarnings("unused") final NoSuchMethodException e) {

                // 親クラスで探し直す

            }

        }

        if (result == null) {

            throw new NoSuchMethodException(clazz.getName() + "." + name);

        }

        result.setAccessible(true);
        return result;

    }

    /**
     * インスタンスフィールドの値を取得する<br>
     *
     * @param <T>
     *               値の型
     * @param target
     *               フィールドを持つインスタンス
     * @param name
     *               フィールド名
     *
     * @return フィールドの値
     *
     * @throws ReflectiveOperationException
     *                                      フィールドが見つからない場合、または値を取得できない場合
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(final Object target, final String name) throws ReflectiveOperationException {

        final Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        final T result = (T) field.get(target);
        return result;

    }

    /**
     * static フィールドの値を取得する<br>
     *
     * @param <T>
     *              値の型
     * @param clazz
     *              フィールドを定義したクラス
     * @param name
     *              フィールド名
     *
     * @return フィールドの値
     *
     * @throws ReflectiveOperationException
     *                                      フィールドが見つからない場合、または値を取得できない場合
     */
    @SuppressWarnings("unchecked")
    public static <T> T getStaticField(final Class<?> clazz, final String name) throws ReflectiveOperationException {

        final Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        final T result = (T) field.get(null);
        return result;

    }

    /**
     * インスタンスメソッドを呼び出す<br>
     * <p>
     * 呼び出したメソッドが例外を投げた場合は、その例外をそのまま投げ直す。
     * </p>
     *
     * @param <T>
     *                       戻り値の型
     * @param target
     *                       呼び出し対象のインスタンス
     * @param name
     *                       メソッド名
     * @param parameterTypes
     *                       引数の型
     * @param args
     *                       引数
     *
     * @return メソッドの戻り値
     *
     * @throws Exception
     *                   メソッドが見つからない場合、またはメソッドが例外を投げた場合
     */
    public static <T> T invoke(final Object target, final String name, final Class<?>[] parameterTypes,
        final Object... args) throws Exception {

        final Method method = ReflectionTestUtil.findMethod(target.getClass(), name, parameterTypes);
        final T      result = ReflectionTestUtil.invokeMethod(method, target, args);
        return result;

    }

    /**
     * メソッドを呼び出し、呼び出し先の例外を取り出して投げ直す<br>
     *
     * @param <T>
     *               戻り値の型
     * @param method
     *               呼び出すメソッド
     * @param target
     *               呼び出し対象のインスタンス（static メソッドの場合は null）
     * @param args
     *               引数
     *
     * @return メソッドの戻り値
     *
     * @throws Exception
     *                   メソッドが例外を投げた場合
     */
    @SuppressWarnings("unchecked")
    private static <T> T invokeMethod(final Method method, final Object target, final Object... args) throws Exception {

        T result = null;

        try {

            result = (T) method.invoke(target, args);

        } catch (final InvocationTargetException e) {

            final Throwable cause = e.getCause();

            if (cause instanceof final Exception exception) {

                throw exception;

            }

            if (cause instanceof final Error error) {

                throw error;

            }

            throw e;

        }

        return result;

    }

    /**
     * static メソッドを呼び出す<br>
     * <p>
     * 呼び出したメソッドが例外を投げた場合は、その例外をそのまま投げ直す。
     * </p>
     *
     * @param <T>
     *                       戻り値の型
     * @param clazz
     *                       メソッドを定義したクラス
     * @param name
     *                       メソッド名
     * @param parameterTypes
     *                       引数の型
     * @param args
     *                       引数
     *
     * @return メソッドの戻り値
     *
     * @throws Exception
     *                   メソッドが見つからない場合、またはメソッドが例外を投げた場合
     */
    public static <T> T invokeStatic(final Class<?> clazz, final String name, final Class<?>[] parameterTypes,
        final Object... args) throws Exception {

        final Method method = ReflectionTestUtil.findMethod(clazz, name, parameterTypes);
        final T      result = ReflectionTestUtil.invokeMethod(method, null, args);
        return result;

    }

    /**
     * コンストラクタ<br>
     * <p>
     * インスタンス化しない。
     * </p>
     */
    private ReflectionTestUtil() {

        // 処理なし

    }

}
