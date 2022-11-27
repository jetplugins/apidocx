package io.apidocx.handle.yapi.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.apidocx.base.sdk.yapi.YapiClient;
import io.apidocx.base.sdk.yapi.model.LoginWay;
import io.apidocx.base.sdk.yapi.model.TestResult;
import io.apidocx.base.sdk.yapi.model.TestResult.Code;
import io.apidocx.base.util.PasswordSafeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapi应用程序级别配置.
 */
@Getter
@Setter
@State(name = "YapixYapiSettings", storages = @Storage("YapixYapiSettings.xml"))
public class YapiSettings implements PersistentStateComponent<YapiSettings> {

    private static final String PASSWORD_KEY = "yapi";

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
    @Transient
    private String password;

    /**
     * 登录后的cookies
     */
    private String cookies;

    /**
     * 登录方式
     */
    private LoginWay loginWay;

    public static YapiSettings getInstance() {
        YapiSettings settings = ServiceManager.getService(YapiSettings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    public static void storeInstance(@NotNull YapiSettings state) {
        getInstance().loadState(state);
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
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

    public TestResult testSettings() {
        YapiSettings settings = this;
        // 测试账户
        YapiClient yapiClient = new YapiClient(settings.getUrl(), settings.getAccount(), settings.getPassword(), settings.getLoginWay(), settings.getCookies());
        TestResult testResult = yapiClient.test();
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setCookies(yapiClient.getCookies());
        }
        return testResult;
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
        if (password != null ? !password.equals(that.password) : that.password != null) {
            return false;
        }
        return loginWay == that.loginWay;
    }

}
