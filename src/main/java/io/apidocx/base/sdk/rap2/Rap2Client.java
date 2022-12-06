package io.apidocx.base.sdk.rap2;

import static com.google.common.base.Preconditions.checkArgument;

import io.apidocx.base.sdk.rap2.dto.CaptchaResponse;
import io.apidocx.base.sdk.rap2.dto.InterfaceCreateResponse;
import io.apidocx.base.sdk.rap2.dto.InterfacePropertiesUpdateRequest;
import io.apidocx.base.sdk.rap2.dto.InterfaceUpdateRequest;
import io.apidocx.base.sdk.rap2.dto.LoginRequest;
import io.apidocx.base.sdk.rap2.dto.LoginResponse;
import io.apidocx.base.sdk.rap2.dto.ModuleCreateRequest;
import io.apidocx.base.sdk.rap2.dto.TestResult;
import io.apidocx.base.sdk.rap2.dto.TestResult.Code;
import io.apidocx.base.sdk.rap2.model.Rap2Interface;
import io.apidocx.base.sdk.rap2.model.Rap2InterfaceBase;
import io.apidocx.base.sdk.rap2.model.Rap2Module;
import io.apidocx.base.sdk.rap2.model.Rap2Repository;
import io.apidocx.base.sdk.rap2.model.Rap2User;
import io.apidocx.base.sdk.rap2.util.SvgUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Rap2客户端
 */
public class Rap2Client {

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
     * 当前用户信息
     */
    private volatile Rap2User currentUser;

    /**
     * 验证码
     */
    private String captcha;

    @Getter
    private String cookies;

    /**
     * 验证码会话
     */
    private String captchaCookies;

    private final Rap2Api rap2Api;


    public Rap2Client(String url, String account, String password) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.rap2Api = createApiClient(url);
    }

    public Rap2Client(String url, String account, String password, String cookies, Long userId) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.cookies = cookies;
        if (userId != null) {
            this.currentUser = new Rap2User(userId);
        }
        this.rap2Api = createApiClient(url);
    }

    /**
     * 测试登录信息
     */
    public TestResult test(String captcha, String captchaCookies) {
        this.captcha = captcha;
        this.captchaCookies = captchaCookies;

        TestResult result = new TestResult();
        Rap2Exception exception = null;
        try {
            rap2Api.getAccountInfo();
        } catch (Rap2Exception e) {
            exception = e;
        }

        // 强制刷新，重试一次
        if (exception != null && exception.isNeedAuth()) {
            exception = null;
            try {
                getOrRefreshAccessToken(true);
            } catch (Rap2Exception e) {
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

    /**
     * 获取验证码
     */
    public CaptchaResponse getCaptcha() {
        try (feign.Response response = rap2Api.getCaptcha();) {
            byte[] bytes = IOUtils.readFully(response.body().asInputStream(), response.body().length());
            bytes = SvgUtils.convertToJpegBytes(bytes);
            CaptchaResponse captchaResponse = new CaptchaResponse();
            captchaResponse.setBytes(bytes);
            String cookies = InternalUtils.parseCookie(response.headers().get("set-cookie"));
            captchaResponse.setSession(cookies);
            return captchaResponse;
        } catch (IOException e) {
            throw new Rap2Exception(Rap2Constants.GetCaptcha, e.getMessage(), e);
        }
    }

    /**
     * 获取仓库信息，包括模块信息
     */
    public Rap2Repository getRepository(long id) {
        return rap2Api.getRepository(id).getData();
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
    public Rap2Module createModule(ModuleCreateRequest request) {
        checkArgument(request.getRepositoryId() != null, "repositoryId can't be null");
        checkArgument(StringUtils.isNotEmpty(request.getName()), "name must not be empty");

        Rap2Module module = new Rap2Module();
        module.setRepositoryId(request.getRepositoryId());
        module.setName(request.getName());
        module.setDescription(request.getDescription() != null ? request.getDescription() : "");
        module.setCreatorId(this.currentUser.getId());
        module.setId(0L);
        module.setPriority(0L);
        return rap2Api.createModule(module).getData();
    }

    /**
     * 获取接口信息
     */
    public Rap2Interface getInterface(Long id) {
        return rap2Api.getInterface(id).getData();
    }

    /**
     * 创建接口
     */
    public Rap2InterfaceBase createInterface(Rap2InterfaceBase request) {
        checkArgument(request.getRepositoryId() != null, "repositoryId can't be null");
        checkArgument(request.getModuleId() != null, "moduleId can't be null");
        InterfaceCreateResponse response = rap2Api.createInterface(request).getData();
        return response.getItf();
    }

    /**
     * 更新接口
     */
    public Rap2InterfaceBase updateInterface(InterfaceUpdateRequest request) {
        checkArgument(request.getId() != null, "id can't be null");
        return rap2Api.updateInterface(request).getData();
    }

    /**
     * 更新接口属性项
     */
    public Rap2InterfaceBase updateInterfaceProperties(InterfacePropertiesUpdateRequest request) {
        checkArgument(request.getInterfaceId() != null, "interfaceId can't be null");
        return rap2Api.updateInterfaceProperties(request.getInterfaceId(), request).getData();
    }

    /**
     * 获取当前登录用户
     */
    public Rap2User getCurrentUser() {
        return currentUser;
    }

    private Rap2Api createApiClient(String url) {
        return Rap2Api.feignBuilder()
                .requestInterceptor(template -> {
                    // 请求设置鉴权信息
                    boolean needCookie = !Rap2Constants.isLoginPath(template.path()) && !Rap2Constants.isCaptchaPath(template.path());
                    if (needCookie) {
                        template.header("cookie", getOrRefreshAccessToken(false));
                    }
                })
                .responseInterceptor(ctx -> {
                    String path = InternalUtils.getUrlPath(ctx.response().request().url());
                    // 响应异常转换
                    Object value = ctx.proceed();
                    if (value instanceof Response) {
                        Response<?> responseValue = (Response<?>) value;
                        if (!responseValue.isSuccess()) {
                            String errMsg = responseValue.getErrMsg() != null ? responseValue.getErrMsg() : "未获取到数据";
                            throw new Rap2Exception(path, errMsg);
                        }
                    }

                    // 登录存储cookie
                    if (Rap2Constants.isLoginPath(path)) {
                        Response<LoginResponse> response = (Response<LoginResponse>) value;
                        LoginResponse loginResult = response.getData();
                        if (!loginResult.isSuccess()) {
                            throw new Rap2Exception(path, loginResult.getErrMsg());
                        }
                        Collection<String> setCookies = ctx.response().headers().get("set-cookie");
                        this.cookies = InternalUtils.parseCookie(setCookies);
                    }
                    return value;
                })
                .errorDecoder((methodKey, response) -> {
                    String path = InternalUtils.getUrlPath(response.request().url());
                    return new Rap2Exception(path, response.status() + response.reason());
                })
                .target(Rap2Api.class, url);
    }

    private String getOrRefreshAccessToken(boolean force) {
        if (!force && this.cookies != null && !this.cookies.isEmpty()) {
            return this.cookies;
        }
        this.currentUser = doLogin();
        return this.cookies;
    }

    private Rap2User doLogin() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(this.account)
                .password(this.password)
                .captcha(this.captcha)
                .build();
        Response<LoginResponse> response = rap2Api.login(loginRequest, this.captchaCookies);
        LoginResponse loginResponse = response.getData();

        Rap2User user = new Rap2User();
        user.setId(loginResponse.getId());
        user.setEmail(loginResponse.getEmail());
        user.setFullname(loginResponse.getFullname());
        return user;
    }

}
