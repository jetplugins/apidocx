package io.apidocx.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * 应用级别配置界面.
 */
public class ApidocxSettingsConfiguration implements Configurable {

    private ApidocxSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DefaultConstants.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new ApidocxSettingsForm();
            form.set(ApidocxSettings.getInstance());
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        ApidocxSettings originSettings = ApidocxSettings.getInstance();
        ApidocxSettings newSettings = form.get();
        return !originSettings.equals(newSettings);
    }

    @Override
    public void apply() {
        ApidocxSettings settings = form.get();
        ApidocxSettings.getInstance().loadState(settings);
    }
}
