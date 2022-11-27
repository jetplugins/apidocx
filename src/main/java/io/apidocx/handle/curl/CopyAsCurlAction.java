package io.apidocx.handle.curl;

import static io.apidocx.base.util.NotificationUtils.notifyInfo;
import static io.apidocx.base.util.NotificationUtils.notifyWarning;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.util.ClipboardUtils;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Api;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Curl字符串处理器
 */
public class CopyAsCurlAction extends AbstractAction {

    public static final String ACTION_TEXT = "Copy as cURL";

    public CopyAsCurlAction() {
        super(false);
    }

    /**
     * 检查前操作
     */
    public boolean before(AnActionEvent event, ApidocxConfig config) {
        config.setStrict(false);
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        if (apis.size() != 1) {
            notifyWarning("Copy as cURL", "only support single api, please choose method in editor");
            return;
        }
        String curl = new CurlGenerator().generate(apis.get(0));
        ClipboardUtils.setClipboard(curl);
        notifyInfo(ACTION_TEXT, "copied to clipboard");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
        e.getPresentation().setEnabledAndVisible(isSelectedMethod(e));
    }

    /**
     * 是否选中了方法
     */
    private boolean isSelectedMethod(@NotNull AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {

            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                PsiMethod method = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
                return method != null;
            }
        }
        return true;
    }
}
