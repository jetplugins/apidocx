package io.yapix.config;

import io.yapix.action.YapiActions;
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
    private JRadioButton curlRadioButton;

    public JPanel getPanel() {
        return panel;
    }

    public void set(YapixSettings data) {
        YapiActions defaultAction = data.getDefaultAction();
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
            case Curl:
                curlRadioButton.doClick();
            default:
                yapiRadioButton.doClick();
        }
    }

    public YapixSettings get() {
        YapiActions defaultAction = YapiActions.YApi;
        if (yapiRadioButton.isSelected()) {
            defaultAction = YapiActions.YApi;
        } else if (rap2RadioButton.isSelected()) {
            defaultAction = YapiActions.Rap2;
        } else if (eolinkerRadioButton.isSelected()) {
            defaultAction = YapiActions.Eolinker;
        } else if (curlRadioButton.isSelected()) {
            defaultAction = YapiActions.Curl;
        }

        YapixSettings data = new YapixSettings();
        data.setDefaultAction(defaultAction);
        return data;
    }
}
