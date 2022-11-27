package io.apidocx.handle.showdoc.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用级别配置界面.
 */
public class ShowdocSettingsConfiguration implements Configurable {

    private ShowdocSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ShowDoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new ShowdocSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        ShowdocSettings settings = ShowdocSettings.getInstance();
        ShowdocSettings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        ShowdocSettings data = form.get();
        ShowdocSettings.storeInstance(data);
    }

    @Override
    public void reset() {
        ShowdocSettings settings = ShowdocSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
