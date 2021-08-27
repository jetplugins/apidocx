package io.yapix.eolinker.config;

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

    private static final String EOLINKER_URL = "https://riag.w.eolinker.com";
    private static final String EOLINKER_LOGIN_URL = "https://www.eolinker.com";

    public JPanel getPanel() {
        return panel;
    }

    public void set(EolinkerSettings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : EOLINKER_URL;
        String loginUrl = StringUtils.isNotEmpty(data.getLoginUrl()) ? data.getLoginUrl() : EOLINKER_LOGIN_URL;
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
