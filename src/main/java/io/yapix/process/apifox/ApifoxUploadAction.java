package io.yapix.process.apifox;

import static io.yapix.base.util.NotificationUtils.notifyError;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractAction;
import io.yapix.base.sdk.apifox.ApifoxClient;
import io.yapix.base.sdk.apifox.ApifoxWebUrlCalculator;
import io.yapix.base.sdk.apifox.model.ApifoxTestResult;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.process.apifox.config.ApifoxSettings;
import io.yapix.process.apifox.config.ApifoxSettingsDialog;
import io.yapix.process.apifox.process.ApifoxUploader;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Apifox上传入口动作.
 */
public class ApifoxUploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To Apifox";

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        String projectId = config.getApifoxProjectId();
        if (StringUtils.isEmpty(projectId)) {
            notifyError("Config file error", "apifoxProjectId must not be empty.");
            return false;
        }

        Project project = event.getData(CommonDataKeys.PROJECT);
        ApifoxSettings settings = ApifoxSettings.getInstance();
        if (!settings.isValidate() || ApifoxTestResult.Code.OK != settings.testSettings().getCode()) {
            ApifoxSettingsDialog dialog = ApifoxSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        Long projectId = Long.valueOf(config.getApifoxProjectId());
        Project project = event.getData(CommonDataKeys.PROJECT);

        ApifoxSettings settings = ApifoxSettings.getInstance();
        ApifoxClient client = new ApifoxClient(settings.getUrl(), settings.getAccount(), settings.getPassword(), settings.getAccessToken(), projectId);
        ApifoxWebUrlCalculator urlCalculator = new ApifoxWebUrlCalculator(settings.getWebUrl());
        ApifoxUploader uploader = new ApifoxUploader(client);

        super.handleUploadAsync(project, apis,
                api -> {
                    Long apiId = uploader.upload(projectId, api);
                    ApiUploadResult result = new ApiUploadResult();
                    result.setCategoryUrl(urlCalculator.projectUrl(projectId));
                    result.setApiUrl(urlCalculator.apiUrl(projectId, apiId));
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
