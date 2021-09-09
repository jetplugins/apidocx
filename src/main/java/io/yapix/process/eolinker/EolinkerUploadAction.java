package io.yapix.process.eolinker;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractionUploadAction;
import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;
import io.yapix.base.sdk.eolinker.EolinkerClient;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.process.eolinker.config.EolinkerSettings;
import io.yapix.process.eolinker.config.EolinkerSettingsDialog;
import io.yapix.process.eolinker.process.EolinkerUploader;
import java.util.List;

/**
 * Eolinker上传入口动作.
 */
public class EolinkerUploadAction extends AbstractionUploadAction {

    public static final String ACTION_TEXT = "Upload To Eolinker";

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
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
        HttpSession session = new HttpSession(settings.getCookies(), settings.getCookiesTtl(),
                settings.getSpaceKey());
        EolinkerClient client = new EolinkerClient(settings.getLoginUrl(), settings.getUrl(),
                settings.getAccount(), settings.getPassword(), session);
        EolinkerUploader uploader = new EolinkerUploader(client);

        super.handleUploadAsync(project, apis,
                api -> {
                    EolinkerApiInfo eapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setCategoryUrl(client.calculateApiListUrl(projectId, eapi.getBaseInfo().getGroupID()));
                    result.setApiUrl(result.getCategoryUrl());
                    return result;
                }, () -> {
                    client.close();
                    return null;
                });
    }

    @Override
    public void applyTextOverride(AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

}
