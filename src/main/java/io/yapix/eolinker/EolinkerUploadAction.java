package io.yapix.eolinker;

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
import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;
import io.yapix.base.sdk.eolinker.EolinkerClient;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.request.EolinkerTestResult;
import io.yapix.base.util.ConcurrentUtils;
import io.yapix.config.DefaultConstants;
import io.yapix.config.YapixConfig;
import io.yapix.eolinker.config.EolinkerSettings;
import io.yapix.eolinker.config.EolinkerSettingsDialog;
import io.yapix.eolinker.process.EolinkerUploader;
import io.yapix.model.Api;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Eolinker上传入口动作.
 */
public class EolinkerUploadAction extends AbstractAction {

    @Override
    public boolean before(AnActionEvent event, YapixConfig config) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        EolinkerSettings settings = EolinkerSettings.getInstance();
        if (!settings.isValidate() || EolinkerTestResult.Code.OK != settings.testSettings().getCode()) {
            EolinkerSettingsDialog dialog = EolinkerSettingsDialog.show(project);
            return !dialog.isCanceled();
        }
        return true;
    }

    @Override
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        String projectId = config.getEolinkerProjectId();
        Project project = event.getData(CommonDataKeys.PROJECT);

        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                EolinkerSettings settings = EolinkerSettings.getInstance();
                HttpSession session = new HttpSession(settings.getCookies(), settings.getCookiesTtl(),
                        settings.getSpaceKey());
                EolinkerClient client = new EolinkerClient(settings.getLoginUrl(), settings.getUrl(),
                        settings.getAccount(), settings.getPassword(), session);
                EolinkerUploader uploader = new EolinkerUploader(client);

                // 进度和并发
                Semaphore semaphore = new Semaphore(3);
                ExecutorService threadPool = Executors.newFixedThreadPool(3);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<EolinkerApiInfo> interfaces = null;
                try {
                    List<Future<EolinkerApiInfo>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<EolinkerApiInfo> future = threadPool.submit(() -> {
                            try {
                                // 上传
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(),
                                        api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return uploader.upload(projectId, api);
                            } catch (Exception e) {
                                notifyError(
                                        String.format("Eolinker Upload failed: [%s %s]", api.getMethod(),
                                                api.getPath()),
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
                        EolinkerApiInfo api = interfaces.get(0);
                        String url = client.calculateApiListUrl(projectId, api.getBaseInfo().getGroupID());
                        notifyInfo("Eolinker Upload successful", format("<a href=\"%s\">%s</a>", url, url));
                    }
                    client.close();
                    threadPool.shutdown();
                }
            }
        });
    }

}
