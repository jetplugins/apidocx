package io.yapix.config.rap2;

import io.yapix.base.DefaultConstants;
import io.yapix.base.sdk.rap2.AbstractClient.HttpSession;
import io.yapix.base.sdk.rap2.request.CaptchaResponse;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;

/**
 * Rap2配置菜单界面
 */
public class Rap2ConfigurationForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JPanel captchaPanel;
    private JLabel captchaImageLabel;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private HttpSession captchaSession;

    public JPanel getPanel() {
        return panel;
    }

    public void set(Rap2Settings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : DefaultConstants.RAP2_URL;
        urlField.setText(url);
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
        return data;
    }

    public void setCaptchaIcon(CaptchaResponse captcha) {
        captchaPanel.setVisible(true);
        captchaLabel.setVisible(true);
        this.captchaSession = captcha.getSession();
        this.captchaImageLabel.setIcon(new ImageIcon(captcha.getBytes()));
    }

    //------------------generated-------------------//

    public JTextField getUrlField() {
        return urlField;
    }

    public JFormattedTextField getAccountField() {
        return accountField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getCaptchaField() {
        return captchaField;
    }

    public HttpSession getCaptchaSession() {
        return captchaSession;
    }
}
