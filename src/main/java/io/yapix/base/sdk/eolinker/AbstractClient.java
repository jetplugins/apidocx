package io.yapix.base.sdk.eolinker;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.yapix.base.sdk.eolinker.request.LoginResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("ALL")
public abstract class AbstractClient implements Closeable {

    protected volatile HttpSession authSession;
    protected CloseableHttpClient httpClient;

    public AbstractClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(15000)
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .build();
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 刷新登录信息
     */
    public abstract void doFreshAuth();

    /**
     * 处理网络请求响应
     */
    public abstract String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException;

    /**
     * 执行yapi请求，包括认证功能.
     */
    protected String doRequest(HttpUriRequest request, boolean retry) {
        freshAuth(false);
        if (this.authSession != null) {
            request.addHeader("Authorization", this.authSession.getCookies());
        }
        try {
            return execute(request, false);
        } catch (EolinkerException e) {
            if (!retry || !e.isNeedAuth()) {
                throw e;
            }
        }
        // 再执行一次
        freshAuth(true);
        request.addHeader("Authorization", this.authSession.getCookies());
        return execute(request, false);
    }

    private void setRequestSpaceKey(HttpUriRequest request) {
        if (this.authSession == null || StringUtils.isEmpty(this.authSession.spaceKey)) {
            return;
        }

        if (request instanceof HttpPost) {
            HttpPost postRequest = (HttpPost) request;
            HttpEntity entity = (postRequest).getEntity();
            if (entity != null && entity instanceof CustomUrlEncodedFormEntity) {
                CustomUrlEncodedFormEntity newEntity = ((CustomUrlEncodedFormEntity) entity)
                        .getNewFormEntity(new BasicNameValuePair("spaceKey", this.authSession.spaceKey));
                postRequest.setEntity(newEntity);
            }
        }
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
        setRequestSpaceKey(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String data = doHandleResponse(request, response);
            if (isStoreAuth) {
                this.authSession = getSession(response);
                LoginResponse loginResponse = (new Gson()).fromJson(data, LoginResponse.class);
                this.authSession.spaceKey = loginResponse.getSpaceKey();
            }
            return data;
        } catch (IOException e) {
            throw new EolinkerException(request.getURI().getPath(), e.getMessage(), e);
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

    public HttpSession getAuthSession() {
        return authSession;
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
            sb.append(cookie.value().trim());
        }
        HttpSession session = new HttpSession();
        session.setCookies(sb.toString());
        return session;
    }


    public static class HttpSession {

        private String cookies;
        private Long cookiesTtl;
        private String spaceKey;

        public HttpSession() {
        }

        public HttpSession(String cookies, Long cookiesTtl, String spaceKey) {
            this.cookies = cookies;
            this.cookiesTtl = cookiesTtl;
            this.spaceKey = spaceKey;
        }

        public boolean isValid() {
            return StringUtils.isNotEmpty(cookies);
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

        public String getSpaceKey() {
            return spaceKey;
        }

        public void setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
        }
    }

    /**
     * 自定义
     */
    public static class CustomUrlEncodedFormEntity extends UrlEncodedFormEntity {

        private final Charset charset;

        public CustomUrlEncodedFormEntity(Iterable<? extends NameValuePair> parameters, Charset charset) {
            super(parameters, charset);
            this.charset = charset;
        }

        public CustomUrlEncodedFormEntity getNewFormEntity(NameValuePair pair) {

            List<NameValuePair> pairs = null;
            try {
                pairs = URLEncodedUtils.parse(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<NameValuePair> parameters = Lists.newArrayListWithExpectedSize(pairs.size() + 1);
            for (NameValuePair item : pairs) {
                if (!Objects.equals(item.getName(), pair.getName())) {
                    parameters.add(item);
                } else {
                    parameters.add(pair);
                }
            }
            return new CustomUrlEncodedFormEntity(parameters, this.charset);
        }
    }
}
