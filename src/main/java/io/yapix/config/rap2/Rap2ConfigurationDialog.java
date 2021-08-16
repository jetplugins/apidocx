package io.yapix.config.rap2;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.DefaultConstants;
import io.yapix.base.sdk.rap2.AbstractClient.HttpSession;
import io.yapix.base.sdk.rap2.Rap2Client;
import io.yapix.base.sdk.rap2.Rap2Exception;
import io.yapix.base.sdk.rap2.request.CaptchaResponse;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class Rap2ConfigurationDialog extends DialogWrapper {

    private Rap2ConfigurationForm form;

    /**
     * 显示弹框
     */
    public static void show(Project project) {
        Rap2ConfigurationDialog dialog = new Rap2ConfigurationDialog(project);
        dialog.show();
    }

    Rap2ConfigurationDialog(@Nullable Project project) {
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
            form = new Rap2ConfigurationForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        Rap2Settings data = form.get();
        if (!data.isValidate()) {
            return;
        }

        // 登录校验
        JTextField captchaField = form.getCaptchaField();
        Rap2Client rap2Client = new Rap2Client(data.getUrl(), data.getAccount(), data.getPassword());
        try {
            rap2Client.login(captchaField.getText().trim(), form.getCaptchaSession());
        } catch (Rap2Exception e) {
            if (e.isCaptchaError()) {
                CaptchaResponse captcha = rap2Client.getCaptcha();
                form.setCaptchaIcon(captcha);
                setErrorText("验证码错误", form.getCaptchaField());
                return;
            } else if (e.isAccountPasswordError()) {
                setErrorText("账号或密码错误", form.getPasswordField());
                return;
            }
            setErrorText("登录失败," + e.getMsg());
            return;
        } finally {
            rap2Client.close();
        }

        // 存储授权信息
        HttpSession authSession = rap2Client.getAuthSession();
        if (authSession != null) {
            data.setCookies(authSession.getCookies());
            data.setCookiesTtl(authSession.getCookiesTtl());
        }
        Rap2Settings.getInstance().loadState(data);
        super.doOKAction();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        Rap2Settings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("Rap2接口地址不能为空", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getAccount())) {
            return new ValidationInfo("账号不能为空", form.getAccountField());
        }
        if (StringUtils.isEmpty(data.getPassword())) {
            return new ValidationInfo("密码不能为空", form.getPasswordField());
        }
        return null;
    }
}
