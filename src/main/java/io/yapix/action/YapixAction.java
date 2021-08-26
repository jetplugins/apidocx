package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import io.yapix.config.ApiPlatformType;
import io.yapix.config.YapixSettings;
import io.yapix.eolinker.EolinkerUploadAction;
import io.yapix.rap2.Rap2UploadAction;
import io.yapix.yapi.YapiUploadAction;
import org.jetbrains.annotations.NotNull;

/**
 * 处理Yapix上传入口动作.
 */
public class YapixAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        YapixSettings settings = YapixSettings.getInstance();
        ApiPlatformType defaultAction = settings.getDefaultAction();
        AnAction action = null;
        switch (defaultAction) {
            case YApi:
                action = new YapiUploadAction();
                break;
            case Rap2:
                action = new Rap2UploadAction();
                break;
            case Eolinker:
                action = new EolinkerUploadAction();
                break;
            default:
                return;
        }
        action.actionPerformed(e);
    }

    @Override
    public void applyTextOverride(AnActionEvent e) {
        YapixSettings settings = YapixSettings.getInstance();
        e.getPresentation().setText("Upload To " + settings.getDefaultAction().name());
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
            }
        }
        e.getPresentation().setVisible(visible);
    }
}
