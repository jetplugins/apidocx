package io.yapix.process.curl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.ClipboardUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static io.yapix.base.util.NotificationUtils.*;

/**
 * 复制成Curl字符串处理器
 */
public class CopyAsCurlAction extends AbstractAction {

    public static final String ACTION_TEXT = "Copy as cURL";

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        if (apis.size() != 1) {
            notifyWarning("Copy as cURL", "only support single api, please choose method in editor");
            return;
        }
        String curl = new CurlGenerator().generate(apis.get(0));
        ClipboardUtils.setClipboard(curl);
        notifyInfo("Copy as cURL", "copied to clipboard");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
        e.getPresentation().setEnabledAndVisible(isSelectedMethod(e));
    }

    @Override
    protected boolean requiredConfigFile() {
        return false;
    }
}
