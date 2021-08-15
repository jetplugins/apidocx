package io.yapix.base.sdk.yapi;

import static java.lang.String.format;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.yapix.base.sdk.yapi.mode.AuthCookies;
import io.yapix.base.sdk.yapi.mode.YapiCategory;
import io.yapix.base.sdk.yapi.mode.YapiCategoryAddRequest;
import io.yapix.base.sdk.yapi.mode.YapiInterface;
import io.yapix.base.sdk.yapi.mode.YapiListInterfaceResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    private final Gson gson = new Gson();

    public YapiClient(String url, String account, String password) {
        this(url, account, password, null, 0);
    }

    public YapiClient(String url, String account, String password, String cookies, long cookiesTtl) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.authSession = new AbstractClient.HttpSession(cookies, cookiesTtl);
    }

    public AuthCookies getAuthCookies() {
        if (this.authSession == null) {
            return null;
        }
        return new AuthCookies(this.authSession.getCookies(), this.authSession.getCookiesTtl());
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
    void doFreshAuth(boolean force) {
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

    @Override
    String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException {
        HttpEntity resEntity = response.getEntity();
        String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
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
