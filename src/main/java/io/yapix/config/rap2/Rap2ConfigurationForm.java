package io.yapix.config.rap2;

import io.yapix.base.DefaultConstants;
import javax.swing.JFormattedTextField;
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

    public JPanel getPanel() {
        return panel;
    }

    public void set(Rap2Settings data) {
        String url = StringUtils.isNotEmpty(data.getUrl()) ? data.getUrl() : DefaultConstants.RAP2_URL;
        urlField.setText(url);
        accountField.setText(data.getAccount());
        passwordField.setText(data.getPassword());
    }

    public Rap2Settings get() {
        Rap2Settings data = new Rap2Settings();
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
