package io.apidocx.handle.apifox.config;

import javax.swing.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Apifox配置菜单界面
 */
@Getter
public class ApifoxSettingsForm {

    private JTextField urlField;
    private JFormattedTextField accountField;
    private JPasswordField passwordField;
    private JPanel panel;
    private JTextField webUrlField;

    public JPanel getPanel() {
        return panel;
    }

    public void set(ApifoxSettings data) {
        if (StringUtils.isNotEmpty(data.getWebUrl())) {
            webUrlField.setText(data.getWebUrl());
        }
        if (StringUtils.isNotEmpty(data.getUrl())) {
            urlField.setText(data.getUrl());
        }
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public ApifoxSettings get() {
        ApifoxSettings data = new ApifoxSettings();
        data.setUrl(urlField.getText().trim());
        data.setWebUrl(webUrlField.getText().trim());
        data.setAccount(accountField.getText().trim());
        data.setPassword(new String(passwordField.getPassword()).trim());
        return data;
    }
}
