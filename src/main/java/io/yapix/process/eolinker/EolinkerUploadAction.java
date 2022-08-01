package io.yapix.process.eolinker;

import static io.yapix.base.util.NotificationUtils.notifyError;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractAction;
import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;
import io.yapix.base.sdk.eolinker.EolinkerClient;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import io.yapix.base.sdk.eolinker.util.EolinkerWebUrlCalculator;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.process.eolinker.config.EolinkerSettings;
import io.yapix.process.eolinker.config.EolinkerSettingsDialog;
import io.yapix.process.eolinker.process.EolinkerUploader;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Eolinker上传入口动作.
 */
public class EolinkerUploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To Eolinker";

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        String projectId = config.getEolinkerProjectId();
        if (StringUtils.isEmpty(projectId)) {
            notifyError("Config file error", "eolinkerProjectId must not be empty.");
            return false;
        }

        Project project = event.getData(CommonDataKeys.PROJECT);
        EolinkerSettings settings = EolinkerSettings.getInstance();
        if (!settings.isValidate() || EolinkerTestResult.Code.OK != settings.testSettings().getCode()) {
            EolinkerSettingsDialog dialog = EolinkerSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        String projectId = config.getEolinkerProjectId();
        Project project = event.getData(CommonDataKeys.PROJECT);

        EolinkerSettings settings = EolinkerSettings.getInstance();
        HttpSession session = new HttpSession(settings.getCookies(), settings.getCookiesTtl());
        EolinkerClient client = new EolinkerClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                session);
        EolinkerUploader uploader = new EolinkerUploader(client);
        EolinkerWebUrlCalculator urlCalculator = new EolinkerWebUrlCalculator(settings.getWebUrl());

        super.handleUploadAsync(project, apis,
                api -> {
                    EolinkerApiInfo eapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setCategoryUrl(urlCalculator.calculateApiListUrl(projectId,
                            eapi.getBaseInfo().getGroupID()));
                    result.setApiUrl(result.getCategoryUrl());
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
