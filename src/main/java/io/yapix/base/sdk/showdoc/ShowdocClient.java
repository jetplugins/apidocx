package io.yapix.base.sdk.showdoc;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.yapix.base.sdk.showdoc.model.AuthCookies;
import io.yapix.base.sdk.showdoc.model.CaptchaResponse;
import io.yapix.base.sdk.showdoc.model.LoginRequest;
import io.yapix.base.sdk.showdoc.model.ProjectTokenGetRequest;
import io.yapix.base.sdk.showdoc.model.ShowdocProjectToken;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult.Code;
import io.yapix.base.sdk.showdoc.model.ShowdocUpdateRequest;
import io.yapix.base.sdk.showdoc.model.ShowdocUpdateResponse;
import io.yapix.base.sdk.showdoc.util.InternalUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

/**
 * Showdoc客户端
 */
public class ShowdocClient extends AbstractClient {

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

    /** 验证码 */
    private String captcha;

    /** 验证码会话 */
    private HttpSession captchaSession;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public ShowdocClient(String url, String account, String password) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        checkArgument(StringUtils.isNotEmpty(account), "account can't be null");
        checkArgument(StringUtils.isNotEmpty(password), "password can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
    }

    public ShowdocClient(String url, String account, String password, String cookies, Long cookiesTtl) {
        checkArgument(StringUtils.isNotEmpty(url), "url can't be null");
        this.url = url;
        this.account = account;
        this.password = password;
        this.authSession = new HttpSession(cookies, cookiesTtl);
    }

    /**
     * 测试登录信息
     */
    public ShowdocTestResult test(String captcha, HttpSession captchaSession) {
        this.captcha = captcha;
        this.captchaSession = captchaSession;
        ShowdocTestResult result = new ShowdocTestResult();
        try {
            HttpGet request = new HttpGet(url(ShowdocConstants.AccountInfoPath));
            doRequest(request, false);

            result.setCode(Code.OK);
            AuthCookies auth = new AuthCookies(this.authSession.getCookies(), this.authSession.getCookiesTtl());
            result.setAuthCookies(auth);
        } catch (ShowdocException e) {
            result.setMessage(e.getMessage());
            if (e.isNeedAuth() || e.isAccountPasswordError()) {
                result.setCode(Code.AUTH_ERROR);
            } else if (e.isCaptchaError()) {
                result.setCode(Code.AUTH_CAPTCHA_ERROR);
            } else {
                result.setCode(Code.NETWORK_ERROR);
            }
        }
        return result;
    }

    private String url(String path) {
        boolean sassConcat = path.equals(ShowdocConstants.UpdatePageOpenApi)
                && (this.url.contains("showdoc.cc") || this.url.contains("showdoc.com.cn"));
        if (sassConcat) {
            return this.url + "/server" + path;
        }
        return this.url + "/server/index.php?s=" + path;
    }

    /**
     * 获取验证码
     */
    public CaptchaResponse getCaptcha() {
        HttpGet request = new HttpGet(url(ShowdocConstants.GetCaptcha));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity resEntity = response.getEntity();
            byte[] bytes = IOUtils.toByteArray(resEntity.getContent());
            this.captchaSession = getSession(response);
            CaptchaResponse captchaResponse = new CaptchaResponse();
            captchaResponse.setBytes(bytes);
            captchaResponse.setSession(this.captchaSession);
            return captchaResponse;
        } catch (IOException e) {
            throw new ShowdocException(request.getURI().getPath(), e.getMessage(), e);
        }
    }

    /**
     * 登录
     */
    public void login(String captcha, HttpSession session) {
        this.captcha = captcha;
        this.captchaSession = session;
        freshAuth(true);
    }

    /**
     * 获取项目授权token
     */
    public ShowdocProjectToken getProjectToken(String projectId) {
        ProjectTokenGetRequest request = new ProjectTokenGetRequest();
        request.setItemId(projectId);
        String json = requestPost(ShowdocConstants.GetItemKey, request);
        return gson.fromJson(json, ShowdocProjectToken.class);
    }

    /**
     * 添加或保存文档，通过OpenApi提供接口
     */
    public ShowdocUpdateResponse updatePageByOpenApi(ShowdocUpdateRequest request) {
        String json = requestPost(ShowdocConstants.UpdatePageOpenApi, request);
        try {
            return gson.fromJson(json, ShowdocUpdateResponse.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
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

    /**
     * 执行Post请求
     */
    public String requestPost(String path, Object data) {
        HttpPost request = new HttpPost(url(path));
        if (data != null) {
            request.setEntity(InternalUtils.beanToFormEntity(data));
        }
        return doRequest(request, true);
    }

    @Override
    public void doFreshAuth() {
        LoginRequest data = new LoginRequest();
        data.setUsername(this.account);
        data.setPassword(this.password);
        data.setV_code(this.captcha);

        HttpPost request = new HttpPost(url(ShowdocConstants.LoginPath));
        request.setEntity(InternalUtils.beanToFormEntity(data));
        if (this.captchaSession != null) {
            request.setHeader("Cookie", this.captchaSession.getCookies());
        }
        execute(request, true);
    }

    @Override
    public String doHandleResponse(HttpUriRequest request, HttpResponse response) throws IOException {
        HttpEntity resEntity = response.getEntity();
        String content = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            throw new ShowdocException(request.getURI().getPath(), content);
        }
        ShowdocResponse r = gson.fromJson(content, ShowdocResponse.class);
        if (!r.isOk()) {
            throw new ShowdocException(request.getURI().getPath(), r);
        }
        return gson.toJson(r.getData());
    }
}
