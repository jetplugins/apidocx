package io.yapix.config.yapi;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.response.YapiTestResult;
import io.yapix.base.sdk.yapi.response.YapiTestResult.Code;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapi应用程序级别配置.
 */
@State(name = "YapixYapiSettings", storages = @Storage("YapixYapiSettings.xml"))
public class YapiSettings implements PersistentStateComponent<YapiSettings> {

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
    private volatile long cookiesTtl;

    public static YapiSettings getInstance() {
        return ServiceManager.getService(YapiSettings.class);
    }

    @Nullable
    @Override
    public YapiSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull YapiSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password);
    }

    public YapiTestResult testSettings() {
        YapiSettings settings = this;
        // 测试账户
        try (YapiClient yapiClient = new YapiClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesTtl())) {
            YapiTestResult testResult = yapiClient.test();
            Code code = testResult.getCode();
            if (code == Code.OK) {
                settings.setCookies(yapiClient.getAuthCookies().getCookies());
                settings.setCookiesTtl(yapiClient.getAuthCookies().getTtl());
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

    public void setCookiesTtl(Long cookiesTtl) {
        this.cookiesTtl = cookiesTtl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof YapiSettings)) {
            return false;
        }

        YapiSettings that = (YapiSettings) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (account != null ? !account.equals(that.account) : that.account != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }
}
