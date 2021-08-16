package io.yapix.config.yapi;

import com.intellij.openapi.options.Configurable;
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
        return "Yapi";
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
