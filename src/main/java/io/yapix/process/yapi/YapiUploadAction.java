package io.yapix.process.yapi;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractionUploadAction;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.response.YapiTestResult.Code;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.process.yapi.config.YapiSettings;
import io.yapix.process.yapi.config.YapiSettingsDialog;
import io.yapix.process.yapi.process.YapiUploader;
import java.util.List;

/**
 * 处理Yapi上传入口动作.
 */
public class YapiUploadAction extends AbstractionUploadAction {

    public static final String ACTION_TEXT = "Upload To YApi";

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        YapiSettings settings = YapiSettings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings().getCode()) {
            YapiSettingsDialog dialog = YapiSettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        Integer projectId = Integer.valueOf(config.getYapiProjectId());
        Project project = event.getData(CommonDataKeys.PROJECT);

        YapiSettings settings = YapiSettings.getInstance();
        YapiClient client = new YapiClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getLoginWay(), settings.getCookies(), settings.getCookiesTtl());
        YapiUploader uploader = new YapiUploader(client);

        super.handleUploadAsync(project, apis,
                api -> {
                    YapiInterface yapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setApiUrl(client.calculateInterfaceUrl(projectId, yapi.getId()));
                    result.setCategoryUrl(client.calculateCatUrl(projectId, yapi.getCatid()));
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
