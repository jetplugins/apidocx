package io.yapix.process.showdoc;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractionUploadAction;
import io.yapix.base.sdk.showdoc.ShowdocClient;
import io.yapix.base.sdk.showdoc.model.ShowdocTestResult.Code;
import io.yapix.base.sdk.showdoc.model.ShowdocUpdateResponse;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.process.showdoc.config.ShowdocSettings;
import io.yapix.process.showdoc.config.ShowdocSettingsDialog;
import io.yapix.process.showdoc.process.ShowdocUploader;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Showdoc上传入口动作.
 */
public class ShowdocUploadAction extends AbstractionUploadAction {

    public static final String ACTION_TEXT = "Upload To ShowDoc";

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        ShowdocSettings settings = ShowdocSettings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings(null, null).getCode()) {
            ShowdocSettingsDialog dialog = ShowdocSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        String projectId = config.getShowdocProjectId();
        Project project = event.getData(CommonDataKeys.PROJECT);

        ShowdocSettings settings = ShowdocSettings.getInstance();
        ShowdocClient client = new ShowdocClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesTtl());
        ShowdocUploader uploader = new ShowdocUploader(client);

        super.handleUploadAsync(project, apis,
                api -> {
                    ShowdocUpdateResponse sapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setApiUrl(client.calculateWebUrl(sapi.getItemId(), sapi.getPageId()));
                    result.setCategoryUrl(client.calculateWebUrl(sapi.getItemId(), null));
                    return result;
                }, () -> {
                    client.close();
                    return null;
                });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

}
