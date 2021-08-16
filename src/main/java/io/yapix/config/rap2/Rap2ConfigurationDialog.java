package io.yapix.config.rap2;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import io.yapix.base.DefaultConstants;
import javax.swing.JComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2配置对话框.
 */
public class Rap2ConfigurationDialog extends DialogWrapper {

    private Rap2ConfigurationForm form;

    /**
     * 显示弹框
     */
    public static void show(Project project) {
        Rap2ConfigurationDialog dialog = new Rap2ConfigurationDialog(project);
        dialog.show();
    }

    Rap2ConfigurationDialog(@Nullable Project project) {
        super(project);
        setTitle(DefaultConstants.NAME);
        init();
    }

    @Override
    protected void init() {
        super.init();
        Rap2Settings setting = Rap2Settings.getInstance();
        form.set(setting);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (form == null) {
            form = new Rap2ConfigurationForm();
        }
        return form.getPanel();
    }

    @Override
    protected void doOKAction() {
        Rap2Settings data = form.get();
        if (data.isValidate()) {
            Rap2Settings.getInstance().loadState(data);
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        Rap2Settings data = form.get();
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
