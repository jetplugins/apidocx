package io.apidocx.handle.markdown;

import com.intellij.openapi.actionSystem.AnActionEvent;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.util.ClipboardUtils;
import io.apidocx.base.util.NotificationUtils;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Api;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Markdown字符串处理器
 */
public class CopyAsMarkdownAction extends AbstractAction {

    public static final String ACTION_TEXT = "Copy as Markdown";

    public CopyAsMarkdownAction() {
        super(false);
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        String markdown = new MarkdownGenerator().generate(apis);
        ClipboardUtils.setClipboard(markdown);
        NotificationUtils.notifyInfo(ACTION_TEXT, "copied to clipboard");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }
}
