package io.yapix.process.markdown;

import com.intellij.openapi.actionSystem.AnActionEvent;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.ClipboardUtils;
import io.yapix.base.util.NotificationUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Markdown字符串处理器
 */
public class CopyAsMarkdownAction extends AbstractAction {

    public static final String ACTION_TEXT = "Copy as Markdown";

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        String markdown = new MarkdownGenerator().generate(apis);
        ClipboardUtils.setClipboard(markdown);
        NotificationUtils.notifyInfo("Copy as Markdown", "copied to clipboard");
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
