package io.yapix.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import io.yapix.base.DefaultConstants;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * 应用级别配置界面.
 */
public class YapixSettingsConfiguration implements Configurable {


    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DefaultConstants.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
