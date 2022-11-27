package io.apidocx.handle.rap2.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用级别配置界面.
 */
public class Rap2SettingsConfiguration implements Configurable {

    private Rap2SettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Rap2";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new Rap2SettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        Rap2Settings settings = Rap2Settings.getInstance();
        Rap2Settings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        Rap2Settings data = form.get();
        Rap2Settings.storeInstance(data);
    }

    @Override
    public void reset() {
        Rap2Settings settings = Rap2Settings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
