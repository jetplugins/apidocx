package io.yapix.base.sdk.yapi;

import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@SuppressWarnings("ALL")
public abstract class AbstractClient implements Closeable {

    protected volatile HttpSession authSession;
    protected CloseableHttpClient httpClient;

    public AbstractClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 刷新登录信息
     *
     * @param force
     */
    abstract void doFreshAuth();

    /** 处理网络请求响应 */
    abstract String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException;

    /**
     * 执行yapi请求，包括认证功能.
     */
    protected String doRequest(HttpUriRequest request) {
        freshAuth(false);
        if (this.authSession != null) {
            request.addHeader("Cookie", this.authSession.getCookies());
        }
        YapiException exception = null;
        try {
            return execute(request, false);
        } catch (YapiException e) {
            if (!e.isNeedAuth()) {
                throw e;
            }
            exception = e;
        }
        freshAuth(true);

        // 不需要再次登录
        if (this.authSession == null && exception != null) {
            throw exception;
        }

        // 再执行一次
        if (this.authSession != null) {
            request.addHeader("Cookie", this.authSession.getCookies());
        }
        return execute(request, false);
    }

    /**
     * 刷新登录认证信息
     */
    protected void freshAuth(boolean force) {
        if (!force && authSession != null && authSession.isValid()) {
            return;
        }
        synchronized (this) {
            if (!force && authSession != null && authSession.isValid()) {
                return;
            }
            doFreshAuth();
        }
    }

    /**
     * 执行网络请求
     */
    protected String execute(HttpUriRequest request, boolean isStoreAuth) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (isStoreAuth) {
                this.authSession = getSession(response);
            }
            return doHandleResponse(request, response);
        } catch (IOException e) {
            throw new YapiException(request.getURI().getPath(), e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }


    protected String encodeUri(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("It's impossible", e);
        }
    }

    protected HttpSession getSession(CloseableHttpResponse httpResponse) {
        StringBuilder sb = new StringBuilder();
        Long ttl = null;
        Header[] headers = httpResponse.getHeaders("set-cookie");
        for (int i = 0; i < headers.length; i++) {
            Cookie cookie = ClientCookieDecoder.STRICT.decode(headers[i].getValue());
            sb.append(cookie.name().trim()).append("=").append(cookie.value().trim());
            if (i != headers.length - 1) {
                sb.append("; ");
            }
            if (ttl == null) {
                ttl = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cookie.maxAge() - 20);
            }
        }
        HttpSession session = new HttpSession();
        session.setCookies(sb.toString());
        session.setCookiesTtl(ttl);
        return session;
    }

    public static class HttpSession {

        private String cookies;
        private Long cookiesTtl;

        public HttpSession() {
        }

        public HttpSession(String cookies, long cookiesTtl) {
            this.cookies = cookies;
            this.cookiesTtl = cookiesTtl;
        }

        public boolean isValid() {
            return StringUtils.isNotEmpty(cookies) && cookiesTtl != null && cookiesTtl > System.currentTimeMillis();
        }

        //-------------------generated-----------------//

        public String getCookies() {
            return cookies;
        }

        public void setCookies(String cookies) {
            this.cookies = cookies;
        }

        public Long getCookiesTtl() {
            return cookiesTtl;
        }

        public void setCookiesTtl(Long cookiesTtl) {
            this.cookiesTtl = cookiesTtl;
        }
    }
}
