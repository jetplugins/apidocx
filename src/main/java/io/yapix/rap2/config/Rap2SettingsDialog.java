package io.yapix.rap2.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.sdk.rap2.Rap2Client;
import io.yapix.base.sdk.rap2.request.CaptchaResponse;
import io.yapix.base.sdk.rap2.request.Rap2TestResult;
import io.yapix.base.sdk.rap2.request.Rap2TestResult.Code;
import io.yapix.config.DefaultConstants;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class Rap2SettingsDialog extends DialogWrapper {

    private Rap2SettingsForm form;
    private boolean canceled;

    /**
     * 显示弹框
     */
    public static Rap2SettingsDialog show(Project project) {
        Rap2SettingsDialog dialog = new Rap2SettingsDialog(project);
        dialog.show();
        return dialog;
    }

    Rap2SettingsDialog(@Nullable Project project) {
        super(project);
        setTitle(DefaultConstants.NAME);
        init();
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
        this.canceled = false;
        Rap2Settings settings = form.get();
        if (!settings.isValidate()) {
            return;
        }

        // 登录校验
        JTextField captchaField = form.getCaptchaField();
        Rap2TestResult testResult = settings.testSettings(captchaField.getText().trim(), form.getCaptchaSession());
        Code code = testResult.getCode();
        if (code == Code.OK) {
            settings.setCookies(testResult.getAuthCookies().getCookies());
            settings.setCookiesTtl(testResult.getAuthCookies().getTtl());
            settings.setCookiesUserId(testResult.getAuthUser().getId());
            // 存储配置
            Rap2Settings.getInstance().loadState(settings);
            super.doOKAction();
        }
        if (code == Code.NETWORK_ERROR) {
            setErrorText("Network error", form.getUrlField());
        }
        if (code == Code.AUTH_ERROR) {
            setErrorText("Password incorrect", form.getPasswordField());
        }
        if (code == Code.AUTH_CAPTCHA_ERROR) {
            try (Rap2Client client = new Rap2Client(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                    settings.getCookies(), settings.getCookiesTtl(), settings.getCookiesUserId())) {
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
        this.canceled = true;
        super.doCancelAction();
    }

    public boolean isCanceled() {
        return canceled;
    }
}
