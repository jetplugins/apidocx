package com.github.jetplugins.yapix.action;

import static com.github.jetplugins.yapix.util.NotificationUtils.notifyError;
import static com.github.jetplugins.yapix.util.NotificationUtils.notifyInfo;

import com.github.jetplugins.yapix.config.YapiConfig;
import com.github.jetplugins.yapix.config.YapiConfigUtils;
import com.github.jetplugins.yapix.config.YapiSettings;
import com.github.jetplugins.yapix.constant.DefaultConstants;
import com.github.jetplugins.yapix.model.Api;
import com.github.jetplugins.yapix.parse.ApiParseSettings;
import com.github.jetplugins.yapix.parse.ApiParser;
import com.github.jetplugins.yapix.process.yapi.YapiUploader;
import com.github.jetplugins.yapix.sdk.yapi.YapiClient;
import com.github.jetplugins.yapix.sdk.yapi.mode.AuthCookies;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiInterface;
import com.github.jetplugins.yapix.ui.YapiConfigurationDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

/**
 * 处理Yapi上传入口动作.
 */
public class YapiUploadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) {
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

        // 账户模式获取token
        YapiSettings settings = YapiSettings.getInstance();
        if (!settings.isValidate()) {
            YapiConfigurationDialog.show(project);
            settings = YapiSettings.getInstance();
            if (!settings.isValidate()) {
                return;
            }
        }
        handle(project, event, config, settings);
    }

    private void handle(Project project, AnActionEvent event, YapiConfig config, YapiSettings settings) {
        Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile file = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            return;
        }
        PsiElement referenceAt = file.findElementAt(editor.getCaretModel().getOffset());
        PsiClass theClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        if (theClass == null) {
            return;
        }

        YapiClient client = new YapiClient(settings.getYapiUrl(), settings.getAccount(), settings.getPassword(),
                settings.getCookies(), settings.getCookiesTtl());
        ApiParseSettings parseSettings = new ApiParseSettings();
        parseSettings.setReturnClass(config.getReturnClass());
        ApiParser parser = new ApiParser(parseSettings);
        List<Api> apis = parser.parse(theClass);
        uploadAsync(apis, project, client, config, settings);
    }

    private void uploadAsync(List<Api> apis, Project project, YapiClient client, YapiConfig config,
            YapiSettings settings) {
        Integer projectId = Integer.valueOf(config.getProjectId());
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
