package io.yapix.process.eolinker.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import javax.swing.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class EolinkerSettingsDialog extends DialogWrapper {

    private EolinkerSettingsForm form;

    EolinkerSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static EolinkerSettingsDialog show(Project project, String title) {
        EolinkerSettingsDialog dialog = new EolinkerSettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        EolinkerSettings setting = EolinkerSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new EolinkerSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        EolinkerSettings originSettings = EolinkerSettings.getInstance();
        EolinkerSettings settings = form.get();
        settings.setCookies(originSettings.getCookies());
        settings.setCookiesTtl(originSettings.getCookiesTtl());

        // 测试账户
        EolinkerTestResult testResult = settings.testSettings();
        EolinkerTestResult.Code code = testResult.getCode();
        if (code == EolinkerTestResult.Code.OK) {
            settings.setCookies(testResult.getAuthSession().getCookies());
            settings.setCookiesTtl(testResult.getAuthSession().getCookiesTtl());

            // 存储配置
            EolinkerSettings.storeInstance(settings);
            super.doOKAction();
        }
        if (code == EolinkerTestResult.Code.NETWORK_ERROR) {
            setErrorText("Network error: " + testResult.getMessage(), form.getUrlField());
        }
        if (code == EolinkerTestResult.Code.AUTH_ERROR) {
            setErrorText("Auth failed: " + testResult.getMessage(), form.getPasswordField());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        EolinkerSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("url must not be empty", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getWebUrl())) {
            return new ValidationInfo("loginUrl must not be empty", form.getWebUrlField());
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
