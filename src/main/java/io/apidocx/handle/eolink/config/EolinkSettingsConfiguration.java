package io.apidocx.handle.eolink.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用级别配置界面.
 */
public class EolinkSettingsConfiguration implements Configurable {

    private EolinkSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Eolinker";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new EolinkSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        EolinkSettings settings = EolinkSettings.getInstance();
        EolinkSettings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        EolinkSettings data = form.get();
        EolinkSettings.storeInstance(data);
    }

    @Override
    public void reset() {
        EolinkSettings settings = EolinkSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
