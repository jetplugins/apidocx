package io.apidocx.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import io.apidocx.config.ApidocxSettings;
import org.jetbrains.annotations.NotNull;

/**
 * 处理上传入口动作.
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ApidocxSettings settings = ApidocxSettings.getInstance();
        ActionType actionType = settings.getDefaultAction();
        if (actionType != null) {
            AnAction action = actionType.getAction();
            action.actionPerformed(event);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // 菜单名称
        ApidocxSettings settings = ApidocxSettings.getInstance();
        ActionType actionType = settings.getDefaultAction();
        if (actionType != null) {
            event.getPresentation().setText(actionType.getName());
        }

        // 是否可见
        boolean visible = false;
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {
            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                visible = true;
            }
        }
        event.getPresentation().setVisible(visible);
    }
}
