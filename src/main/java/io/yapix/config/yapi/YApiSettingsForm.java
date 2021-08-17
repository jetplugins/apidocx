package io.yapix.config.yapi;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Yapi配置菜单界面
 */
public class YApiSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapiSettings data) {
        urlField.setText(data.getUrl());
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public YapiSettings get() {
        YapiSettings data = new YapiSettings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        return data;
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
}
