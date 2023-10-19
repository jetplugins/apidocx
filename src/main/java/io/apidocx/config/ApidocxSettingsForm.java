package io.apidocx.config;

import io.apidocx.action.ActionType;
import javax.swing.*;

/**
 * Yapix配置菜单界面
 */
public class ApidocxSettingsForm {

    private JPanel panel;

    private JFormattedTextField curlHostField;
    private JComboBox<String> defaultActionComboBox;

    public JPanel getPanel() {
        return panel;
    }

    public void set(ApidocxSettings data) {
        ActionType defaultAction = data.getDefaultAction();
        defaultActionComboBox.setSelectedItem(defaultAction.getName());
        curlHostField.setText(data.getCurlHost());
    }

    public ApidocxSettings get() {
        ActionType defaultAction = ActionType.YApi;
        if (defaultActionComboBox.getSelectedItem() != null) {
            defaultAction = ActionType.valueOf(defaultActionComboBox.getSelectedItem().toString());
        }
        ApidocxSettings data = new ApidocxSettings();
        data.setDefaultAction(defaultAction);
        data.setCurlHost(curlHostField.getText());
        return data;
    }
}
