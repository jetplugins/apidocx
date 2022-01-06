package io.yapix.base.sdk.yapi;

import static java.lang.String.format;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.yapix.base.sdk.yapi.model.AuthCookies;
import io.yapix.base.sdk.yapi.model.YapiCategory;
import io.yapix.base.sdk.yapi.model.YapiCategoryAddRequest;
import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.model.YapiListInterfaceResponse;
import io.yapix.base.sdk.yapi.response.YapiTestResult;
import io.yapix.base.sdk.yapi.response.YapiTestResult.Code;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Yapi客户端
 */
public class YapiClient extends AbstractClient {

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
    /** 登录方式 */
    private final LoginWay loginWay;

    /** 项目token */
    private final String token;

    private final Gson gson = new Gson();

    public YapiClient(String url, String account, String password, LoginWay loginWay,
            String cookies, Long cookiesTtl) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.loginWay = loginWay == null ? LoginWay.DEFAULT : loginWay;
        this.authSession = new AbstractClient.HttpSession(cookies, cookiesTtl);
        this.token = null;
    }

    public YapiClient(String url, String token) {
        this.url = url;
        this.token = token;
        this.account = null;
        this.password = null;
        this.loginWay = null;
    }

    public AuthCookies getAuthCookies() {
        if (this.authSession == null) {
            return null;
        }
        return new AuthCookies(this.authSession.getCookies(), this.authSession.getCookiesTtl());
    }

    /**
     * 测试是否正常
     */
    public YapiTestResult test() {
        String path = StringUtils.isNoneEmpty(token) ? YapiConstants.yapiProjectGet : YapiConstants.yapiUserStatus;
        YapiTestResult result = new YapiTestResult();
        try {
            requestGet(path);
            result.setCode(Code.OK);
            result.setAuthCookies(getAuthCookies());
        } catch (YapiException e) {
            if (e.isNeedAuth() || e.isAuthFailed()) {
                result.setCode(Code.AUTH_ERROR);
                result.setMessage(e.getMessage());
            } else {
                result.setCode(Code.NETWORK_ERROR);
                result.setMessage(e.getMessage());
            }
        }
        return result;
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
        YapiInterface api = gson.fromJson(data, YapiInterface.class);
        if (api != null) {
            api.setId(id);
        }
        return api;
    }

    /**
     * 新增分类
     */
    public void saveInterface(YapiInterface api) {
        String data = requestPost(YapiConstants.yapiSave, api);
        JsonArray dataArray = gson.fromJson(data, JsonArray.class);
        if (dataArray != null && dataArray.size() > 0) {
            int apiId = dataArray.get(0).getAsJsonObject().get("_id").getAsInt();
            api.setId(apiId);
        }
    }

    /**
     * 获取接口列表
     */
    public YapiListInterfaceResponse listInterfaceByCat(Integer catId, int page, int limit) {
        String path = format("%s?catid=%s&page=%d&limit=%d", YapiConstants.yapiListByCatId, encodeUri(String.valueOf(catId)), page,
                limit);
        String data = requestGet(path);
        return gson.fromJson(data, YapiListInterfaceResponse.class);
    }

    /**
     * 计算类别地址
     */
    public String calculateCatUrl(Integer projectId, Integer catId) {
        return format("%s/project/%d/interface/api/cat_%s", url, projectId, catId);
    }

    /**
     * 计算接口访问地址
     */
    public String calculateInterfaceUrl(Integer projectId, Integer id) {
        return format("%s/project/%d/interface/api/%d", url, projectId, id);
    }

    /**
     * 执行Get请求
     */
    public String requestGet(String path) {
        HttpGet request = new HttpGet(this.url + pathWithToken(path));
        return doRequest(request);
    }

    /**
     * 执行Post请求
     */
    public String requestPost(String path, Object data) {
        String json = gson.toJson(data);
        HttpPost request = new HttpPost(url + pathWithToken(path));
        request.setHeader("Content-type", "application/json;charset=utf-8");
        request.setEntity(new StringEntity(json == null ? "" : json, StandardCharsets.UTF_8));
        return doRequest(request);
    }

    private String pathWithToken(String path) {
        if (StringUtils.isEmpty(token)) {
            return path;
        }
        if (path.indexOf('?') == -1) {
            return path + "?token=" + token;
        }
        return path + "&token=" + token;
    }


    @Override
    void doFreshAuth() {
        if (StringUtils.isNotEmpty(token)) {
            return;
        }
        String path = this.loginWay.getPath();
        JsonObject params = new JsonObject();
        params.addProperty("email", this.account);
        params.addProperty("password", this.password);
        String json = gson.toJson(params);
        HttpPost request = new HttpPost(url + path);
        request.setHeader("Content-type", "application/json;charset=utf-8");
        request.setEntity(new StringEntity(json == null ? "" : json, StandardCharsets.UTF_8));
        execute(request, true);
    }

    @Override
    String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException {
        HttpEntity resEntity = response.getEntity();
        String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            throw new YapiException(request.getURI().getPath(), content, null);
        }
        YapiResponse yapiResponse = gson.fromJson(content, YapiResponse.class);
        if (!yapiResponse.isOk()) {
            throw new YapiException(request.getURI().getPath(), yapiResponse.getErrcode(),
                    yapiResponse.getErrmsg());
        }
        if (yapiResponse.getData() == null) {
            return null;
        }
        return gson.toJson(yapiResponse.getData());
    }

}
