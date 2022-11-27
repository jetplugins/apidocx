package io.apidocx.handle.rap2.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.apidocx.base.sdk.rap2.Rap2Client;
import io.apidocx.base.sdk.rap2.dto.CaptchaResponse;
import io.apidocx.base.sdk.rap2.dto.TestResult;
import io.apidocx.base.sdk.rap2.dto.TestResult.Code;
import javax.swing.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class Rap2SettingsDialog extends DialogWrapper {

    private Rap2SettingsForm form;

    Rap2SettingsDialog(@Nullable Project project, String title) {
        super(project);
        setTitle(title);
        init();
    }

    /**
     * 显示弹框
     */
    public static Rap2SettingsDialog show(Project project, String title) {
        Rap2SettingsDialog dialog = new Rap2SettingsDialog(project, title);
        dialog.show();
        return dialog;
    }

    @Override
    protected void init() {
        super.init();
        Rap2Settings setting = Rap2Settings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new Rap2SettingsForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        Rap2Settings settings = form.get();
        if (!settings.isValidate()) {
            return;
        }

        // 登录校验
        JTextField captchaField = form.getCaptchaField();
        TestResult testResult = settings.testSettings(captchaField.getText().trim(), form.getCaptchaSession());
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setCookies(testResult.getCookies());
            settings.setCookiesUserId(testResult.getAuthUser().getId());
            // 存储配置
            Rap2Settings.storeInstance(settings);
            super.doOKAction();
        }
        if (code == Code.NETWORK_ERROR) {
            setErrorText("Network error: " + testResult.getMessage(), form.getUrlField());
        }
        if (code == Code.AUTH_ERROR) {
            setErrorText("Auth failed: " + testResult.getMessage(), form.getPasswordField());
        }
        if (code == Code.AUTH_CAPTCHA_ERROR) {
            Rap2Client client = new Rap2Client(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                    settings.getCookies(), settings.getCookiesUserId());
            CaptchaResponse captcha = client.getCaptcha();
            form.setCaptchaIcon(captcha);
            form.getCaptchaField().setText("");
            setErrorText("Captcha incorrect", form.getCaptchaField());
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        Rap2Settings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("Rap2接口地址不能为空", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getWebUrl())) {
            return new ValidationInfo("Rap2网页地址不能为空", form.getWebUrlField());
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
