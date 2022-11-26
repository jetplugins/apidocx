package io.apidocx.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.apidocx.action.ActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapix应用程序级别配置.
 */
@State(name = "YapixSettings", storages = @Storage("YapixSettings.xml"))
public class ApidocxSettings implements PersistentStateComponent<ApidocxSettings> {

    private ActionType defaultAction = ActionType.YApi;

    public static ApidocxSettings getInstance() {
        return ServiceManager.getService(ApidocxSettings.class);
    }

    @Nullable
    @Override
    public ApidocxSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ApidocxSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public ActionType getDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(ActionType defaultAction) {
        this.defaultAction = defaultAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApidocxSettings)) {
            return false;
        }

        ApidocxSettings that = (ApidocxSettings) o;

        return defaultAction == that.defaultAction;
    }

}
