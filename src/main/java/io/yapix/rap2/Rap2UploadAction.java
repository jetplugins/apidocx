package io.yapix.rap2;

import static io.yapix.base.NotificationUtils.notifyError;
import static io.yapix.base.NotificationUtils.notifyInfo;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractAction;
import io.yapix.base.sdk.rap2.Rap2Client;
import io.yapix.base.sdk.rap2.model.Rap2Interface;
import io.yapix.base.sdk.rap2.request.Rap2TestResult.Code;
import io.yapix.base.sdk.rap2.util.Rap2WebUrlCalculator;
import io.yapix.config.DefaultConstants;
import io.yapix.config.YapiConfig;
import io.yapix.model.Api;
import io.yapix.rap2.config.Rap2Settings;
import io.yapix.rap2.config.Rap2SettingsDialog;
import io.yapix.rap2.process.Rap2Uploader;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Rap2上传入口动作.
 */
public class Rap2UploadAction extends AbstractAction {

    @Override
    public boolean before(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        Rap2Settings settings = Rap2Settings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings(null, null).getCode()) {
            Rap2SettingsDialog dialog = Rap2SettingsDialog.show(project);
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapiConfig config, List<Api> apis) {
        Integer projectId = Integer.valueOf(config.getProjectId());
        Project project = event.getData(CommonDataKeys.PROJECT);
        Rap2Settings settings = Rap2Settings.getInstance();
        Rap2Client client = new Rap2Client(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesTtl(), settings.getCookiesUserId());

        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                double step = 1.0 / apis.size();

                String url = null;
                Rap2Uploader uploader = new Rap2Uploader(client);
                Rap2WebUrlCalculator urlCalculator = new Rap2WebUrlCalculator(settings.getWebUrl());
                try {
                    for (int i = 0; i < apis.size(); i++) {
                        if (indicator.isCanceled()) {
                            break;
                        }
                        Api api = apis.get(i);
                        indicator.setText("[" + (i + 1) + "/" + apis.size() + "] " + api.getMethod() + " "
                                + api
                                .getPath());
                        try {
                            // 上传
                            Rap2Interface rapi = uploader.upload(projectId, api);
                            url = urlCalculator.calculateEditorUrl(rapi.getRepositoryId(), rapi.getModuleId(),
                                    rapi.getId());
                        } catch (Exception e) {
                            notifyError("Rap2 Upload failed", ExceptionUtils.getStackTrace(e));
                        }
                        indicator.setFraction(indicator.getFraction() + step);
                    }
                } finally {
                    if (url != null) {
                        notifyInfo("Rap2 Upload successful", String.format("<a href=\"%s\">%s</a>", url, url));
                    }
                }
            }
        });
    }

}
