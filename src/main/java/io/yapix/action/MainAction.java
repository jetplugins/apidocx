package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
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
        AnAction action = settings.getDefaultAction().getAction();
        action.actionPerformed(e);
    }

    @Override
    public void applyTextOverride(AnActionEvent e) {
        YapixSettings settings = YapixSettings.getInstance();
        e.getPresentation().setText(settings.getDefaultAction().getName());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean visible = false;
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {

            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                visible = true;
                // 必须选中方法的情况下
                YapixSettings settings = YapixSettings.getInstance();
                if (settings.getDefaultAction() == ActionType.Curl) {
                    PsiMethod method = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
                    visible = method != null;
                }
            }
        }
        e.getPresentation().setVisible(visible);
    }
}
