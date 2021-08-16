package io.yapix.action;

import static io.yapix.base.util.NotificationUtils.notifyError;
import static io.yapix.base.util.NotificationUtils.notifyInfo;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.yapix.base.DefaultConstants;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.mode.AuthCookies;
import io.yapix.base.sdk.yapi.mode.YapiInterface;
import io.yapix.config.YapiConfig;
import io.yapix.config.yapi.YapiConfigurationDialog;
import io.yapix.config.yapi.YapiSettings;
import io.yapix.model.Api;
import io.yapix.process.yapi.YapiUploader;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 处理Yapi上传入口动作.
 */
public class YapiUploadAction extends AbstractAction {

    @Override
    boolean before(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        // 账户模式获取token
        YapiSettings settings = YapiSettings.getInstance();
        if (!settings.isValidate()) {
            YapiConfigurationDialog.show(project);
            settings = YapiSettings.getInstance();
            if (!settings.isValidate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    void handle(AnActionEvent event, YapiConfig config, List<Api> apis) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        YapiSettings settings = YapiSettings.getInstance();
        YapiClient client = new YapiClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesTtl());
        Integer projectId = Integer.valueOf(config.getProjectId());

        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                double step = 1.0 / apis.size();
                String categoryUrl = null;
                String interfaceUrl = null;
                YapiUploader uploader = new YapiUploader(client);
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
                            YapiInterface yapi = uploader.upload(projectId, api);
                            categoryUrl = client.calculateCatUrl(yapi.getProjectId(), yapi.getCatid());
                            interfaceUrl = client.calculateInterfaceUrl(yapi.getProjectId(), yapi.getId());
                        } catch (Exception e) {
                            notifyError("Yapi Upload failed", ExceptionUtils.getStackTrace(e));
                        }
                        indicator.setFraction(indicator.getFraction() + step);
                    }
                    // 保存认证信息
                    AuthCookies cookies = client.getAuthCookies();
                    settings.setCookies(cookies.getCookies());
                    settings.setCookiesTtl(cookies.getTtl());
                } finally {
                    String url = apis.size() == 1 ? interfaceUrl : categoryUrl;
                    if (url != null) {
                        notifyInfo("Yapi Upload successful", String.format("<a href=\"%s\">%s</a>", url, url));
                    }
                }
            }
        });
    }

}
