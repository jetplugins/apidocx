package io.apidocx.base.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.experimental.UtilityClass;

/**
 * PsiModule模块工具类
 */
@UtilityClass
public class PsiModuleUtils {

    /**
     * 获取模块
     */
    public static Module findModuleByEvent(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }
        VirtualFile psiFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (psiFile == null) {
            return null;
        }
        return ModuleUtil.findModuleForFile(psiFile, project);
    }

    /**
     * 获取模块路径
     */
    public static String getModulePath(Module module) {
        return ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
    }
}
