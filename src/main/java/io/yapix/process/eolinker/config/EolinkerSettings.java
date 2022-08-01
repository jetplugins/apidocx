package io.yapix.process.eolinker.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;
import io.yapix.base.sdk.eolinker.EolinkerClient;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult.Code;
import io.yapix.base.util.PasswordSafeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eolinker应用程序级别配置.
 */
@State(name = "YapixEolinkerSettings", storages = @Storage("YapixEolinkerSettings.xml"))
public class EolinkerSettings implements PersistentStateComponent<EolinkerSettings> {

    private static final String PASSWORD_KEY = "eolinker";

    /**
     * 服务页面地址
     */
    private String webUrl;

    /**
     * 服务接口地址
     */
    private String url;

    /**
     * 用户名
     */
    private String account;

    /**
     * 密码
     */
    @Transient
    private String password;

    /** 登录后的cookies */
    private String cookies;

    /** 授权cookies的有效期. */
    private Long cookiesTtl;

    public static EolinkerSettings getInstance() {
        EolinkerSettings settings = ServiceManager.getService(EolinkerSettings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    public static void storeInstance(@NotNull EolinkerSettings state) {
        getInstance().loadState(state);
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
    }


    @Nullable
    @Override
    public EolinkerSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EolinkerSettings state) {
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(webUrl)
                && StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password);
    }

    public EolinkerTestResult testSettings() {
        EolinkerSettings settings = this;
        HttpSession session = new HttpSession(settings.getCookies(), settings.getCookiesTtl());
        // 测试账户
        try (EolinkerClient client = new EolinkerClient(settings.getUrl(), settings.getAccount(),
                settings.getPassword(), session)) {
            EolinkerTestResult testResult = client.test();
            Code code = testResult.getCode();
            if (code == Code.OK) {
                settings.setCookies(client.getAuthSession().getCookies());
                settings.setCookiesTtl(client.getAuthSession().getCookiesTtl());
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

    @Transient
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

    public Long getCookiesTtl() {
        return cookiesTtl;
    }

    public void setCookiesTtl(Long cookiesTtl) {
        this.cookiesTtl = cookiesTtl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EolinkerSettings)) {
            return false;
        }

        EolinkerSettings that = (EolinkerSettings) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (webUrl != null ? !webUrl.equals(that.webUrl) : that.webUrl != null) {
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
        result = 31 * result + (webUrl != null ? webUrl.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
