package io.apidocx.handle.yapi.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.apidocx.base.sdk.yapi.model.TestResult;
import io.apidocx.base.sdk.yapi.model.TestResult.Code;
import javax.swing.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * 配置对话框.
 */
public class YapiSettingsDialog extends DialogWrapper {

    private YApiSettingsForm form;

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
        YapiSettings originSettings = YapiSettings.getInstance();
        YapiSettings settings = form.get();
        settings.setCookies(originSettings.getCookies());

        // 测试账户
        TestResult testResult = settings.testSettings();
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setCookies(testResult.getCookies());
            // 存储配置
            YapiSettings.storeInstance(settings);
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
        super.doCancelAction();
    }

    public boolean isCanceled() {
        return this.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE;
    }

    public YApiSettingsForm getForm() {
        return form;
    }

    public void setErrorText(@Nls @Nullable String text, @Nullable JComponent component) {
        super.setErrorText(text, component);
    }
}
