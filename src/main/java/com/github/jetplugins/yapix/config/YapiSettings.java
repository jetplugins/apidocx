package com.github.jetplugins.yapix.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapi应用程序级别配置.
 */
@State(name = "YapixSettings", storages = @Storage("yapix.xml"))
public class YapiSettings implements PersistentStateComponent<YapiSettings> {

    /**
     * 服务地址
     */
    private String yapiUrl;

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
        return StringUtils.isNotEmpty(yapiUrl) && StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password);
    }

    //----------------------generated----------------------//

    public String getYapiUrl() {
        return yapiUrl;
    }

    public void setYapiUrl(String yapiUrl) {
        this.yapiUrl = yapiUrl;
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
        if (!(o instanceof YapiSettings)) {
            return false;
        }

        YapiSettings that = (YapiSettings) o;

        if (yapiUrl != null ? !yapiUrl.equals(that.yapiUrl) : that.yapiUrl != null) {
            return false;
        }
        if (account != null ? !account.equals(that.account) : that.account != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }
}
