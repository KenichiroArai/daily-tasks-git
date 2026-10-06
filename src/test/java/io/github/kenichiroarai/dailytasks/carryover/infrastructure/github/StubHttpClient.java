package io.github.kenichiroarai.dailytasks.carryover.infrastructure.github;

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
     * send で中断例外を投げるようにする<br>
     *
     * @param exception
     *                  中断例外
     */
    public void setInterruptedException(final InterruptedException exception) {

        this.interruptedException = exception;

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

        final Object[] response = this.responses.removeFirst();
        final HttpResponse<T> result = (HttpResponse<T>) new StubHttpResponse(request,
            ((Integer) response[0]).intValue(), (String) response[1]);
        return result;

    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request,
        final HttpResponse.BodyHandler<T> responseBodyHandler) {

        throw new UnsupportedOperationException();

    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest request,
        final HttpResponse.BodyHandler<T> responseBodyHandler,
        final HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {

        throw new UnsupportedOperationException();

    }

    @Override
    public Optional<CookieHandler> cookieHandler() {

        return Optional.empty();

    }

    @Override
    public Optional<Duration> connectTimeout() {

        return Optional.empty();

    }

    @Override
    public Redirect followRedirects() {

        return Redirect.NEVER;

    }

    @Override
    public Optional<ProxySelector> proxy() {

        return Optional.empty();

    }

    @Override
    public SSLContext sslContext() {

        return null;

    }

    @Override
    public SSLParameters sslParameters() {

        return null;

    }

    @Override
    public Optional<Authenticator> authenticator() {

        return Optional.empty();

    }

    @Override
    public Version version() {

        return Version.HTTP_1_1;

    }

    @Override
    public Optional<Executor> executor() {

        return Optional.empty();

    }

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
        StubHttpResponse(final HttpRequest request, final int statusCode, final String body) {

            this.request = request;
            this.statusCode = statusCode;
            this.body = body;

        }

        @Override
        public int statusCode() {

            return this.statusCode;

        }

        @Override
        public HttpRequest request() {

            return this.request;

        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {

            return Optional.empty();

        }

        @Override
        public HttpHeaders headers() {

            return HttpHeaders.of(Map.of(), (name, value) -> true);

        }

        @Override
        public String body() {

            return this.body;

        }

        @Override
        public Optional<SSLSession> sslSession() {

            return Optional.empty();

        }

        @Override
        public URI uri() {

            return this.request.uri();

        }

        @Override
        public Version version() {

            return Version.HTTP_1_1;

        }

    }

}
