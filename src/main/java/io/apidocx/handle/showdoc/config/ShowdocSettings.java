package io.apidocx.handle.showdoc.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.apidocx.base.sdk.showdoc.ShowdocClient;
import io.apidocx.base.sdk.showdoc.model.TestResult;
import io.apidocx.base.util.PasswordSafeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Showdoc应用程序级别配置.
 */
@Getter
@Setter
@State(name = "YapixShowdocSettings", storages = @Storage("YapixShowdocSettings.xml"))
public class ShowdocSettings implements PersistentStateComponent<ShowdocSettings> {

    private static final String PASSWORD_KEY = "showdoc";

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

    public static ShowdocSettings getInstance() {
        ShowdocSettings settings = ServiceManager.getService(ShowdocSettings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    public static void storeInstance(@NotNull ShowdocSettings state) {
        getInstance().loadState(state);
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
    }

    @Nullable
    @Override
    public ShowdocSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowdocSettings state) {
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(account)
                && StringUtils.isNotEmpty(password);
    }

    public TestResult testSettings(String captcha, String captchaSession) {
        // 测试账户
        ShowdocClient client = new ShowdocClient(this.getUrl(), this.getAccount(), this.getPassword(), this.getCookies());
        TestResult testResult = client.test(captcha, captchaSession);
        TestResult.Code code = testResult.getCode();
        if (code == TestResult.Code.OK) {
            testResult.setCookies(client.getCookies());
        }
        return testResult;
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
