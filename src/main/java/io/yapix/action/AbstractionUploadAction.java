package io.yapix.action;

import static io.yapix.base.util.NotificationUtils.notifyError;
import static io.yapix.base.util.NotificationUtils.notifyInfo;
import static java.lang.String.format;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import io.yapix.base.util.ConcurrentUtils;
import io.yapix.config.DefaultConstants;
import io.yapix.model.Api;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 文件上传处理
 */
public abstract class AbstractionUploadAction extends AbstractAction {

    /**
     * 异步上传模板方法
     *
     * @param project     项目
     * @param apis        待处理接口列表
     * @param apiConsumer 单个接口数据消费者
     * @param afterAction 所有接口列表处理完毕后的回调执行，用于关闭资源
     */
    protected <T> void handleUploadAsync(Project project, List<Api> apis, Function<Api, ApiUploadResult> apiConsumer,
            Supplier<?> afterAction) {
        // 异步处理
        ProgressManager.getInstance().run(new Task.Backgroundable(project, DefaultConstants.NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                int poolSize = apis.size() == 1 ? 1 : 4;
                // 进度和并发
                Semaphore semaphore = new Semaphore(poolSize);
                ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<ApiUploadResult> urls = null;
                try {
                    List<Future<ApiUploadResult>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<ApiUploadResult> future = threadPool.submit(() -> {
                            try {
                                // 上传
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(),
                                        api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return apiConsumer.apply(api);
                            } catch (Exception e) {
                                notifyError(
                                        String.format("Upload failed: [%s %s]", api.getMethod(), api.getPath()),
                                        ExceptionUtils.getStackTrace(e));
                            } finally {
                                indicator.setFraction(fraction.addAndGet(step));
                                semaphore.release();
                            }
                            return null;
                        });
                        futures.add(future);
                    }
                    urls = ConcurrentUtils.waitFuturesSilence(futures).stream()
                            .filter(Objects::nonNull).collect(Collectors.toList());
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    if (urls != null && urls.size() > 0) {
                        ApiUploadResult uploadResult = urls.get(0);
                        String url = urls.size() == 1 ? uploadResult.getApiUrl() : uploadResult.getCategoryUrl();
                        notifyInfo("Upload successful", format("<a href=\"%s\">%s</a>", url, url));
                    }
                    threadPool.shutdown();
                    afterAction.get();
                }
            }
        });
    }


    public static class ApiUploadResult {

        private String categoryUrl;
        private String apiUrl;

        //------------------ generated ------------------------//

        public String getCategoryUrl() {
            return categoryUrl;
        }

        public void setCategoryUrl(String categoryUrl) {
            this.categoryUrl = categoryUrl;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }
}
