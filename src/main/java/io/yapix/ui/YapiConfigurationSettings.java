package io.yapix.ui;

import com.intellij.openapi.options.Configurable;
import io.yapix.base.DefaultConstants;
import io.yapix.config.YapiSettings;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * 应用级别配置界面.
 */
public class YapiConfigurationSettings implements Configurable {

    private YApiConfigurationForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DefaultConstants.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new YApiConfigurationForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        YapiSettings settings = YapiSettings.getInstance();
        YapiSettings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        YapiSettings data = form.get();
        YapiSettings.getInstance().loadState(data);
    }

    @Override
    public void reset() {
        YapiSettings settings = YapiSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
