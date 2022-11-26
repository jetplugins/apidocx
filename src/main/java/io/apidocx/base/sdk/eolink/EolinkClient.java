package io.apidocx.base.sdk.eolink;

import static com.google.common.base.Preconditions.checkArgument;

import io.apidocx.base.sdk.eolink.model.ApiBase;
import io.apidocx.base.sdk.eolink.model.ApiGroup;
import io.apidocx.base.sdk.eolink.model.ApiInfo;
import io.apidocx.base.sdk.eolink.model.UserInfo;
import io.apidocx.base.sdk.eolink.request.ApiListRequest;
import io.apidocx.base.sdk.eolink.request.ApiListResponse;
import io.apidocx.base.sdk.eolink.request.ApiRequest;
import io.apidocx.base.sdk.eolink.request.ApiResponse;
import io.apidocx.base.sdk.eolink.request.ApiSaveRequest;
import io.apidocx.base.sdk.eolink.request.ApiSaveResponse;
import io.apidocx.base.sdk.eolink.request.GetUserInfoResponse;
import io.apidocx.base.sdk.eolink.request.GroupAddRequest;
import io.apidocx.base.sdk.eolink.request.GroupAddResponse;
import io.apidocx.base.sdk.eolink.request.GroupListRequest;
import io.apidocx.base.sdk.eolink.request.GroupListResponse;
import io.apidocx.base.sdk.eolink.request.LoginRequest;
import io.apidocx.base.sdk.eolink.request.LoginResponseData;
import io.apidocx.base.sdk.eolink.request.Response;
import io.apidocx.base.sdk.eolink.request.SsoResponse;
import io.apidocx.base.sdk.eolink.request.TestResult;
import io.apidocx.base.sdk.eolink.request.TestResult.Code;
import io.apidocx.base.sdk.eolink.util.ApiConverter;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Eolink客户端
 */
public class EolinkClient {

    private final EolinkApi eolinkApi;

    private final String loginUrl;

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

    @Getter
    private String accessToken;

    private UserInfo userInfo;

    public EolinkClient(String url, String loginUrl, String account, String password, String accessToken) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.loginUrl = loginUrl;
        this.url = url;
        this.account = account;
        this.password = password;
        this.accessToken = accessToken;
        this.eolinkApi = createApiClient(this.url);
    }

    /**
     * 测试是否正常
     */
    public TestResult test() {
        TestResult result = new TestResult();
        EolinkException exception = null;
        try {
            eolinkApi.getCurrentUserInfo();
        } catch (EolinkException e) {
            exception = e;
        }

        // 强制刷新，重试一次
        if (exception != null && exception.isNeedAuth()) {
            exception = null;
            try {
                getOrRefreshAccessToken(true);
            } catch (EolinkException e) {
                exception = e;
            }
        }

        if (exception == null) {
            result.setCode(Code.OK);
            result.setCookies(this.accessToken);
        } else {
            result.setMessage(exception.getMessage());
            if (exception.isNeedAuth() || exception.isAccountPasswordError()) {
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
    public Long createGroup(GroupAddRequest request) {
        request.setSpaceKey(getSpaceKey());
        GroupAddResponse response = eolinkApi.createGroup(request);
        return response.getGroupID();
    }

    /**
     * 获取分组列表
     */
    public List<ApiGroup> getGroupList(String projectHashKey) {
        GroupListRequest request = new GroupListRequest();
        request.setProjectHashKey(projectHashKey);
        request.setSpaceKey(getSpaceKey());

        GroupListResponse response = eolinkApi.getGroupList(request);
        return response.getApiGroupData() != null ? response.getApiGroupData() : Collections.emptyList();
    }

    /**
     * 获取接口列表
     */
    public List<ApiBase> getApiList(String projectHashKey, Long groupId) {
        ApiListRequest request = new ApiListRequest();
        request.setSpaceKey(getSpaceKey());
        request.setGroupID(groupId);
        request.setProjectHashKey(projectHashKey);
        request.setPage(1);
        request.setPageSize(1000);

        ApiListResponse response = eolinkApi.getApiList(request);
        return response.getApiList() != null ? response.getApiList() : Collections.emptyList();
    }

    /**
     * 获取接口信息
     */
    public ApiInfo getApi(String projectHashKey, Long apiId) {
        ApiRequest request = new ApiRequest();
        request.setSpaceKey(getSpaceKey());
        request.setProjectHashKey(projectHashKey);
        request.setApiID(apiId);
        ApiResponse response = eolinkApi.getApi(request);
        return response.getApiInfo();
    }

    /**
     * 保存接口
     */
    public ApiSaveResponse saveApi(String projectHashKey, ApiInfo api) {
        ApiSaveRequest request = ApiConverter.convertApiSaveRequest(projectHashKey, api);
        request.setSpaceKey(getSpaceKey());
        Map<String, String> params = InternalUtils.beanToMap(request);

        if (request.getApiID() == null) {
            return eolinkApi.createApi(params);
        } else {
            return eolinkApi.updateApi(params);
        }
    }

    private String getSpaceKey() {
        if (this.userInfo != null) {
            return this.userInfo.getSpaceKey();
        }
        synchronized (this) {
            if (this.userInfo != null) {
                return this.userInfo.getSpaceKey();
            }
            GetUserInfoResponse userInfoResponse = eolinkApi.getCurrentUserInfo();
            this.userInfo = userInfoResponse.getUserInfo();
        }
        return this.userInfo.getSpaceKey();
    }

    private EolinkApi createApiClient(String url) {
        return EolinkApi.feignBuilder()
                .requestInterceptor(template -> {
                    // 请求设置鉴权信息
                    boolean isLoginRequest = EolinkConstants.isLoginPath(template.url());
                    if (!isLoginRequest) {
                        template.header("Authorization", getOrRefreshAccessToken(false));
                    }
                })
                .responseInterceptor(ctx -> {
                    feign.Response response = ctx.response();
                    String requestUrl = response.request().url();
                    String path = InternalUtils.getUrlPath(requestUrl);
                    // 响应异常转换
                    Object value = ctx.proceed();
                    if (value instanceof Response) {
                        Response responseValue = (Response) value;
                        if (!responseValue.isSuccess()) {
                            throw new EolinkException(path, responseValue.getStatusCode());
                        }
                    }
                    return value;
                })
                .errorDecoder((methodKey, response) -> {
                    String path = InternalUtils.getUrlPath(response.request().url());
                    return new EolinkException(path, response.status() + response.reason());
                })
                .target(EolinkApi.class, url);
    }

    private String getOrRefreshAccessToken(boolean force) {
        if (!force && this.accessToken != null && !this.accessToken.isEmpty()) {
            return this.accessToken;
        }
        LoginResponseData authInfo = doLogin();
        this.accessToken = authInfo.getJwt();
        return this.accessToken;
    }

    private LoginResponseData doLogin() {
        LoginRequest loginREquest = new LoginRequest();
        loginREquest.setPassword(this.password);
        loginREquest.setUsername(this.account);

        URI loginUri = URI.create(this.loginUrl + EolinkConstants.Login);
        SsoResponse<LoginResponseData> response = eolinkApi.login(loginUri, loginREquest);
        return response.getData();
    }
}
