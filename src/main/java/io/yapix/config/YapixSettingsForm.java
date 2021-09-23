package io.yapix.config;

import io.yapix.action.ActionType;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Yapix配置菜单界面
 */
public class YapixSettingsForm {

    private JPanel panel;
    private JRadioButton yapiRadioButton;
    private JRadioButton rap2RadioButton;
    private JRadioButton eolinkerRadioButton;
    private JRadioButton showdocRadioButton;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapixSettings data) {
        ActionType defaultAction = data.getDefaultAction();
        switch (defaultAction) {
            case Rap2:
                rap2RadioButton.doClick();
                break;
            case Eolinker:
                eolinkerRadioButton.doClick();
                break;
            case ShowDoc:
                showdocRadioButton.doClick();
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
        } else if (eolinkerRadioButton.isSelected()) {
            defaultAction = ActionType.Eolinker;
        } else if (showdocRadioButton.isSelected()) {
            defaultAction = ActionType.ShowDoc;
        }

        YapixSettings data = new YapixSettings();
        data.setDefaultAction(defaultAction);
        return data;
    }
}
