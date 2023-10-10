package io.apidocx.handle.eolink.config;

import javax.swing.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Eolinker配置菜单界面
 */
public class EolinkSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JTextField webUrlField;

    private static final String EOLINKER_URL = "https://apis.eolink.com";
    private static final String EOLINKER_WEB_URL = "https://riag.w.eolink.com";

    public JPanel getPanel() {
        return panel;
    }

    public void set(EolinkSettings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : EOLINKER_URL;
        String webUrl = StringUtils.isNotEmpty(data.getWebUrl()) ? data.getWebUrl() : EOLINKER_WEB_URL;
        urlField.setText(url);
        webUrlField.setText(webUrl);
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public EolinkSettings get() {
        EolinkSettings data = new EolinkSettings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        data.setWebUrl(webUrlField.getText().trim());
        return data;
    }

    //------------------generated-------------------//

    public JTextField getUrlField() {
        return urlField;
    }

    public JTextField getWebUrlField() {
        return webUrlField;
    }

    public JFormattedTextField getAccountField() {
        return accountField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

}
