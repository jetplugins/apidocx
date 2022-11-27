package io.apidocx.handle.apifox.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用级别配置界面.
 */
public class ApifoxSettingsConfiguration implements Configurable {

    private ApifoxSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Apifox";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new ApifoxSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        ApifoxSettings settings = ApifoxSettings.getInstance();
        ApifoxSettings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        ApifoxSettings data = form.get();
        ApifoxSettings.storeInstance(data);
    }

    @Override
    public void reset() {
        ApifoxSettings settings = ApifoxSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
