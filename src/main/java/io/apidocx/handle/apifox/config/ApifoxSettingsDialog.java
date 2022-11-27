package io.apidocx.handle.apifox.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.apidocx.base.sdk.apifox.model.TestResult;
import io.apidocx.base.sdk.apifox.model.TestResult.Code;
import javax.swing.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Apifox配置对话框.
 */
public class ApifoxSettingsDialog extends DialogWrapper {

    private ApifoxSettingsForm form;

    ApifoxSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static ApifoxSettingsDialog show(Project project, String title) {
        ApifoxSettingsDialog dialog = new ApifoxSettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ApifoxSettings setting = ApifoxSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new ApifoxSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        ApifoxSettings settings = form.get();
        if (!settings.isValidate()) {
            return;
        }

        // 登录校验
        TestResult testResult = settings.testSettings();
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setAccessToken(testResult.getAccessToken());
            ApifoxSettings.storeInstance(settings);
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
        ApifoxSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("ApiURL不能为空", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getAccount())) {
            return new ValidationInfo("账号不能为空", form.getAccountField());
        }
        if (StringUtils.isEmpty(data.getPassword())) {
            return new ValidationInfo("密码不能为空", form.getPasswordField());
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
