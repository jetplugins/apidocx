package io.yapix.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * 应用级别配置界面.
 */
public class YapixSettingsConfiguration implements Configurable {

    private YapixSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DefaultConstants.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new YapixSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        YapixSettings originSettings = YapixSettings.getInstance();
        YapixSettings newSettings = form.get();
        return !originSettings.equals(newSettings);
    }

    @Override
    public void apply() {
        YapixSettings settings = form.get();
        YapixSettings.getInstance().loadState(settings);
    }
}
