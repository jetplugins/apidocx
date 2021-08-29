package io.yapix.process.curl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.ClipboardUtils;
import io.yapix.base.util.NotificationUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import java.util.List;

/**
 * 复制成Curl字符串处理器
 */
public class CopyAsCurlAction extends AbstractAction {

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        if (apis.size() != 1) {
            return;
        }
        String curl = new CurlGenerator().generate(apis.get(0));
        ClipboardUtils.setClipboard(curl);
        NotificationUtils.notifyInfo("Copy as cURL", "Copied");
    }

}
