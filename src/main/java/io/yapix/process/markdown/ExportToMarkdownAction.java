package io.yapix.process.markdown;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.NotificationUtils;
import io.yapix.base.util.PsiModuleUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Markdown字符串处理器
 */
public class ExportToMarkdownAction extends AbstractAction {

    public static final String ACTION_TEXT = "Export To Markdown";

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        Module module = PsiModuleUtils.findModuleByEvent(event);
        String modulePath = PsiModuleUtils.getModulePath(module);
        String file = modulePath + File.separator + "api.md";

        String markdown = new MarkdownGenerator().generate(apis);
        try {
            FileUtils.writeStringToFile(new File(file), markdown, StandardCharsets.UTF_8);
            NotificationUtils.notifyInfo("Export to markdown successful.");
        } catch (IOException e) {
            throw new RuntimeException("Write markdown file error: " + file, e);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

    @Override
    protected boolean requiredConfigFile() {
        return false;
    }
}
