package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import io.yapix.config.YapixSettings;
import org.jetbrains.annotations.NotNull;

/**
 * 处理Yapix上传入口动作.
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        YapixSettings settings = YapixSettings.getInstance();
        ActionType actionType = settings.getDefaultAction();
        if (actionType != null) {
            AnAction action = actionType.getAction();
            action.actionPerformed(e);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 菜单名称
        YapixSettings settings = YapixSettings.getInstance();
        ActionType actionType = settings.getDefaultAction();
        if (actionType != null) {
            e.getPresentation().setText(actionType.getName());
        }

        // 是否可见
        boolean visible = false;
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {
            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                visible = true;
            }
        }
        e.getPresentation().setVisible(visible);
    }
}
