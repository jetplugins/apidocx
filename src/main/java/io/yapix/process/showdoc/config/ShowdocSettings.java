package io.yapix.process.showdoc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.yapix.base.sdk.showdoc.AbstractClient.HttpSession;
import io.yapix.base.sdk.showdoc.ShowdocClient;
import io.yapix.base.sdk.showdoc.model.AuthCookies;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Showdoc应用程序级别配置.
 */
@State(name = "YapixShowdocSettings", storages = @Storage("YapixShowdocSettings.xml"))
public class ShowdocSettings implements PersistentStateComponent<ShowdocSettings> {

    /**
     * 服务地址
     */
    private String url;

    /**
     * 用户名
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录后的cookies
     */
    private String cookies;

    /**
     * 授权cookies的有效期.
     */
    private long cookiesTtl;

    public static ShowdocSettings getInstance() {
        return ServiceManager.getService(ShowdocSettings.class);
    }

    @Nullable
    @Override
    public ShowdocSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowdocSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(account)
                && StringUtils.isNotEmpty(password);
    }

    public ShowdocTestResult testSettings(String captcha, HttpSession captchaSession) {
        // 测试账户
        try (ShowdocClient client = new ShowdocClient(this.getUrl(), this.getAccount(), this.getPassword(),
                this.getCookies(), this.getCookiesTtl())) {
            ShowdocTestResult testResult = client.test(captcha, captchaSession);
            ShowdocTestResult.Code code = testResult.getCode();
            if (code == ShowdocTestResult.Code.OK) {
                HttpSession authSession = client.getAuthSession();
                if (authSession != null) {
                    testResult.setAuthCookies(new AuthCookies(authSession.getCookies(), authSession.getCookiesTtl()));
                }
            }
            return testResult;
        }
    }

    //----------------------generated----------------------//


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public long getCookiesTtl() {
        return cookiesTtl;
    }

    public void setCookiesTtl(long cookiesTtl) {
        this.cookiesTtl = cookiesTtl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowdocSettings)) {
            return false;
        }

        ShowdocSettings that = (ShowdocSettings) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (account != null ? !account.equals(that.account) : that.account != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
