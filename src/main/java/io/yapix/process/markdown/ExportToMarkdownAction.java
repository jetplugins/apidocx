package io.yapix.process.markdown;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.FileUtilsExt;
import io.yapix.base.util.NotificationUtils;
import io.yapix.base.util.PsiModuleUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
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
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
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
