package io.yapix.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Yapi X菜单分组
 */
public class YapixActionGroup extends DefaultActionGroup {

    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean visible = false;
        // 编辑器上下文非java文件不展示
        VirtualFile file = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null){
            visible = file.isDirectory() || "java".equals(file.getExtension());
        }
        event.getPresentation().setVisible(visible);
    }
}
