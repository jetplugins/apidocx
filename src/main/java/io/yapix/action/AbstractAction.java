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
import io.yapix.eolinker.EolinkerUploadAction;
import io.yapix.model.Api;
import io.yapix.parse.ApiParser;
import io.yapix.rap2.Rap2UploadAction;
import io.yapix.yapi.YapiUploadAction;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理Yapi上传入口动作.
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

        // 配置文件解析
        VirtualFile file = psiFiles[0];
        Module module = ModuleUtil.findModuleForFile(file, project);
        VirtualFile yapiConfigFile = YapixConfigUtils.findConfigFile(project, module);
        if (yapiConfigFile == null || !yapiConfigFile.exists()) {
            notifyError("Not found config file .yapi or yapi.xml");
            return;
        }
        YapixConfig config;
        try {
            config = YapixConfigUtils.readYapixConfig(yapiConfigFile, module != null ? module.getName() : null);
        } catch (Exception e) {
            notifyError(String.format("Config file error: %s", e.getMessage()));
            return;
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
        List<Api> apis = parse(config, psiClasses, selectMethod);
        handle(event, config, apis);
    }

    /**
     * 解析文档模型数据
     */
    private List<Api> parse(YapixConfig config, List<PsiClass> controllers, PsiMethod selectMethod) {
        ApiParser parser = new ApiParser(config);
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


}
