package io.apidocx.base.sdk.showdoc;

import static com.google.common.base.Preconditions.checkArgument;

import io.apidocx.base.sdk.showdoc.model.CaptchaResponse;
import io.apidocx.base.sdk.showdoc.model.LoginRequest;
import io.apidocx.base.sdk.showdoc.model.ShowdocProjectToken;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateRequest;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateResponse;
import io.apidocx.base.sdk.showdoc.model.TestResult;
import io.apidocx.base.sdk.showdoc.model.TestResult.Code;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Showdoc客户端
 */
public class ShowdocClient {

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
    private String cookies;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码会话
     */
    private String captchaCookies;

    private final ShowdocApi showdocApi;


    public ShowdocClient(String url, String account, String password) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.showdocApi = createApiClient(url);
    }

    public ShowdocClient(String url, String account, String password, String cookies) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.cookies = cookies;
        this.showdocApi = createApiClient(url);
    }

    /**
     * 测试登录信息
     */
    public TestResult test(String captcha, String captchaCookies) {
        this.captcha = captcha;
        this.captchaCookies = captchaCookies;

        TestResult result = new TestResult();
        ShowdocException exception = null;
        try {
            showdocApi.getCurrentUserInfo(uri(ShowdocConstants.AccountInfoPath));
        } catch (ShowdocException e) {
            exception = e;
        }

        // 强制刷新，重试一次
        if (exception != null && exception.isNeedAuth()) {
            exception = null;
            try {
                getOrRefreshAccessToken(true);
            } catch (ShowdocException e) {
                exception = e;
            }
        }

        if (exception == null) {
            result.setCode(Code.OK);
            result.setCookies(this.cookies);
        } else {
            result.setMessage(exception.getMessage());
            if (exception.isNeedAuth() || exception.isAccountPasswordError()) {
                result.setCode(Code.AUTH_ERROR);
            } else if (exception.isCaptchaError()) {
                result.setCode(Code.AUTH_CAPTCHA_ERROR);
            } else {
                result.setCode(Code.NETWORK_ERROR);
            }
        }
        return result;
    }

    private URI uri(String path) {
        String theUrl = this.url + "/server/index.php?s=" + path;
        boolean sassConcat = path.equals(ShowdocConstants.UpdatePageOpenApi)
                && (this.url.contains("showdoc.cc") || this.url.contains("showdoc.com.cn"));
        if (sassConcat) {
            theUrl = this.url + "/server" + path;
        }
        return URI.create(theUrl);
    }

    /**
     * 获取验证码
     */
    public CaptchaResponse getCaptcha() {
        try (feign.Response response = showdocApi.getCaptcha(uri(ShowdocConstants.GetCaptcha));) {
            byte[] bytes = IOUtils.toByteArray(response.body().asInputStream());
            this.captchaCookies = InternalUtils.parseCookie(response.headers().get("set-cookie"));
            CaptchaResponse captchaResponse = new CaptchaResponse();
            captchaResponse.setBytes(bytes);
            captchaResponse.setSession(this.captchaCookies);
            return captchaResponse;
        } catch (IOException e) {
            throw new ShowdocException(ShowdocConstants.GetCaptcha, e.getMessage(), e);
        }
    }

    /**
     * 获取项目授权token
     */
    public ShowdocProjectToken getProjectToken(String projectId) {
        Response<ShowdocProjectToken> response = showdocApi.getProjectToken(uri(ShowdocConstants.GetItemKey), projectId);
        return response.getData();
    }

    /**
     * 添加或保存文档，通过OpenApi提供接口
     */
    public ShowdocUpdateResponse updatePageByOpenApi(ShowdocUpdateRequest request) {
        Map<String, String> params = InternalUtils.beanToMap(request);
        Response<ShowdocUpdateResponse> response = showdocApi.savePage(uri(ShowdocConstants.UpdatePageOpenApi), params);
        return response.getData();
    }

    /**
     * 计算接口网页地址
     */
    public String calculateWebUrl(String projectId, String pageId) {
        if (StringUtils.isEmpty(pageId)) {
            return this.url + "/" + projectId;
        }
        return this.url + "/" + projectId + "/" + pageId;
    }


    private ShowdocApi createApiClient(String url) {
        return ShowdocApi.feignBuilder()
                .requestInterceptor(template -> {
                    // 请求设置鉴权信息
                    boolean needCookie = !ShowdocConstants.isLoginPath(template.url()) && !ShowdocConstants.isCaptchaPath(template.url());
                    if (needCookie) {
                        template.header("cookie", getOrRefreshAccessToken(false));
                    }
                })
                .responseInterceptor(ctx -> {
                    String requestUrl = ctx.response().request().url();
                    String path = InternalUtils.getUrlPath(requestUrl);
                    // 响应异常转换
                    Object value = ctx.proceed();
                    if (value instanceof Response) {
                        Response<?> responseValue = (Response<?>) value;
                        if (!responseValue.isSuccess()) {
                            throw new ShowdocException(path, responseValue);
                        }
                    }
                    // 登录存储cookie
                    if (ShowdocConstants.isLoginPath(requestUrl)) {
                        Collection<String> setCookies = ctx.response().headers().get("set-cookie");
                        this.cookies = InternalUtils.parseCookie(setCookies);
                    }
                    return value;
                })
                .errorDecoder((methodKey, response) -> {
                    String path = InternalUtils.getUrlPath(response.request().url());
                    return new ShowdocException(path, response.status() + response.reason());
                })
                .target(ShowdocApi.class, url);
    }

    private String getOrRefreshAccessToken(boolean force) {
        if (!force && this.cookies != null && !this.cookies.isEmpty()) {
            return this.cookies;
        }
        doLogin();
        return this.cookies;
    }

    private void doLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername(this.account);
        request.setPassword(this.password);
        request.setV_code(this.captcha);

        Map<String, String> params = InternalUtils.beanToMap(request);
        showdocApi.login(uri(ShowdocConstants.LoginPath), params, this.captchaCookies);
    }
}
