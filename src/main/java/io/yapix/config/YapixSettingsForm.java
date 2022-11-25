package io.yapix.config;

import io.yapix.action.ActionType;
import javax.swing.*;

/**
 * Yapix配置菜单界面
 */
public class YapixSettingsForm {

    private JPanel panel;
    private JRadioButton yapiRadioButton;
    private JRadioButton rap2RadioButton;
    private JRadioButton eolinkRadioButton;
    private JRadioButton showdocRadioButton;
    private JRadioButton apifoxRadioButton;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapixSettings data) {
        ActionType defaultAction = data.getDefaultAction();
        switch (defaultAction) {
            case Rap2:
                rap2RadioButton.doClick();
                break;
            case Eolink:
                eolinkRadioButton.doClick();
                break;
            case ShowDoc:
                showdocRadioButton.doClick();
                break;
            case Apifox:
                apifoxRadioButton.doClick();
                break;
            default:
                yapiRadioButton.doClick();
        }
    }

    public YapixSettings get() {
        ActionType defaultAction = ActionType.YApi;
        if (yapiRadioButton.isSelected()) {
            defaultAction = ActionType.YApi;
        } else if (rap2RadioButton.isSelected()) {
            defaultAction = ActionType.Rap2;
        } else if (eolinkRadioButton.isSelected()) {
            defaultAction = ActionType.Eolink;
        } else if (showdocRadioButton.isSelected()) {
            defaultAction = ActionType.ShowDoc;
        } else if (apifoxRadioButton.isSelected()) {
            defaultAction = ActionType.Apifox;
        }

        YapixSettings data = new YapixSettings();
        data.setDefaultAction(defaultAction);
        return data;
    }
}
