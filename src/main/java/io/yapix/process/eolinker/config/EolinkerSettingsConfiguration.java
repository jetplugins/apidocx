package io.yapix.process.eolinker.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用级别配置界面.
 */
public class EolinkerSettingsConfiguration implements Configurable {

    private EolinkerSettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Eolinker";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new EolinkerSettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        EolinkerSettings settings = EolinkerSettings.getInstance();
        EolinkerSettings data = form.get();
        return !settings.equals(data);
    }

    @Override
    public void apply() {
        EolinkerSettings data = form.get();
        EolinkerSettings.getInstance().loadState(data);
    }

    @Override
    public void reset() {
        EolinkerSettings settings = EolinkerSettings.getInstance();
        form.set(settings);
    }

    @Override
    public void disposeUIResources() {
        this.form = null;
    }
}
