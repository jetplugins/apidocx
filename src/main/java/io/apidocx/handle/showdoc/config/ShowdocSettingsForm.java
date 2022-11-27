package io.apidocx.handle.showdoc.config;

import io.apidocx.base.sdk.showdoc.model.CaptchaResponse;
import javax.swing.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Showdoc配置菜单界面
 */
@Getter
public class ShowdocSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JPanel captchaPanel;
    private JLabel captchaImageLabel;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private String captchaSession;

    public JPanel getPanel() {
        return panel;
    }

    public void set(ShowdocSettings data) {
        if (StringUtils.isNotEmpty(data.getUrl())) {
            urlField.setText(data.getUrl());
        }
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
        captchaPanel.setVisible(false);
        captchaLabel.setVisible(false);
    }

    public ShowdocSettings get() {
        ShowdocSettings data = new ShowdocSettings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        return data;
    }

    public void setCaptchaIcon(CaptchaResponse captcha) {
        captchaPanel.setVisible(true);
        captchaLabel.setVisible(true);
        this.captchaSession = captcha.getSession();
        this.captchaImageLabel.setIcon(new ImageIcon(captcha.getBytes()));
    }

}
