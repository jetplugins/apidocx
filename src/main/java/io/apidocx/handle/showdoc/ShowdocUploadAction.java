package io.apidocx.handle.showdoc;

import static io.apidocx.base.util.NotificationUtils.notifyError;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.sdk.showdoc.ShowdocClient;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateResponse;
import io.apidocx.base.sdk.showdoc.model.TestResult.Code;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.handle.showdoc.config.ShowdocSettings;
import io.apidocx.handle.showdoc.config.ShowdocSettingsDialog;
import io.apidocx.handle.showdoc.process.ShowdocUploader;
import io.apidocx.model.Api;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Showdoc上传入口动作.
 */
public class ShowdocUploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To ShowDoc";

    @Override
    public boolean before(AnActionEvent event, ApidocxConfig config) {
        String projectId = config.getShowdocProjectId();
        if (StringUtils.isEmpty(projectId)) {
            notifyError("Config file error", "showdocProjectId must not be empty.");
            return false;
        }

        Project project = event.getData(CommonDataKeys.PROJECT);
        ShowdocSettings settings = ShowdocSettings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings(null, null).getCode()) {
            ShowdocSettingsDialog dialog = ShowdocSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        String projectId = config.getShowdocProjectId();
        Project project = event.getData(CommonDataKeys.PROJECT);

        ShowdocSettings settings = ShowdocSettings.getInstance();
        ShowdocClient client = new ShowdocClient(settings.getUrl(), settings.getAccount(), settings.getPassword(), settings.getCookies());
        ShowdocUploader uploader = new ShowdocUploader(client);

        super.handleUploadAsync(project, apis,
                api -> {
                    ShowdocUpdateResponse sapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setApiUrl(client.calculateWebUrl(sapi.getItemId(), sapi.getPageId()));
                    result.setCategoryUrl(client.calculateWebUrl(sapi.getItemId(), null));
                    return result;
                }, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

}
