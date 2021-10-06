package io.yapix.process.showdoc.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.sdk.showdoc.ShowdocClient;
import io.yapix.base.sdk.showdoc.model.CaptchaResponse;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult.Code;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Showdoc配置对话框.
 */
public class ShowdocSettingsDialog extends DialogWrapper {

    private ShowdocSettingsForm form;

    ShowdocSettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static ShowdocSettingsDialog show(Project project, String title) {
        ShowdocSettingsDialog dialog = new ShowdocSettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        ShowdocSettings setting = ShowdocSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new ShowdocSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        ShowdocSettings settings = form.get();
        if (!settings.isValidate()) {
            return;
        }

        // 登录校验
        JTextField captchaField = form.getCaptchaField();
        ShowdocTestResult testResult = settings.testSettings(captchaField.getText().trim(), form.getCaptchaSession());
        ShowdocTestResult.Code code = testResult.getCode();
        if (code == ShowdocTestResult.Code.OK) {
            settings.setCookies(testResult.getAuthCookies().getCookies());
            settings.setCookiesTtl(testResult.getAuthCookies().getTtl());
            // 存储配置
            ShowdocSettings.getInstance().loadState(settings);
            super.doOKAction();
        }
        if (code == Code.NETWORK_ERROR) {
            setErrorText("Network error: " + testResult.getMessage(), form.getUrlField());
        }
        if (code == Code.AUTH_ERROR) {
            setErrorText("Auth failed: " + testResult.getMessage(), form.getPasswordField());
        }
        if (code == Code.AUTH_CAPTCHA_ERROR) {
            try (ShowdocClient client = new ShowdocClient(settings.getUrl(), settings.getAccount(),
                    settings.getPassword(),
                    settings.getCookies(), settings.getCookiesTtl())) {
                CaptchaResponse captcha = client.getCaptcha();
                form.setCaptchaIcon(captcha);
            }
            form.getCaptchaField().setText("");
            setErrorText("Captcha incorrect", form.getCaptchaField());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        ShowdocSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("服务地址不能为空", form.getUrlField());
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
