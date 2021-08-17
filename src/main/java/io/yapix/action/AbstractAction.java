package io.yapix.action;

import static io.yapix.base.NotificationUtils.notifyError;

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
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import io.yapix.config.YapiConfig;
import io.yapix.config.YapiConfigUtils;
import io.yapix.model.Api;
import io.yapix.parse.ApiParseSettings;
import io.yapix.parse.ApiParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * 处理Yapi上传入口动作.
 */
public abstract class AbstractAction extends AnAction {

    /**
     * 检查前操作
     */
    public abstract boolean before(AnActionEvent event);

    /**
     * 文档处理
     */
    public abstract void handle(AnActionEvent event, YapiConfig config, List<Api> apis);


    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || file == null) {
            return;
        }
        // 查找配置文件
        Module module = ModuleUtil.findModuleForFile(file, project);
        VirtualFile yapiConfigFile = YapiConfigUtils.findConfigFile(project, module);
        if (yapiConfigFile == null || !yapiConfigFile.exists()) {
            notifyError("Not found config file yapi.xml.");
            return;
        }
        // 获取配置
        YapiConfig config = null;
        try {
            String projectConfig = new String(yapiConfigFile.contentsToByteArray(), StandardCharsets.UTF_8);
            config = YapiConfigUtils.readFromXml(projectConfig, module != null ? module.getName() : null);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            notifyError(String.format("Config file error: %s", e.getMessage()));
            return;
        }
        // 配置校验
        if (!config.isValidate()) {
            notifyError("Yapi config required, please check config,[projectId,projectType]");
            return;
        }

        boolean isContinue = before(event);
        if (!isContinue) {
            return;
        }
        List<Api> apis = parse(event, config);
        handle(event, config, apis);
    }

    private List<Api> parse(AnActionEvent event, YapiConfig config) {
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        PsiFile file = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (editor == null || file == null) {
            return null;
        }
        PsiElement referenceAt = file.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        PsiMethod selectMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
        if (selectClass == null) {
            return null;
        }

        ApiParseSettings parseSettings = new ApiParseSettings();
        parseSettings.setReturnClass(config.getReturnClass());
        ApiParser parser = new ApiParser(parseSettings);
        return parser.parse(selectClass, selectMethod);
    }


}
