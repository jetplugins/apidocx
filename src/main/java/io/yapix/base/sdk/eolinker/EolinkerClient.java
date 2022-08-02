package io.yapix.base.sdk.eolinker;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import io.yapix.base.sdk.eolinker.model.EolinkerApiGroup;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.model.EolinkerUserInfo;
import io.yapix.base.sdk.eolinker.request.ApiListRequest;
import io.yapix.base.sdk.eolinker.request.ApiListResponse;
import io.yapix.base.sdk.eolinker.request.ApiRequest;
import io.yapix.base.sdk.eolinker.request.ApiResponse;
import io.yapix.base.sdk.eolinker.request.ApiSaveRequest;
import io.yapix.base.sdk.eolinker.request.ApiSaveResponse;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult.Code;
import io.yapix.base.sdk.eolinker.request.GetUserInfoResponse;
import io.yapix.base.sdk.eolinker.request.GroupAddRequest;
import io.yapix.base.sdk.eolinker.request.GroupAddResponse;
import io.yapix.base.sdk.eolinker.request.GroupListRequest;
import io.yapix.base.sdk.eolinker.request.GroupListResponse;
import io.yapix.base.sdk.eolinker.request.LoginRequest;
import io.yapix.base.sdk.eolinker.request.Response;
import io.yapix.base.sdk.eolinker.util.ApiConverter;
import io.yapix.base.sdk.eolinker.util.InternalUtils;
import io.yapix.base.util.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Eolinker客户端
 */
public class EolinkerClient extends AbstractClient {

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

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private EolinkerUserInfo userInfo;

    public EolinkerClient(String url, String account, String password, HttpSession authSession) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.authSession = authSession;
    }

    /**
     * 测试是否正常
     */
    public EolinkerTestResult test() {
        EolinkerTestResult result = new EolinkerTestResult();
        try {
            requestPost(EolinkerConstants.GetUserInfo, null);
            result.setCode(Code.OK);
            result.setAuthSession(this.authSession);
        } catch (EolinkerException e) {
            result.setMessage(e.getMessage());
            if (e.isNeedAuth() || e.isAccountPasswordError()) {
                result.setCode(Code.AUTH_ERROR);
            } else {
                result.setCode(Code.NETWORK_ERROR);
            }
        }
        return result;
    }

    /**
     * 添加分组
     */
    public Long addGroup(GroupAddRequest request) {
        request.setSpaceKey(getSpaceKey());
        String json = requestPost(EolinkerConstants.AddGroup, request);
        GroupAddResponse response = gson.fromJson(json, GroupAddResponse.class);
        return response.getGroupID();
    }

    /**
     * 获取分组列表
     */
    public List<EolinkerApiGroup> getGroupList(String projectHashKey) {
        GroupListRequest request = new GroupListRequest();
        request.setProjectHashKey(projectHashKey);
        request.setSpaceKey(getSpaceKey());
        String json = requestPost(EolinkerConstants.GetGroupList, request);
        GroupListResponse response = gson.fromJson(json, GroupListResponse.class);
        return response.getApiGroupData() != null ? response.getApiGroupData() : Collections.emptyList();
    }

    /**
     * 获取接口列表
     */
    public List<EolinkerApiBase> getApiList(String projectHashKey, Long groupId) {
        ApiListRequest request = new ApiListRequest();
        request.setSpaceKey(getSpaceKey());
        request.setGroupID(groupId);
        request.setProjectHashKey(projectHashKey);
        request.setPage(1);
        request.setPageSize(1000);

        String json = requestPost(EolinkerConstants.GetApiList, request);
        ApiListResponse response = gson.fromJson(json, ApiListResponse.class);
        return response.getApiList();
    }

    /**
     * 获取接口信息
     */
    public EolinkerApiInfo getApi(String projectHashKey, Long apiId) {
        ApiRequest request = new ApiRequest();
        request.setSpaceKey(getSpaceKey());
        request.setProjectHashKey(projectHashKey);
        request.setApiID(apiId);
        String json = requestPost(EolinkerConstants.GetApi, request);
        ApiResponse response = gson.fromJson(json, ApiResponse.class);
        return response.getApiInfo();
    }

    /**
     * 保存接口
     */
    public ApiSaveResponse saveApi(String projectHashKey, EolinkerApiInfo api) {
        ApiSaveRequest request = ApiConverter.convertApiSaveRequest(projectHashKey, api);
        request.setSpaceKey(getSpaceKey());
        if (request.getApiID() == null) {
            return doAddApi(request);
        } else {
            return doEditApi(request);
        }
    }


    /**
     * 添加接口
     */
    private ApiSaveResponse doAddApi(ApiSaveRequest api) {
        String json = requestPost(EolinkerConstants.AddApi, api);
        return gson.fromJson(json, ApiSaveResponse.class);
    }

    /**
     * 编辑接口
     */
    private ApiSaveResponse doEditApi(ApiSaveRequest api) {
        String json = requestPost(EolinkerConstants.EditApi, api);
        return gson.fromJson(json, ApiSaveResponse.class);
    }

    /**
     * 执行Post请求
     */
    public String requestPost(String path, Object data) {
        HttpPost request = new HttpPost(url + path);
        if (data != null) {
            request.setEntity(InternalUtils.beanToFormEntity(data));
        }
        return doRequest(request, true);
    }

    private String getSpaceKey() {
        if (this.userInfo != null) {
            return this.userInfo.getSpaceKey();
        }
        synchronized (this) {
            if (this.userInfo != null) {
                return this.userInfo.getSpaceKey();
            }
            String json = requestPost(EolinkerConstants.GetUserInfo, null);
            GetUserInfoResponse userInfoResponse = JsonUtils.fromJson(json, GetUserInfoResponse.class);
            this.userInfo = userInfoResponse.getUserInfo();
        }
        return this.userInfo.getSpaceKey();
    }

    @Override
    public void doFreshAuth() {
        LoginRequest user = new LoginRequest();
        // Note: 只允许一个端在线，所以此处使用1为客户端，避免使用0网页端
        user.setClient(1);
        user.setPassword(this.password);
        user.setUsername(this.account);
        String verifyCode = DateFormatUtils.format(new Date(), "EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        user.setVerifyCode(InternalUtils.md5(verifyCode));

        HttpPost request = new HttpPost(url + EolinkerConstants.Login);
        request.setEntity(new StringEntity(JsonUtils.toJson(user), ContentType.APPLICATION_JSON));
        execute(request, true);
    }

    @Override
    public String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException {
        HttpEntity resEntity = response.getEntity();
        String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            throw new EolinkerException(request.getURI().getPath(), null, content);
        }
        JsonObject result = gson.fromJson(content, JsonObject.class);
        JsonElement statusCodeElement = result.get("statusCode");
        if (statusCodeElement != null) {
            // 常规接口
            String resultCode = statusCodeElement.getAsString();
            if (!Response.isOkCode(resultCode)) {
                throw new EolinkerException(request.getURI().getPath(), resultCode);
            }
        } else {
            // 登录接口
            JsonElement codeElement = result.get("code");
            String code = codeElement.getAsString();
            if (!code.equals("0")) {
                String message = result.get("message").getAsString();
                throw new EolinkerException(request.getURI().getPath(), code, message);
            }
        }
        return content;
    }

}
