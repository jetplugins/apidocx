package io.yapix.eolinker.config;

import io.yapix.config.DefaultConstants;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;

/**
 * Eolinker配置菜单界面
 */
public class EolinkerSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JTextField loginUrlField;

    public JPanel getPanel() {
        return panel;
    }

    public void set(EolinkerSettings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : DefaultConstants.EOLINKER_URL;
        String loginUrl =
                StringUtils.isNotEmpty(data.getLoginUrl()) ? data.getLoginUrl() : DefaultConstants.EOLINKER_LOGIN_URL;
        urlField.setText(url);
        loginUrlField.setText(loginUrl);
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public EolinkerSettings get() {
        EolinkerSettings data = new EolinkerSettings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        data.setLoginUrl(loginUrlField.getText().trim());
        return data;
    }

    //------------------generated-------------------//

    public JTextField getUrlField() {
        return urlField;
    }

    public JTextField getLoginUrlField() {
        return loginUrlField;
    }

    public JFormattedTextField getAccountField() {
        return accountField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

}
