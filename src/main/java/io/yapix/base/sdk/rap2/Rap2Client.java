package io.yapix.base.sdk.rap2;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.yapix.base.sdk.rap2.model.Rap2Interface;
import io.yapix.base.sdk.rap2.model.Rap2InterfaceBase;
import io.yapix.base.sdk.rap2.model.Rap2Module;
import io.yapix.base.sdk.rap2.model.Rap2Repository;
import io.yapix.base.sdk.rap2.model.Rap2User;
import io.yapix.base.sdk.rap2.request.CreateModuleRequest;
import io.yapix.base.sdk.rap2.request.LoginRequest;
import io.yapix.base.sdk.rap2.request.UpdatePropertiesRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Rap2客户端
 */
public class Rap2Client extends AbstractClient {

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

    /** 当前用户信息 */
    private volatile Rap2User currentUser;

    /** 验证码 */
    private String captcha;

    /** 验证码会话 */
    private HttpSession captchaSession;

    private final Gson gson = new GsonBuilder().serializeNulls().create();


    public Rap2Client(String url, String account, String password) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.url = url;
        this.account = account;
        this.password = password;

    }

    public Rap2Client(String url, String cookies, long cookiesTtl, Rap2User user) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        this.url = url;
        this.account = null;
        this.password = null;
        this.authSession = new HttpSession(cookies, cookiesTtl);
        this.currentUser = user;
    }

    /**
     * 获取验证码
     */
    public byte[] getCaptcha() {
        String url = this.url + Rap2Constants.GetCaptcha;
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity resEntity = response.getEntity();
            byte[] bytes = IOUtils.readFully(resEntity.getContent(), (int) resEntity.getContentLength());
            this.captchaSession = getSession(response);
            return bytes;
        } catch (IOException e) {
            throw new Rap2Exception(request.getURI().getPath(), e.getMessage(), e);
        }
    }

    /**
     * 登录
     */
    public void login(String captcha) {
        this.captcha = captcha;
        freshAuth(true);
    }

    /**
     * 获取仓库信息，包括模块信息
     */
    public Rap2Repository getRepository(long id) {
        String path = Rap2Constants.GetRepositoryPath + String.format("?id=%d&excludeProperty=true", id);
        String data = requestGet(path);
        return gson.fromJson(data, Rap2Repository.class);
    }

    /**
     * 获取仓库模块信息
     */
    public List<Rap2Module> getModules(long id) {
        Rap2Repository repository = getRepository(id);
        if (repository == null || repository.getModules() == null) {
            return Collections.emptyList();
        }
        return repository.getModules();
    }

    /**
     * 新增分类
     */
    public Rap2Module createModule(CreateModuleRequest request) {
        checkArgument(request.getRepositoryId() != null, "repositoryId can't be null");
        checkArgument(StringUtils.isNotEmpty(request.getName()), "name must not be empty");

        Rap2Module module = new Rap2Module();
        module.setRepositoryId(request.getRepositoryId());
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setCreatorId(this.currentUser.getId());
        module.setId(0L);
        module.setPriority(0L);
        String data = requestPost(Rap2Constants.CreateModulePath, module);
        return gson.fromJson(data, Rap2Module.class);
    }

    /**
     * 获取接口信息
     */
    public Rap2Interface getInterface(long id) {
        String path = Rap2Constants.GetInterfacePath + String.format("?id=%d", id);
        String data = requestGet(path);
        return gson.fromJson(data, Rap2Interface.class);
    }

    /**
     * 创建接口
     */
    public Rap2InterfaceBase createInterface(Rap2InterfaceBase request) {
        checkArgument(request.getRepositoryId() != null, "repositoryId can't be null");
        checkArgument(request.getModuleId() != null, "moduleId can't be null");

        String data = requestPost(Rap2Constants.CreateInterfacePath, request);
        return gson.fromJson(data, Rap2Interface.class);
    }

    /**
     * 更新接口
     */
    public Rap2InterfaceBase updateInterface(Rap2InterfaceBase request) {
        checkArgument(request.getId() != null, "id can't be null");
        checkArgument(request.getRepositoryId() != null, "repositoryId can't be null");
        checkArgument(request.getModuleId() != null, "moduleId can't be null");

        String data = requestPost(Rap2Constants.UpdateInterfacePath, request);
        return gson.fromJson(data, Rap2Interface.class);
    }

    /**
     * 更新接口
     */
    public Rap2InterfaceBase updateInterfaceProperties(UpdatePropertiesRequest request) {
        checkArgument(request.getInterfaceId() != null, "interfaceId can't be null");

        String data = requestPost(Rap2Constants.UpdateInterfacePropertiesPath, request);
        return gson.fromJson(data, Rap2Interface.class);
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
        LoginRequest authInfo = new LoginRequest();
        authInfo.setEmail(this.account);
        authInfo.setPassword(this.password);
        authInfo.setCaptcha(this.captcha);
        String json = gson.toJson(authInfo);
        HttpPost request = new HttpPost(url + Rap2Constants.LoginPath);
        if (this.captchaSession != null) {
            request.addHeader("Cookie", this.captchaSession.getCookies());
        }
        request.setEntity(new StringEntity(json == null ? "" : json, StandardCharsets.UTF_8));
        String userInfo = execute(request, true);
        this.currentUser = gson.fromJson(userInfo, Rap2User.class);
    }

    @Override
    String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException {
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

        return gson.toJson(data);
    }
}
