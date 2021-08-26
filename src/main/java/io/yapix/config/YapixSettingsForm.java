package io.yapix.config;

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

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapixSettings data) {
        ApiPlatformType defaultAction = data.getDefaultAction();
        switch (defaultAction) {
            case YApi:
                yapiRadioButton.doClick();
                break;
            case Rap2:
                rap2RadioButton.doClick();
                break;
            case Eolinker:
                eolinkerRadioButton.doClick();
                break;
            default:
                yapiRadioButton.doClick();
        }
    }

    public YapixSettings get() {
        ApiPlatformType defaultAction = ApiPlatformType.YApi;
        if (yapiRadioButton.isSelected()) {
            defaultAction = ApiPlatformType.YApi;
        } else if (rap2RadioButton.isSelected()) {
            defaultAction = ApiPlatformType.Rap2;
        } else if (eolinkerRadioButton.isSelected()) {
            defaultAction = ApiPlatformType.Eolinker;
        }

        YapixSettings data = new YapixSettings();
        data.setDefaultAction(defaultAction);
        return data;
    }
}
