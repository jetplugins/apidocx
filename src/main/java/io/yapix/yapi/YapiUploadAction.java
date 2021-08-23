package io.yapix.yapi;

import static io.yapix.base.util.NotificationUtils.notifyError;
import static io.yapix.base.util.NotificationUtils.notifyInfo;
import static java.lang.String.format;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.yapix.action.AbstractAction;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.response.YapiTestResult.Code;
import io.yapix.base.util.ConcurrentUtils;
import io.yapix.base.util.NotificationUtils;
import io.yapix.config.DefaultConstants;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.yapi.config.YapiSettings;
import io.yapix.yapi.config.YapiSettingsDialog;
import io.yapix.yapi.process.YapiUploader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 处理Yapi上传入口动作.
 */
public class YapiUploadAction extends AbstractAction {

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        boolean check = checkConfig(config);
        if (!check) {
            return false;
        }
        Project project = event.getData(CommonDataKeys.PROJECT);
        YapiSettings settings = YapiSettings.getInstance();
        if (!settings.isValidate() || Code.OK != settings.testSettings().getCode()) {
            YapiSettingsDialog dialog = YapiSettingsDialog.show(project);
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        Integer projectId = Integer.valueOf(config.getYapiProjectId());
        Project project = event.getData(CommonDataKeys.PROJECT);

        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                YapiSettings settings = YapiSettings.getInstance();
                YapiClient client = new YapiClient(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                        settings.getCookies(), settings.getCookiesTtl());
                YapiUploader uploader = new YapiUploader(client);
                // 进度和并发
                Semaphore semaphore = new Semaphore(3);
                ExecutorService threadPool = Executors.newFixedThreadPool(3);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<YapiInterface> interfaces = null;
                try {
                    List<Future<YapiInterface>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<YapiInterface> future = threadPool.submit(() -> {
                            try {
                                // 上传
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(),
                                        api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return uploader.upload(projectId, api);
                            } catch (Exception e) {
                                notifyError(
                                        String.format("Yapi Upload failed: [%s %s]", api.getMethod(), api.getPath()),
                                        ExceptionUtils.getStackTrace(e));
                            } finally {
                                indicator.setFraction(fraction.addAndGet(step));
                                semaphore.release();
                            }
                            return null;
                        });
                        futures.add(future);
                    }
                    interfaces = ConcurrentUtils.waitFuturesSilence(futures);
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    if (interfaces != null && interfaces.size() > 0) {
                        YapiInterface yapi = interfaces.get(0);
                        String url = interfaces.size() == 1 && yapi.getId() != null ?
                                client.calculateInterfaceUrl(yapi.getProjectId(), yapi.getId())
                                : client.calculateCatUrl(yapi.getProjectId(), yapi.getCatid());
                        notifyInfo("Yapi Upload successful", format("<a href=\"%s\">%s</a>", url, url));
                    }
                    client.close();
                    threadPool.shutdown();
                }
            }
        });
    }

    private boolean checkConfig(YapixConfig config) {
        try {
            Integer.valueOf(config.getYapiProjectId());
            return true;
        } catch (NumberFormatException e) {
            NotificationUtils
                    .notifyError("Yapix config file error", "projectId or yapiProjectId must be integer number.");
        }
        return false;
    }

}
