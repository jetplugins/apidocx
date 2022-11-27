package io.apidocx.handle.rap2;

import static io.apidocx.base.util.NotificationUtils.notifyError;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.sdk.rap2.Rap2Client;
import io.apidocx.base.sdk.rap2.dto.TestResult.Code;
import io.apidocx.base.sdk.rap2.model.Rap2Interface;
import io.apidocx.base.sdk.rap2.util.Rap2WebUrlCalculator;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.handle.rap2.config.Rap2Settings;
import io.apidocx.handle.rap2.config.Rap2SettingsDialog;
import io.apidocx.handle.rap2.process.Rap2Uploader;
import io.apidocx.model.Api;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Rap2上传入口动作.
 */
public class Rap2UploadAction extends AbstractAction {

    public static final String ACTION_TEXT = "Upload To Rap2";

    @Override
    public boolean before(AnActionEvent event, ApidocxConfig config) {
        String projectId = config.getRap2ProjectId();
        if (StringUtils.isEmpty(projectId)) {
            notifyError("Config file error", "rap2ProjectId must not be empty.");
            return false;
        }

        Project project = event.getData(CommonDataKeys.PROJECT);
        Rap2Settings settings = Rap2Settings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings(null, null).getCode()) {
            Rap2SettingsDialog dialog = Rap2SettingsDialog.show(project, event.getPresentation().getText());
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        Integer projectId = Integer.valueOf(config.getRap2ProjectId());
        Project project = event.getData(CommonDataKeys.PROJECT);

        Rap2Settings settings = Rap2Settings.getInstance();
        Rap2Client client = new Rap2Client(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesUserId());
        Rap2Uploader uploader = new Rap2Uploader(client);
        Rap2WebUrlCalculator urlCalculator = new Rap2WebUrlCalculator(settings.getWebUrl());

        super.handleUploadAsync(project, apis,
                api -> {
                    Rap2Interface rapi = uploader.upload(projectId, api);

                    ApiUploadResult result = new ApiUploadResult();
                    result.setApiUrl(
                            urlCalculator.calculateEditorUrl(rapi.getRepositoryId(), rapi.getModuleId(), rapi.getId())
                    );
                    result.setCategoryUrl(
                            urlCalculator.calculateEditorUrl(rapi.getRepositoryId(), rapi.getModuleId(), null)
                    );
                    return result;
                }, null);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

}
