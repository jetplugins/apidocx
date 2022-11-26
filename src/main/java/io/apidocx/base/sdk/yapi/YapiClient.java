package io.apidocx.base.sdk.yapi;

import static java.lang.String.format;

import io.apidocx.base.sdk.yapi.model.ApiCategory;
import io.apidocx.base.sdk.yapi.model.ApiInterface;
import io.apidocx.base.sdk.yapi.model.CategoryCreateRequest;
import io.apidocx.base.sdk.yapi.model.CreateInterfaceResponseItem;
import io.apidocx.base.sdk.yapi.model.ListInterfaceResponse;
import io.apidocx.base.sdk.yapi.model.LoginRequest;
import io.apidocx.base.sdk.yapi.model.LoginWay;
import io.apidocx.base.sdk.yapi.model.TestResult;
import io.apidocx.base.sdk.yapi.model.TestResult.Code;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Yapi客户端
 */
@Getter
public class YapiClient {

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
    /**
     * 登录方式
     */
    private final LoginWay loginWay;

    private final YapiApi yapiApi;

    /**
     * 项目token
     */
    private String token;

    private String cookies;


    public YapiClient(String url, String account, String password, LoginWay loginWay, String cookies) {
        this.url = url;
        this.account = account;
        this.password = password;
        this.loginWay = loginWay == null ? LoginWay.DEFAULT : loginWay;
        this.cookies = cookies;
        this.yapiApi = createYapiApi(url);
    }

    public YapiClient(String url, String token) {
        this.url = url;
        this.token = token;
        this.yapiApi = createYapiApi(url);
        this.account = null;
        this.password = null;
        this.loginWay = null;
    }

    /**
     * 测试是否正常
     */
    public TestResult test() {
        TestResult result = new TestResult();
        YapiException exception = null;
        boolean usingToken = StringUtils.isNoneEmpty(token);
        try {
            if (usingToken) {
                yapiApi.getProjects();
            } else {
                yapiApi.getUserStatus();
            }
        } catch (YapiException e) {
            exception = e;
        }

        // 强制刷新
        if (!usingToken && exception != null && exception.isNeedAuth()) {
            exception = null;
            try {
                getOrRefreshAccessToken(true);
            } catch (YapiException e) {
                exception = e;
            }
        }

        if (exception == null) {
            result.setCode(Code.OK);
            result.setCookies(this.cookies);
        } else {
            if (exception.isNeedAuth() || exception.isAuthFailed()) {
                result.setCode(Code.AUTH_ERROR);
                result.setMessage(exception.getMessage());
            } else {
                result.setCode(Code.NETWORK_ERROR);
                result.setMessage(exception.getMessage());
            }
        }
        return result;
    }

    /**
     * 获取所有分类
     */
    public List<ApiCategory> getCategories(Integer projectId) {
        return yapiApi.getCategories(projectId).getData();
    }

    /**
     * 新增分类
     */
    public ApiCategory addCategory(CategoryCreateRequest request) {
        return yapiApi.createCategory(request).getData();
    }

    /**
     * 获取单个接口信息
     */
    public ApiInterface getInterface(Integer id) {
        ApiInterface api = yapiApi.getInterface(id).getData();
        if (api != null && api.getId() == null) {
            api.setId(id);
        }
        return api;
    }

    /**
     * 新增分类
     */
    public void saveInterface(ApiInterface api) {
        Integer apiId = api.getId();
        if (apiId == null) {
            Response<List<CreateInterfaceResponseItem>> response = yapiApi.createInterface(api);
            List<CreateInterfaceResponseItem> items = response.getData();
            if (items != null && !items.isEmpty()) {
                api.setId(items.get(0).getId());
            }
        } else {
            yapiApi.updateInterface(api);
        }
    }

    /**
     * 获取接口列表
     */
    public ListInterfaceResponse listInterfaceByCat(Integer catId, int page, int limit) {
        return yapiApi.listInterfaceByCategory(catId, page, limit).getData();
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

    private YapiApi createYapiApi(String url) {
        return YapiApi.feignBuilder()
                .requestInterceptor(template -> {
                    // 请求设置鉴权信息
                    boolean isLogin = YapiConstants.isLoginPath(template.path());
                    if (!isLogin) {
                        if (StringUtils.isNotEmpty(this.token)) {
                            template.query("token", this.token);
                        } else {
                            template.header("cookie", getOrRefreshAccessToken(false));
                        }
                    }
                })
                .responseInterceptor(ctx -> {
                    // 登录存储cookie
                    String path = InternalUtils.getUrlPath(ctx.response().request().url());
                    if (YapiConstants.isLoginPath(path)) {
                        Collection<String> setCookies = ctx.response().headers().get("set-cookie");
                        this.cookies = InternalUtils.parseCookie(setCookies);
                    }
                    // 响应异常转换
                    Object value = ctx.proceed();
                    if (value instanceof Response) {
                        Response<?> responseValue = (Response<?>) value;
                        if (!responseValue.isSuccess()) {
                            throw new YapiException(path, responseValue.getErrorCode(), responseValue.getErrorMessage());
                        }
                    }
                    return value;
                })
                .errorDecoder((methodKey, response) -> {
                    String path = InternalUtils.getUrlPath(response.request().url());
                    return new YapiException(path, response.status(), response.reason());
                })
                .target(YapiApi.class, url);
    }

    private String getOrRefreshAccessToken(boolean force) {
        if (!force && this.cookies != null && !this.cookies.isEmpty()) {
            return this.cookies;
        }
        LoginRequest loginRequest = LoginRequest.builder().email(this.account).password(this.password).build();
        if (loginWay == LoginWay.LDAP) {
            yapiApi.loginLdap(loginRequest);
        } else {
            yapiApi.login(loginRequest);
        }
        return this.cookies;
    }

}
