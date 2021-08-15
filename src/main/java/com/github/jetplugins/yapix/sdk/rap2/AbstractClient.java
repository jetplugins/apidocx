package com.github.jetplugins.yapix.sdk.rap2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public abstract class AbstractClient implements Closeable {

    protected volatile HttpSession authSession;
    protected CloseableHttpClient httpClient;
    public static final Gson gson = new GsonBuilder().serializeNulls().create();

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
    abstract void doFreshAuth(boolean force);


    /**
     * 执行yapi请求，包括认证功能.
     */
    protected String doRequest(HttpUriRequest request) {
        freshAuth(false);
        if (this.authSession != null) {
            request.addHeader("Cookie", this.authSession.getCookies());
        }
        try {
            return execute(request, false);
        } catch (Rap2Exception e) {
            if (!e.isNeedAuth()) {
                throw e;
            }
        }
        // 再执行一次
        freshAuth(true);
        request.addHeader("Cookie", this.authSession.getCookies());
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
            doFreshAuth(force);
        }
    }

    /**
     * 执行网络请求
     */
    protected String execute(HttpUriRequest request, boolean isStoreAuth) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity resEntity = response.getEntity();
            String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode > 299) {
                throw new Rap2Exception(request.getURI().getPath(), content);
            }
            JsonObject rap2Response = gson.fromJson(content, JsonObject.class);
            JsonObject data = rap2Response.getAsJsonObject("data");
            if (data == null) {
                JsonElement errMsg = rap2Response.get("errMsg");
                throw new Rap2Exception(request.getURI().getPath(), errMsg.getAsString());
            }
            JsonElement errMsg = data.get("errMsg");
            if (errMsg != null) {
                throw new Rap2Exception(request.getURI().getPath(), errMsg.getAsString());
            }
            if (isStoreAuth) {
                this.authSession = getSession(response);
            }
            return gson.toJson(data);
        } catch (IOException e) {
            throw new Rap2Exception(request.getURI().getPath(), e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }

    protected static HttpSession getSession(CloseableHttpResponse httpResponse) {
        StringBuilder sb = new StringBuilder();
        Long ttl = null;
        Header[] headers = httpResponse.getHeaders("set-cookie");
        for (int i = 0; i < headers.length; i++) {
            Cookie cookie = ClientCookieDecoder.STRICT.decode(headers[i].getValue());
            sb.append(cookie.name()).append("=").append(cookie.value());
            if (i != headers.length - 1) {
                sb.append(";");
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

}
