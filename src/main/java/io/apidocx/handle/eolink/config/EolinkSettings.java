package io.apidocx.handle.eolink.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.apidocx.base.sdk.eolink.EolinkClient;
import io.apidocx.base.sdk.eolink.request.TestResult;
import io.apidocx.base.sdk.eolink.request.TestResult.Code;
import io.apidocx.base.util.PasswordSafeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Eolinker应用程序级别配置.
 */
@Getter
@Setter
@State(name = "YapixEolinkerSettings", storages = @Storage("YapixEolinkerSettings.xml"))
public class EolinkSettings implements PersistentStateComponent<EolinkSettings> {

    private static final String PASSWORD_KEY = "eolinker";

    /**
     * 登录地址
     */
    private String loginUrl;

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

    /**
     * 登录后的cookies
     */
    private String accessToken;


    public static EolinkSettings getInstance() {
        EolinkSettings settings = ServiceManager.getService(EolinkSettings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    public static void storeInstance(@NotNull EolinkSettings state) {
        getInstance().loadState(state);
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
    }


    @Nullable
    @Override
    public EolinkSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EolinkSettings state) {
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url)
                && StringUtils.isNotEmpty(webUrl)
                && StringUtils.isNotEmpty(loginUrl)
                && StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password);
    }

    public TestResult testSettings() {
        EolinkSettings settings = this;
        // 测试账户
        EolinkClient client = new EolinkClient(settings.getUrl(), settings.loginUrl, settings.getAccount(),
                settings.getPassword(), settings.getAccessToken());
        TestResult testResult = client.test();
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setAccessToken(client.getAccessToken());
        }
        return testResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EolinkSettings)) {
            return false;
        }

        EolinkSettings that = (EolinkSettings) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (webUrl != null ? !webUrl.equals(that.webUrl) : that.webUrl != null) {
            return false;
        }
        if (loginUrl != null ? !loginUrl.equals(that.loginUrl) : that.loginUrl != null) {
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
        result = 31 * result + (loginUrl != null ? loginUrl.hashCode() : 0);
        result = 31 * result + (webUrl != null ? webUrl.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
