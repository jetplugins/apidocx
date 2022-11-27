package io.apidocx.handle.eolink.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.apidocx.base.sdk.eolink.request.TestResult;
import javax.swing.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class EolinkSettingsDialog extends DialogWrapper {

    private EolinkSettingsForm form;

    EolinkSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static EolinkSettingsDialog show(Project project, String title) {
        EolinkSettingsDialog dialog = new EolinkSettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        EolinkSettings setting = EolinkSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new EolinkSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        EolinkSettings originSettings = EolinkSettings.getInstance();
        EolinkSettings settings = form.get();
        settings.setAccessToken(originSettings.getAccessToken());

        // 测试账户
        TestResult testResult = settings.testSettings();
        TestResult.Code code = testResult.getCode();
        if (code == TestResult.Code.OK) {
            settings.setAccessToken(testResult.getCookies());

            // 存储配置
            EolinkSettings.storeInstance(settings);
            super.doOKAction();
        }
        if (code == TestResult.Code.NETWORK_ERROR) {
            setErrorText("Network error: " + testResult.getMessage(), form.getUrlField());
        }
        if (code == TestResult.Code.AUTH_ERROR) {
            setErrorText("Auth failed: " + testResult.getMessage(), form.getPasswordField());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        EolinkSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("url must not be empty", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getWebUrl())) {
            return new ValidationInfo("webUrl must not be empty", form.getWebUrlField());
        }
        if (StringUtils.isEmpty(data.getLoginUrl())) {
            return new ValidationInfo("loginUrl must not be empty", form.getLoginUrlField());
        }
        if (StringUtils.isEmpty(data.getAccount())) {
            return new ValidationInfo("account must not be empty", form.getAccountField());
        }
        if (StringUtils.isEmpty(data.getPassword())) {
            return new ValidationInfo("password must not be empty", form.getPasswordField());
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
}
