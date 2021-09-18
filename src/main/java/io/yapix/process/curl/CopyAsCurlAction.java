package io.yapix.process.curl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.ClipboardUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 复制成Curl字符串处理器
 */
public class CopyAsCurlAction extends AbstractAction {

    public static final String ACTION_TEXT = "Copy as cURL";

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        if (apis.size() != 1) {
            return;
        }
        String curl = new CurlGenerator().generate(apis.get(0));
        ClipboardUtils.setClipboard(curl);
    }

    @Override
    public void applyTextOverride(AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(isSelectedMethod(e));
    }
}
