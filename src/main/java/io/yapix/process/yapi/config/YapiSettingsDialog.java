package io.yapix.process.yapi.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.sdk.yapi.response.YapiTestResult;
import io.yapix.base.sdk.yapi.response.YapiTestResult.Code;
import javax.swing.JComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * 配置对话框.
 */
public class YapiSettingsDialog extends DialogWrapper {

    private YApiSettingsForm form;
    private boolean canceled;

    public YapiSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static YapiSettingsDialog show(Project project, String title) {
        YapiSettingsDialog dialog = new YapiSettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        YapiSettings setting = YapiSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new YApiSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        this.canceled = false;
        YapiSettings originSettings = YapiSettings.getInstance();
        YapiSettings settings = form.get();
        settings.setCookies(originSettings.getCookies());
        settings.setCookiesTtl(originSettings.getCookiesTtl());

        // 测试账户
        YapiTestResult testResult = settings.testSettings();
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setCookies(testResult.getAuthCookies().getCookies());
            settings.setCookiesTtl(testResult.getAuthCookies().getTtl());
            // 存储配置
            YapiSettings.getInstance().loadState(settings);
            super.doOKAction();
        }
        if (code == Code.NETWORK_ERROR) {
            setErrorText("Network error: " + testResult.getMessage(), form.getUrlField());
        }
        if (code == Code.AUTH_ERROR) {
            setErrorText("Auth failed: " + testResult.getMessage(), form.getPasswordField());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        YapiSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getAccount())) {
            return new ValidationInfo("", form.getAccountField());
        }
        if (StringUtils.isEmpty(data.getPassword())) {
            return new ValidationInfo("", form.getPasswordField());
        }
        return null;
    }

    @Override
    public void doCancelAction() {
        this.canceled = true;
        super.doCancelAction();
    }

    public boolean isCanceled() {
        return canceled;
    }

    public YApiSettingsForm getForm() {
        return form;
    }

    public void setErrorText(@Nls @Nullable String text, @Nullable JComponent component) {
        super.setErrorText(text, component);
    }
}
