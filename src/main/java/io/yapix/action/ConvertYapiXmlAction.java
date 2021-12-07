package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import io.yapix.base.util.NotificationUtils;
import io.yapix.config.DefaultConstants;
import io.yapix.config.YapixConfig;
import io.yapix.config.YapixConfigUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

/**
 * 过渡支持，协助yapi.xml文件转换.yapi, 未来会移除
 */
public class ConvertYapiXmlAction extends AnAction {

    private static final String XML_FILE_NAME = "yapi.xml";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // 参数校验
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        VirtualFile file = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) {
            NotificationUtils.notifyError("Convert error not found file");
            return;
        }
        Module module = ModuleUtil.findModuleForFile(file, project);
        if (module == null) {
            NotificationUtils.notifyError("Convert error not found module");
            return;
        }

        // 转换文件
        try {
            String content = new String(file.contentsToByteArray(), StandardCharsets.UTF_8);
            YapixConfig config = YapixConfigUtils.readFromXml(content);
            writeFile(module, config);
            NotificationUtils.notifyInfo("Convert to .yapi successful");
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            NotificationUtils.notifyError("Convert error: " + ex.getMessage());
        }
    }

    private void writeFile(Module module, YapixConfig config) throws IOException {
        File moduleRoot = new File(module.getModuleFilePath()).getParentFile();
        File file = Paths.get(moduleRoot.getPath(), DefaultConstants.FILE_NAME).toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(file);) {
            Properties properties = new Properties();
            if (config.getYapiProjectId() != null) {
                properties.setProperty("yapiProjectId", config.getYapiProjectId());
            }
            if (config.getReturnWrapType() != null) {
                properties.setProperty("returnWrapType", config.getReturnWrapType());
            }
            if (config.getReturnUnwrapTypes() != null) {
                properties.setProperty("returnUnwrapTypes", StringUtils.join(config.getReturnUnwrapTypes(), ","));
            }
            properties.store(fos, " https://github.com/jetplugins/yapix/blob/main/docs/GUIDE.md");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        VirtualFile file = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        boolean visible = file != null && !file.isDirectory() && file.getName().equals(XML_FILE_NAME);
        event.getPresentation().setVisible(visible);
    }
}
