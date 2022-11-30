package io.apidocx.base.sdk.apifox;

import feign.RetryableException;
import io.apidocx.base.sdk.apifox.model.ApiDetail;
import io.apidocx.base.sdk.apifox.model.ApiFolder;
import io.apidocx.base.sdk.apifox.model.ApiTreeItem;
import io.apidocx.base.sdk.apifox.model.CreateFolderRequest;
import io.apidocx.base.sdk.apifox.model.LoginRequest;
import io.apidocx.base.sdk.apifox.model.LoginResponse;
import io.apidocx.base.sdk.apifox.model.LoginType;
import io.apidocx.base.sdk.apifox.model.TestResult;
import io.apidocx.base.sdk.apifox.model.TestResult.Code;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Apifox客户端
 */
public class ApifoxClient {
    private final String url;
    private final String account;
    private final String password;
    private final ApifoxApi apifoxApi;
    private final Long projectId;
    private String accessToken;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PROJECT_ID_HEADER = "X-Project-Id";
    private static final String LOGIN_PATH = "/api/v1/login";

    public ApifoxClient(String url, String account, String password, String accessToken, Long projectId) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.accessToken = accessToken;
        this.projectId = projectId;
        this.apifoxApi = createApiClient(this.url);
    }

    public TestResult test() {
        TestResult result = new TestResult();
        try {
            apifoxApi.getCurrentUser();
            result.setCode(Code.OK);
            result.setAccessToken(this.accessToken);
        } catch (ApifoxException e) {
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
     * 获取接口目录列表
     */
    public List<ApiFolder> getApiFolders(Long projectId) {
        List<ApiFolder> folders = apifoxApi.getApiFolders(projectId).getData();
        if (folders == null) {
            folders = Collections.emptyList();
        }
        return folders.stream().filter(f -> !f.isRoot()).collect(Collectors.toList());
    }

    /**
     * 创建接口目录
     */
    public ApiFolder createApiFolder(CreateFolderRequest request) {
        Response<ApiFolder> response = apifoxApi.createApiFolders(request);
        return response.getData();
    }

    /**
     * 获取接口详情
     */
    public ApiDetail getApiDetail(Long id) {
        return apifoxApi.getApiDetail(id).getData();
    }

    /**
     * 添加或修改接口
     */
    public Long saveApiDetail(ApiDetail request) {
        Map<String, String> map = InternalUtils.beanToMap(request);
        if (request.getId() == null) {
            Response<ApiDetail> response = apifoxApi.createApiDetail(map);
            return response.getData().getId();
        } else {
            apifoxApi.updateApiDetail(request.getId(), map);
            return request.getId();
        }
    }

    /**
     * 获取接口树列表
     */
    public List<ApiTreeItem> getApiTreeList(Long projectId) {
        return apifoxApi.getApiTreeList(projectId).getData();
    }

    private ApifoxApi createApiClient(String url) {
        return ApifoxApi.feignBuilder()
                .requestInterceptor(template -> {
                    Map<String, Collection<String>> headers = template.headers();
                    if (projectId != null && !headers.containsKey(PROJECT_ID_HEADER)) {
                        template.header(PROJECT_ID_HEADER, String.valueOf(projectId));
                    }
                    if (!LOGIN_PATH.equals(template.path())) {
                        template.header(AUTHORIZATION_HEADER, getOrRefreshAccessToken(false));
                    }
                })
                .responseInterceptor(ctx -> {
                    Object value = ctx.proceed();
                    if (value instanceof Response) {
                        Response<?> response = (Response<?>) value;
                        if (!response.isSuccess()) {
                            String path = InternalUtils.getUrlPath(ctx.response().request().url());
                            throw new ApifoxException(path, response.getErrorCode(), response.getErrorMessage());
                        }
                    }
                    return value;
                })
                .errorDecoder((methodKey, response) -> {
                    if (response.status() == 401) {
                        getOrRefreshAccessToken(true);
                        return new RetryableException(response.status(), "Unauthorized", response.request().httpMethod(), null, response.request());
                    }
                    String path = InternalUtils.getUrlPath(response.request().url());
                    return new ApifoxException(path, response.status() + "", response.reason());
                })
                .target(ApifoxApi.class, url);
    }

    private String getOrRefreshAccessToken(boolean force) {
        if (force || accessToken == null) {
            LoginType loginType = StringUtils.isNumeric(this.account) ? LoginType.MobilePassword : LoginType.EmailPassword;
            String account = this.account;
            if (loginType == LoginType.MobilePassword) {
                account = "+86 " + account;
            }
            LoginRequest loginRequest = LoginRequest.builder()
                    .loginType(loginType.name())
                    .account(account)
                    .password(this.password)
                    .build();
            Response<LoginResponse> response = apifoxApi.login(loginRequest);
            LoginResponse loginResponse = response.getData();
            if (!response.isSuccess()) {
                throw new ApifoxException(LOGIN_PATH, response.getErrorCode(), response.getErrorMessage());
            }
            this.accessToken = loginResponse.getAccessToken();
        }
        return this.accessToken;
    }

}
