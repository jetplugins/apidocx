package io.yapix.config.yapi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.DefaultConstants;
import javax.swing.JComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 配置对话框.
 */
public class YapiConfigurationDialog extends DialogWrapper {

    private YApiConfigurationForm form;

    /**
     * 显示弹框
     */
    public static void show(Project project) {
        YapiConfigurationDialog dialog = new YapiConfigurationDialog(project);
        dialog.show();
    }

    YapiConfigurationDialog(@Nullable Project project) {
        super(project);
        setTitle(DefaultConstants.NAME);
        init();
    }

    @Override
    protected void init() {
        super.init();
        YapiSettings setting = YapiSettings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new YApiConfigurationForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        YapiSettings data = form.get();
        if (data.isValidate()) {
            YapiSettings.getInstance().loadState(data);
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        YapiSettings data = form.get();
        if (StringUtils.isEmpty(data.getUrl())) {
            return new ValidationInfo("", form.getUrlField());
        }
        if (StringUtils.isEmpty(data.getAccount())) {
            return new ValidationInfo("", form.getAccountField());
        }
        if (StringUtils.isEmpty(data.getPassword())) {
            return new ValidationInfo("", form.getPasswordField());
        }
        return null;
    }
}
