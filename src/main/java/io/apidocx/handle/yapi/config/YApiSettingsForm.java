package io.apidocx.handle.yapi.config;

import io.apidocx.base.sdk.yapi.model.LoginWay;
import javax.swing.*;

/**
 * Yapi配置菜单界面
 */
public class YApiSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JRadioButton loginWayDefaultRadioButton;
    private JRadioButton loginWayLdapRadioButton;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapiSettings data) {
        urlField.setText(data.getUrl());
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
        if (data.getLoginWay() == LoginWay.LDAP) {
            loginWayLdapRadioButton.doClick();
        } else {
            loginWayDefaultRadioButton.doClick();
        }
    }

    public YapiSettings get() {
        YapiSettings data = new YapiSettings();
        data.setUrl(urlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        LoginWay way = LoginWay.DEFAULT;
        if (loginWayLdapRadioButton.isSelected()) {
            way = LoginWay.LDAP;
        }
        data.setLoginWay(way);
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
