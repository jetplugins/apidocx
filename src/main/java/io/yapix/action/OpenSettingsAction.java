package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import io.yapix.config.YapixSettingsConfiguration;
import org.jetbrains.annotations.NotNull;

public class OpenSettingsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, YapixSettingsConfiguration.class);
    }
}
