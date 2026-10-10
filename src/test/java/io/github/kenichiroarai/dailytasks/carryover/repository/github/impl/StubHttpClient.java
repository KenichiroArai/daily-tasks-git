package io.github.kenichiroarai.dailytasks.carryover.repository.github.impl;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

/**
 * テスト用の HTTP クライアント<br>
 * <p>
 * 実際の通信は行わず、登録した応答を順に返す。送信したリクエストは記録する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
public class StubHttpClient extends HttpClient {

    /**
     * テスト用の応答<br>
     *
     * @author KenichiroArai
     *
     * @since 0.1.0
     *
     * @version 0.1.0
     */
    private static final class StubHttpResponse implements HttpResponse<String> {

        /**
         * リクエスト
         */
        private final HttpRequest request;

        /**
         * ステータスコード
         */
        private final int statusCode;

        /**
         * 本文
         */
        private final String body;

        /**
         * コンストラクタ<br>
         *
         * @param request
         *                   リクエスト
         * @param statusCode
         *                   ステータスコード
         * @param body
         *                   本文
         */
        private StubHttpResponse(final HttpRequest request, final int statusCode, final String body) {

            this.request = request;
            this.statusCode = statusCode;
            this.body = body;

        }

        /**
         * 本文を返す<br>
         *
         * @return 本文
         */
        @Override
        public String body() {

            final String result = this.body;
            return result;

        }

        /**
         * ヘッダを返す<br>
         *
         * @return 空のヘッダ
         */
        @Override
        public HttpHeaders headers() {

            final HttpHeaders result = HttpHeaders.of(Map.of(), (_, _) -> true);
            return result;

        }

        /**
         * 前の応答を返す<br>
         *
         * @return 空
         */
        @Override
        public Optional<HttpResponse<String>> previousResponse() {

            final Optional<HttpResponse<String>> result = Optional.empty();
            return result;

        }

        /**
         * リクエストを返す<br>
         *
         * @return リクエスト
         */
        @Override
        public HttpRequest request() {

            final HttpRequest result = this.request;
            return result;

        }

        /**
         * SSL セッションを返す<br>
         *
         * @return 空
         */
        @Override
        public Optional<SSLSession> sslSession() {

            final Optional<SSLSession> result = Optional.empty();
            return result;

        }

        /**
         * ステータスコードを返す<br>
         *
         * @return ステータスコード
         */
        @Override
        public int statusCode() {

            final int result = this.statusCode;
            return result;

        }

        /**
         * URI を返す<br>
         *
         * @return リクエストの URI
         */
        @Override
        public URI uri() {

            final URI result = this.request.uri();
            return result;

        }

        /**
         * HTTP のバージョンを返す<br>
         *
         * @return HTTP/1.1
         */
        @Override
        public Version version() {

            final Version result = Version.HTTP_1_1;
            return result;

        }

    }

    /**
     * 返す応答（ステータスコードと本文）
     */
    private final Deque<Object[]> responses = new ArrayDeque<>();

    /**
     * 送信されたリクエスト
     */
    private final List<HttpRequest> requests = new ArrayList<>();

    /**
     * send で投げる中断例外。null の場合は投げない
     */
    private InterruptedException interruptedException;

    /**
     * 返す応答を追加する<br>
     *
     * @param statusCode
     *                   ステータスコード
     * @param body
     *                   本文
     *
     * @return 自身
     */
    public StubHttpClient addResponse(final int statusCode, final String body) {

        this.responses.add(new Object[] {
            Integer.valueOf(statusCode), body
        });
        final StubHttpClient result = this;
        return result;

    }

    /**
     * 認証を返す<br>
     *
     * @return 空
     */
    @Override
    public Optional<Authenticator> authenticator() {

        final Optional<Authenticator> result = Optional.empty();
        return result;

    }

    /**
     * クライアントを閉じる<br>
     * <p>
     * 通信を行わないため、何もしない。
     * </p>
     */
    @Override
    public void close() {

        // 処理なし

    }

    /**
     * 接続タイムアウトを返す<br>
     *
     * @return 空
     */
    @Override
    public Optional<Duration> connectTimeout() {

        final Optional<Duration> result = Optional.empty();
        return result;

    }

    /**
     * Cookie ハンドラを返す<br>
     *
     * @return 空
     */
    @Override
    public Optional<CookieHandler> cookieHandler() {

        final Optional<CookieHandler> result = Optional.empty();
        return result;

    }

    /**
     * エグゼキュータを返す<br>
     *
     * @return 空
     */
    @Override
    public Optional<Executor> executor() {

        final Optional<Executor> result = Optional.empty();
        return result;

    }

    /**
     * リダイレクトの方針を返す<br>
     *
     * @return リダイレクトしない
     */
    @Override
    public Redirect followRedirects() {

        final Redirect result = Redirect.NEVER;
        return result;

    }

    /**
     * 送信されたリクエストを返す<br>
     *
     * @return 送信されたリクエスト
     */
    public List<HttpRequest> getRequests() {

        final List<HttpRequest> result = this.requests;
        return result;

    }

    /**
     * プロキシを返す<br>
     *
     * @return 空
     */
    @Override
    public Optional<ProxySelector> proxy() {

        final Optional<ProxySelector> result = Optional.empty();
        return result;

    }

    /**
     * リクエストを記録し、登録した応答を返す<br>
     *
     * @param <T>
     *                    本文の型
     * @param request
     *                    リクエスト
     * @param bodyHandler
     *                    本文の変換（使用しない）
     *
     * @return 応答
     *
     * @throws IOException
     *                              使用しない
     * @throws InterruptedException
     *                              中断例外を設定した場合
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> send(final HttpRequest request, final HttpResponse.BodyHandler<T> bodyHandler)
        throws IOException, InterruptedException {

        this.requests.add(request);

        if (this.interruptedException != null) {

            throw this.interruptedException;

        }

        final Object[]        response = this.responses.removeFirst();
        final HttpResponse<T> result   = (HttpResponse<T>) new StubHttpResponse(request,
            ((Integer) response[0]), (String) response[1]);
        return result;

    }

    /**
     * 非同期送信（使用しない）<br>
     *
     * @param <T>
     *                            本文の型
     * @param request
     *                            リクエスト
     * @param responseBodyHandler
     *                            本文の変換
     *
     * @return 返さない
     *
     * @throws UnsupportedOperationException
     *                                       常に投げる
     */
    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request,
        final HttpResponse.BodyHandler<T> responseBodyHandler) {

        throw new UnsupportedOperationException();

    }

    /**
     * プッシュ対応の非同期送信（使用しない）<br>
     *
     * @param <T>
     *                            本文の型
     * @param request
     *                            リクエスト
     * @param responseBodyHandler
     *                            本文の変換
     * @param pushPromiseHandler
     *                            プッシュの処理
     *
     * @return 返さない
     *
     * @throws UnsupportedOperationException
     *                                       常に投げる
     */
    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request,
        final HttpResponse.BodyHandler<T> responseBodyHandler,
        final HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {

        throw new UnsupportedOperationException();

    }

    /**
     * send で中断例外を投げるようにする<br>
     *
     * @param exception
     *                  中断例外
     */
    public void setInterruptedException(final InterruptedException exception) {

        this.interruptedException = exception;

    }

    /**
     * SSL コンテキストを返す<br>
     *
     * @return null
     */
    @Override
    public SSLContext sslContext() {

        final SSLContext result = null;
        return result;

    }

    /**
     * SSL パラメータを返す<br>
     *
     * @return null
     */
    @Override
    public SSLParameters sslParameters() {

        final SSLParameters result = null;
        return result;

    }

    /**
     * HTTP のバージョンを返す<br>
     *
     * @return HTTP/1.1
     */
    @Override
    public Version version() {

        final Version result = Version.HTTP_1_1;
        return result;

    }

}
