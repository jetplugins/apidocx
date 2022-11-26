package io.apidocx.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import io.apidocx.config.ApidocxSettingsConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * 打开设置菜单项
 */
public class OpenSettingsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, ApidocxSettingsConfiguration.class);
    }
}
