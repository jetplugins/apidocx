package io.yapix.action;

import static io.yapix.base.util.NotificationUtils.notifyError;
import static io.yapix.base.util.NotificationUtils.notifyInfo;
import static java.lang.String.format;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import io.yapix.base.StepResult;
import io.yapix.base.util.ConcurrentUtils;
import io.yapix.base.util.NotificationUtils;
import io.yapix.base.util.PsiFileUtils;
import io.yapix.config.DefaultConstants;
import io.yapix.config.YapixConfig;
import io.yapix.config.YapixConfigUtils;
import io.yapix.model.Api;
import io.yapix.parse.ApiParser;
import io.yapix.parse.model.ClassParseData;
import io.yapix.parse.model.MethodParseData;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * API文档解析处理的动作模板类
 */
public abstract class AbstractAction extends AnAction {

    /**
     * 配置文件是否必须
     */
    private final boolean requiredConfigFile;

    protected AbstractAction() {
        this.requiredConfigFile = true;
    }

    protected AbstractAction(boolean requiredConfigFile) {
        this.requiredConfigFile = requiredConfigFile;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // make sure documents all saved before refresh v-files in sync/recursive.
        FileDocumentManager.getInstance().saveAllDocuments();
        EventData data = EventData.of(event);
        if (!data.shouldHandle()) {
            return;
        }
        // 1.解析配置
        StepResult<YapixConfig> configResult = resolveConfig(data);
        YapixConfig config = configResult.getData();
        if (!configResult.isContinue()) {
            return;
        }
        // 2.前置处理
        if (!before(event, config)) {
            return;
        }
        // 3.解析文档
        StepResult<List<Api>> apisResult = parse(data, config);
        if (!apisResult.isContinue()) {
            return;
        }
        // 4.文档处理
        List<Api> apis = apisResult.getData();
        handle(event, config, apis);
    }

    /**
     * 检查前操作
     */
    public boolean before(AnActionEvent event, YapixConfig config) {
        return true;
    }

    /**
     * 文档处理
     */
    public abstract void handle(AnActionEvent event, YapixConfig config, List<Api> apis);

    /**
     * 解析文档模型数据
     */
    private StepResult<List<Api>> parse(EventData data, YapixConfig config) {
        ApiParser parser = new ApiParser(data.project, data.module, config);
        // 选中方法
        if (data.selectedMethod != null) {
            MethodParseData methodData = parser.parse(data.selectedMethod);
            if (!methodData.valid) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME,
                        "The current method is not a valid api or ignored");
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(methodData.declaredApiSummary)) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME, "The current method must declare summary");
                return StepResult.stop();
            }
            return StepResult.ok(methodData.apis);
        }

        // 选中类
        if (data.selectedClass != null) {
            ClassParseData controllerData = parser.parse(data.selectedClass);
            if (!controllerData.valid) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME,
                        "The current class is not a valid controller or ignored");
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.declaredCategory)) {
                NotificationUtils.notifyWarning(DefaultConstants.NAME, "The current class must declare category");
                return StepResult.stop();
            }
            return StepResult.ok(controllerData.getApis());
        }

        // 批量
        List<PsiClass> controllers = PsiFileUtils.getPsiClassByFile(data.selectedJavaFiles);
        if (controllers.isEmpty()) {
            NotificationUtils.notifyWarning(DefaultConstants.NAME, "Not found valid controller class");
            return StepResult.stop();
        }
        List<Api> apis = Lists.newLinkedList();
        for (PsiClass controller : controllers) {
            ClassParseData controllerData = parser.parse(controller);
            if (!controllerData.valid) {
                continue;
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.declaredCategory)) {
                continue;
            }
            List<Api> controllerApis = controllerData.getApis();
            if (config.isStrict()) {
                controllerApis = controllerApis.stream().filter(o -> StringUtils.isNotEmpty(o.getSummary()))
                        .collect(Collectors.toList());
            }
            apis.addAll(controllerApis);
        }
        return StepResult.ok(apis);
    }

    /**
     * 获取配置
     */
    private StepResult<YapixConfig> resolveConfig(EventData data) {
        // 配置文件解析
        VirtualFile file = YapixConfigUtils.findConfigFile(data.project, data.module);
        if (requiredConfigFile && (file == null || !file.exists())) {
            NotificationUtils.notify(NotificationType.WARNING, "",
                    "Not found config file .yapix",
                    new CreateConfigFileAction(data.project, data.module, "Create Config File"));
            return StepResult.stop();
        }
        YapixConfig config = null;
        if (file != null && file.exists()) {
            try {
                config = YapixConfigUtils.readYapixConfig(file);
            } catch (Exception e) {
                notifyError(String.format("Config file error: %s", e.getMessage()));
                return StepResult.stop();
            }
        }
        if (config == null) {
            config = new YapixConfig();
        }
        config = YapixConfig.getMergedInternalConfig(config);
        return StepResult.ok(config);
    }


    /**
     * 异步上传模板方法
     *
     * @param project     项目
     * @param apis        待处理接口列表
     * @param apiConsumer 单个接口数据消费者
     * @param afterAction 所有接口列表处理完毕后的回调执行，用于关闭资源
     */
    protected void handleUploadAsync(Project project, List<Api> apis, Function<Api, ApiUploadResult> apiConsumer,
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
                    if (urls != null && !urls.isEmpty()) {
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

    static class EventData {

        /**
         * 源事件
         */
        AnActionEvent event;
        /**
         * 项目
         */
        Project project;

        /**
         * 模块
         */
        Module module;

        /**
         * 选择的文件
         */
        VirtualFile[] selectedFiles;

        /**
         * 选择的Java文件
         */
        List<PsiJavaFile> selectedJavaFiles;

        /**
         * 选择类
         */
        PsiClass selectedClass;

        /**
         * 选择方法
         */
        PsiMethod selectedMethod;

        /**
         * 是否应当继续解析处理
         */
        public boolean shouldHandle() {
            return project != null && module != null && (selectedJavaFiles != null || selectedClass != null);
        }

        /**
         * 从事件中解析需要的通用数据
         */
        public static EventData of(AnActionEvent event) {
            EventData data = new EventData();
            data.event = event;
            data.project = event.getData(CommonDataKeys.PROJECT);
            data.module = event.getData(LangDataKeys.MODULE);
            data.selectedFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
            if (data.project != null && data.selectedFiles != null) {
                data.selectedJavaFiles = PsiFileUtils.getPsiJavaFiles(data.project, data.selectedFiles);
            }
            Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
            PsiFile editorFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
            if (editor != null && editorFile != null) {
                PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
                data.selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
                data.selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            }
            return data;
        }
    }
}
