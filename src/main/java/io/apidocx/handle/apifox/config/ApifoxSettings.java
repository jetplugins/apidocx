package io.apidocx.handle.apifox.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.apidocx.base.sdk.apifox.ApifoxClient;
import io.apidocx.base.sdk.apifox.model.TestResult;
import io.apidocx.base.sdk.apifox.model.TestResult.Code;
import io.apidocx.base.util.PasswordSafeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Apifox应用程序级别配置.
 */
@Getter
@Setter
@State(name = "YapixApifoxSettings", storages = @Storage("YapixApifoxSettings.xml"))
public class ApifoxSettings implements PersistentStateComponent<ApifoxSettings> {

    private static final String PASSWORD_KEY = "apifox";

    /**
     * 服务地址
     */
    private String webUrl;

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
    private String accessToken;

    public static ApifoxSettings getInstance() {
        ApifoxSettings settings = ServiceManager.getService(ApifoxSettings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    public static void storeInstance(@NotNull ApifoxSettings state) {
        getInstance().loadState(state);
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
    }

    @Nullable
    @Override
    public ApifoxSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ApifoxSettings state) {
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(webUrl) && StringUtils.isNotEmpty(account)
                && StringUtils.isNotEmpty(password);
    }

    public TestResult testSettings() {
        // 测试账户
        ApifoxClient client = new ApifoxClient(this.getUrl(), this.getAccount(), this.getPassword(), this.getAccessToken(), null);
        TestResult testResult = client.test();
        if (testResult.getCode() == Code.OK) {
            this.accessToken = testResult.getAccessToken();
        }
        return testResult;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApifoxSettings)) {
            return false;
        }

        ApifoxSettings that = (ApifoxSettings) o;
        if (webUrl != null ? !webUrl.equals(that.webUrl) : that.webUrl != null) {
            return false;
        }
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
        result = 31 * result + (webUrl != null ? webUrl.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
