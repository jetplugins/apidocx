package io.yapix.ui;

import io.yapix.config.YapiSettings;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Yapi配置菜单界面
 */
public class YApiConfigurationForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapiSettings data) {
        urlField.setText(data.getYapiUrl());
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public YapiSettings get() {
        YapiSettings data = new YapiSettings();
        data.setYapiUrl(urlField.getText().trim());
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
