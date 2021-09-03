package io.yapix.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.yapix.action.ActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapix应用程序级别配置.
 */
@State(name = "YapixSettings", storages = @Storage("YapixSettings.xml"))
public class YapixSettings implements PersistentStateComponent<YapixSettings> {

    private ActionType defaultAction = ActionType.YApi;

    public static YapixSettings getInstance() {
        return ServiceManager.getService(YapixSettings.class);
    }

    @Nullable
    @Override
    public YapixSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull YapixSettings state) {
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
        if (!(o instanceof YapixSettings)) {
            return false;
        }

        YapixSettings that = (YapixSettings) o;

        return defaultAction == that.defaultAction;
    }

}
