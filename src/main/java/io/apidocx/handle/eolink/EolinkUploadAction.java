package io.apidocx.handle.eolink;

import static io.apidocx.base.util.NotificationUtils.notifyError;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.sdk.eolink.EolinkClient;
import io.apidocx.base.sdk.eolink.EolinkWebUrlCalculator;
import io.apidocx.base.sdk.eolink.model.ApiInfo;
import io.apidocx.base.sdk.eolink.request.TestResult;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.handle.eolink.config.EolinkSettings;
import io.apidocx.handle.eolink.config.EolinkSettingsDialog;
import io.apidocx.handle.eolink.process.EolinkUploader;
import io.apidocx.model.Api;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Eolink上传入口动作.
 */
public class EolinkUploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To Eolink";

    @Override
    public boolean before(AnActionEvent event, ApidocxConfig config) {
        String projectId = config.getEolinkProjectId();
        if (StringUtils.isEmpty(projectId)) {
            notifyError("Config file error", "eolinkerProjectId must not be empty.");
            return false;
        }

        Project project = event.getData(CommonDataKeys.PROJECT);
        EolinkSettings settings = EolinkSettings.getInstance();
        if (!settings.isValidate() || TestResult.Code.OK != settings.testSettings().getCode()) {
            EolinkSettingsDialog dialog = EolinkSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        String projectId = config.getEolinkProjectId();
        Project project = event.getData(CommonDataKeys.PROJECT);

        EolinkSettings settings = EolinkSettings.getInstance();
        EolinkClient client = new EolinkClient(settings.getUrl(), settings.getLoginUrl(), settings.getAccount(), settings.getPassword(), settings.getAccessToken());
        EolinkUploader uploader = new EolinkUploader(client);
        EolinkWebUrlCalculator urlCalculator = new EolinkWebUrlCalculator(settings.getWebUrl());

        super.handleUploadAsync(project, apis,
                api -> {
                    ApiInfo eapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setCategoryUrl(urlCalculator.calculateApiListUrl(projectId,
                            eapi.getBaseInfo().getGroupID()));
                    result.setApiUrl(result.getCategoryUrl());
                    return result;
                }, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }
}
