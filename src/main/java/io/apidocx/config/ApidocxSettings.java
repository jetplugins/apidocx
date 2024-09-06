package io.apidocx.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.apidocx.action.ActionType;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapix应用程序级别配置.
 */
@State(name = "YapixSettings", storages = @Storage("YapixSettings.xml"))
@Getter
@Setter
public class ApidocxSettings implements PersistentStateComponent<ApidocxSettings> {

    private ActionType defaultAction = ActionType.YApi;

    private String curlHost = "http://localhost:8080";

    public static ApidocxSettings getInstance() {
        return ApplicationManager.getApplication().getService(ApidocxSettings.class);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApidocxSettings that = (ApidocxSettings) o;
        return defaultAction == that.defaultAction && Objects.equals(curlHost, that.curlHost);
    }

}
