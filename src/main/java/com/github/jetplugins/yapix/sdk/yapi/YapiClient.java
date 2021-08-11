package com.github.jetplugins.yapix.sdk.yapi;

import static java.lang.String.format;

import com.github.jetplugins.yapix.sdk.yapi.mode.AuthCookies;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiCategory;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiCategoryAddRequest;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiInterface;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiListInterfaceResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Yapi客户端
 */
public class YapiClient implements Closeable {

    /**
     * 服务地址
     */
    private final String url;
    /**
     * 账户
     */
    private final String account;
    /**
     * 密码
     */
    private final String password;

    private volatile String cookies;
    private volatile long cookiesTtl;
    private final CloseableHttpClient httpClient;
    private static final Gson gson = new Gson();

    public YapiClient(String url, String account, String password) {
        this(url, account, password, null, 0);
    }

    public YapiClient(String url, String account, String password, String cookies, long cookiesTtl) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.cookies = cookies;
        this.cookiesTtl = cookiesTtl;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    public AuthCookies getAuthCookies() {
        return new AuthCookies(this.cookies, this.cookiesTtl);
    }

    /**
     * 获取项目访问授权token
     */
    public String getProjectToken(int projectId) {
        String path = "/api/project/token?project_id=" + projectId;
        String json = requestGet(path);
        return gson.fromJson(json, String.class);
    }

    /**
     * 获取所有分类
     */
    public List<YapiCategory> getCategories(int projectId) {
        String path = format("%s?project_id=%d", YapiConstants.yapiCatMenu, projectId);
        String data = requestGet(path);
        return gson.fromJson(data, new TypeToken<List<YapiCategory>>() {
        }.getType());
    }

    /**
     * 新增分类
     */
    public YapiCategory addCategory(YapiCategoryAddRequest request) {
        String data = requestPost(YapiConstants.yapiAddCat, request);
        return gson.fromJson(data, YapiCategory.class);
    }

    /**
     * 获取单个接口信息
     */
    public YapiInterface getInterface(int id) {
        String path = format("%s?id=%d", YapiConstants.yapiGet, id);
        String data = requestGet(path);
        return gson.fromJson(data, YapiInterface.class);
    }

    /**
     * 新增分类
     */
    public void saveInterface(YapiInterface request) {
        requestPost(YapiConstants.yapiSave, request);
    }

    /**
     * 获取接口列表
     */
    public YapiListInterfaceResponse listInterfaceByCat(String catId, int page, int limit) {
        String path = format("%s?catid=%s&page=%d&limit=%d", YapiConstants.yapiListByCatId, encodeUri(catId), page,
                limit);
        String data = requestGet(path);
        return gson.fromJson(data, YapiListInterfaceResponse.class);
    }

    /**
     * 计算类别地址
     */
    public String calculateCatUrl(Integer projectId, String catId) {
        return format("%s/project/%d/interface/api/cat_%s", url, projectId, catId);
    }

    /**
     * 计算接口访问地址
     */
    public String calculateInterfaceUrl(Integer projectId, String id) {
        return format("%s/project/%d/interface/api/%s", url, projectId, id);
    }

    /**
     * 执行Get请求
     */
    public String requestGet(String path) {
        HttpGet request = new HttpGet(this.url + path);
        return doRequest(request);
    }

    /**
     * 执行Post请求
     */
    public String requestPost(String path, Object data) {
        String json = gson.toJson(data);
        HttpPost request = new HttpPost(url + path);
        request.setHeader("Content-type", "application/json;charset=utf-8");
        request.setEntity(new StringEntity(json == null ? "" : json, StandardCharsets.UTF_8));
        return doRequest(request);
    }

    @Override
    public void close() throws IOException {
        this.httpClient.close();
    }

    /**
     * 执行yapi请求，包括认证功能.
     */
    private String doRequest(HttpUriRequest request) {
        freshAuth(false);
        request.addHeader("Cookie", this.cookies);
        try {
            return execute(request, false);
        } catch (YapiException e) {
            if (!Integer.valueOf(40011).equals(e.getCode())) {
                throw e;
            }
        }
        // 再执行一次
        freshAuth(true);
        request.addHeader("Cookie", this.cookies);
        return execute(request, false);
    }

    /**
     * 刷新登录认证信息
     */
    private void freshAuth(boolean force) {
        if (!force && cookiesTtl > System.currentTimeMillis() && StringUtils.isNotEmpty(this.cookies)) {
            return;
        }
        synchronized (this) {
            if (!force && cookiesTtl > System.currentTimeMillis() && StringUtils.isNotEmpty(this.cookies)) {
                return;
            }
            String path = "/api/user/login";
            JsonObject params = new JsonObject();
            params.addProperty("email", this.account);
            params.addProperty("password", this.password);
            String json = gson.toJson(params);
            HttpPost request = new HttpPost(url + path);
            request.setHeader("Content-type", "application/json;charset=utf-8");
            request.setEntity(new StringEntity(json == null ? "" : json, StandardCharsets.UTF_8));
            execute(request, true);
        }
    }

    private String execute(HttpUriRequest request, boolean isStoreAuth) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity resEntity = response.getEntity();
            String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
            YapiResponse yapiResponse = gson.fromJson(content, YapiResponse.class);
            if (!yapiResponse.isOk()) {
                throw new YapiException(request.getURI().getPath(), yapiResponse.getErrcode(),
                        yapiResponse.getErrmsg());
            }
            if (isStoreAuth) {
                storeCookie(response);
            }
            if (yapiResponse.getData() == null) {
                return null;
            }
            return gson.toJson(yapiResponse.getData());
        } catch (IOException e) {
            throw new YapiException(request.getURI().getPath(), e.getMessage(), e);
        }
    }

    private void storeCookie(CloseableHttpResponse httpResponse) {
        StringBuilder sb = new StringBuilder();
        Header[] headers = httpResponse.getHeaders("set-cookie");
        for (Header header : headers) {
            Cookie cookie = ClientCookieDecoder.STRICT.decode(header.getValue());
            sb.append(cookie.name()).append("=").append(cookie.value()).append(";");
            if ("_yapi_token".equals(cookie.name())) {
                this.cookiesTtl = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cookie.maxAge() - 20);
            }
        }
        this.cookies = sb.toString();
    }

    private String encodeUri(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("It's impossible", e);
        }
    }
}
