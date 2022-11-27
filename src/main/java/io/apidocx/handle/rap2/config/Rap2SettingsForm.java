package io.apidocx.handle.rap2.config;

import io.apidocx.base.sdk.rap2.dto.CaptchaResponse;
import javax.swing.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Rap2配置菜单界面
 */
@Getter
public class Rap2SettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JPanel captchaPanel;
    private JLabel captchaImageLabel;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private JTextField webUrlField;
    private String captchaSession;

    private static final String RAP2_URL = "http://rap2api.taobao.org";
    private static final String RAP2_WEB_URL = "http://rap2.taobao.org";

    public JPanel getPanel() {
        return panel;
    }

    public void set(Rap2Settings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : RAP2_URL;
        String webUrl = StringUtils.isNotEmpty(data.getWebUrl()) ? data.getWebUrl() : RAP2_WEB_URL;
        urlField.setText(url);
        webUrlField.setText(webUrl);
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
        captchaPanel.setVisible(false);
        captchaLabel.setVisible(false);
    }

    public Rap2Settings get() {
        Rap2Settings data = new Rap2Settings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        data.setWebUrl(webUrlField.getText().trim());
        return data;
    }

    public void setCaptchaIcon(CaptchaResponse captcha) {
        captchaPanel.setVisible(true);
        captchaLabel.setVisible(true);
        this.captchaSession = captcha.getSession();
        this.captchaImageLabel.setIcon(new ImageIcon(captcha.getBytes()));
    }

}
