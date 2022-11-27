package io.apidocx.handle.markdown;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.util.FileUtilsExt;
import io.apidocx.base.util.NotificationUtils;
import io.apidocx.base.util.PsiModuleUtils;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Api;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Markdown字符串处理器
 */
public class ExportToMarkdownAction extends AbstractAction {

    public static final String ACTION_TEXT = "Export To Markdown";

    public ExportToMarkdownAction() {
        super(false);
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        Module module = PsiModuleUtils.findModuleByEvent(event);
        String modulePath = PsiModuleUtils.getModulePath(module);
        File file = new File(modulePath + File.separator + "api.md");

        String markdown = new MarkdownGenerator().generate(apis);
        try {
            FileUtilsExt.writeText(file, markdown);
            NotificationUtils.notifyInfo("Export to markdown successful.");
        } catch (IOException e) {
            throw new RuntimeException("Write markdown file error: " + file.getAbsolutePath(), e);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }
}
