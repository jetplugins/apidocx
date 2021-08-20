package io.yapix.rap2;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
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
        Project project = event.getData(CommonDataKeys.PROJECT);

        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                Rap2Settings settings = Rap2Settings.getInstance();
                Rap2Client client = new Rap2Client(settings.getUrl(), settings.getAccount(), settings.getPassword(),
                        settings.getCookies(), settings.getCookiesTtl(), settings.getCookiesUserId());
                Integer projectId = Integer.valueOf(config.getProjectId());
                Rap2Uploader uploader = new Rap2Uploader(client);
                Rap2WebUrlCalculator urlCalculator = new Rap2WebUrlCalculator(settings.getWebUrl());
                // 进度和并发
                Semaphore semaphore = new Semaphore(3);
                ExecutorService threadPool = Executors.newFixedThreadPool(3);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<Rap2Interface> interfaces = null;
                try {
                    List<Future<Rap2Interface>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<Rap2Interface> future = threadPool.submit(() -> {
                            try {
                                // 上传
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(),
                                        api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return uploader.upload(projectId, api);
                            } catch (Exception e) {
                                notifyError(
                                        String.format("Rap2 Upload failed: [%s %s]", api.getMethod(), api.getPath()),
                                        ExceptionUtils.getStackTrace(e));
                            } finally {
                                indicator.setFraction(fraction.addAndGet(step));
                                semaphore.release();
                            }
                            return null;
                        });
                        futures.add(future);
                    }
                    interfaces = waitFuturesSilence(futures);
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    if (interfaces != null && interfaces.size() > 0) {
                        Rap2Interface rapi = interfaces.get(0);
                        String url = interfaces.size() == 1 && rapi.getId() != null ?
                                urlCalculator
                                        .calculateEditorUrl(rapi.getRepositoryId(), rapi.getModuleId(), rapi.getId())
                                : urlCalculator.calculateEditorUrl(rapi.getRepositoryId(), rapi.getModuleId(), null);
                        notifyInfo("Rap2 Upload successful", format("<a href=\"%s\">%s</a>", url, url));
                    }
                    client.close();
                    threadPool.shutdown();
                }
            }
        });
    }

    private static <T> List<T> waitFuturesSilence(List<Future<T>> futures) {
        List<T> values = Lists.newArrayListWithExpectedSize(futures.size());
        for (Future<T> future : futures) {
            try {
                T value = future.get();
                if (value != null) {
                    values.add(value);
                }
            } catch (InterruptedException | ExecutionException e) {
                // ignore
            }
        }
        return values;
    }

}
