package io.yapix.action;

import static io.yapix.base.util.NotificationUtils.notifyError;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import io.yapix.base.util.PsiFileUtils;
import io.yapix.config.YapixConfig;
import io.yapix.config.YapixConfigUtils;
import io.yapix.config.YapixSettings;
import io.yapix.model.Api;
import io.yapix.parse.ApiParser;
import io.yapix.process.eolinker.EolinkerUploadAction;
import io.yapix.process.rap2.Rap2UploadAction;
import io.yapix.process.yapi.YapiUploadAction;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * API文档解析处理的动作模板类
 */
public abstract class AbstractAction extends AnAction {

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

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        VirtualFile[] psiFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (project == null || psiFiles == null) {
            return;
        }
        List<PsiJavaFile> psiJavaFiles = PsiFileUtils.getPsiJavaFiles(project, psiFiles);
        if (psiJavaFiles.size() == 0) {
            return;
        }
        YapixSettings settings = YapixSettings.getInstance();
        ActionType defaultAction = settings.getDefaultAction();

        // 配置文件解析
        VirtualFile file = psiFiles[0];
        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module == null) {
            return;
        }
        VirtualFile yapiConfigFile = YapixConfigUtils.findConfigFile(project, module);
        if (yapiConfigFile == null || !yapiConfigFile.exists()) {
            notifyError("Not found config file .yapi or yapi.xml");
            return;
        }
        YapixConfig config = null;
        try {
            config = YapixConfigUtils.readYapixConfig(yapiConfigFile, module != null ? module.getName() : null);
        } catch (Exception e) {
            if (defaultAction == null || defaultAction.isRequiredConfigFile()) {
                notifyError(String.format("Config file error: %s", e.getMessage()));
                return;
            }
        }
        if (!checkConfig(config)) {
            return;
        }

        boolean isContinue = before(event, config);
        if (!isContinue) {
            return;
        }

        List<PsiClass> psiClasses;
        PsiMethod selectMethod = null;

        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {
            psiClasses = Lists.newArrayList();
            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                psiClasses.add(selectClass);
                selectMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            }
        } else {
            psiClasses = PsiFileUtils.getPsiClassByFile(psiJavaFiles);
        }
        List<Api> apis = parse(project, module, config, psiClasses, selectMethod);
        handle(event, config, apis);
    }

    /**
     * 解析文档模型数据
     */
    private List<Api> parse(Project project, Module module, YapixConfig config, List<PsiClass> controllers,
            PsiMethod selectMethod) {
        ApiParser parser = new ApiParser(project, module, config);
        List<Api> apis = Lists.newLinkedList();
        for (PsiClass controller : controllers) {
            List<Api> controllerApis = parser.parse(controller, selectMethod);
            if (controllerApis != null) {
                apis.addAll(controllerApis);
            }
        }
        return apis;
    }

    /**
     * 配置检查
     */
    private boolean checkConfig(YapixConfig config) {
        // yapi
        if (this.getClass() == YapiUploadAction.class) {
            String projectId = config.getYapiProjectId();
            if (StringUtils.isEmpty(projectId)) {
                notifyError("Config file error", "yapiProjectId must not be empty.");
                return false;
            }
            if (StringUtils.isNotEmpty(config.getYapiProjectToken()) && StringUtils.isEmpty(config.getYapiUrl())) {
                notifyError("Config file error", "yapiUrl must not be empty, when you config yapiProjectToken.");
                return false;
            }
        }
        // rap2
        if (this.getClass() == Rap2UploadAction.class) {
            String projectId = config.getRap2ProjectId();
            if (StringUtils.isEmpty(projectId)) {
                notifyError("Config file error", "rap2ProjectId must not be empty.");
                return false;
            }
        }
        // eolinker
        if (this.getClass() == EolinkerUploadAction.class) {
            String projectId = config.getEolinkerProjectId();
            if (StringUtils.isEmpty(projectId)) {
                notifyError("Config file error", "eolinkerProjectId must not be empty.");
                return false;
            }
        }
        return true;
    }

    //--------------- 辅助方法 -------------------//

    /**
     * 是否选中了方法
     */
    protected boolean isSelectedMethod(@NotNull AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile editorFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor != null && editorFile != null) {

            PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
            if (selectClass != null) {
                PsiMethod method = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
                return method != null;
            }
        }
        return true;
    }
}
