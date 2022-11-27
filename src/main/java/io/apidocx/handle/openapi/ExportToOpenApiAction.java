package io.apidocx.handle.openapi;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import io.apidocx.action.AbstractAction;
import io.apidocx.base.util.FileUtilsExt;
import io.apidocx.base.util.NotificationUtils;
import io.apidocx.base.util.PsiModuleUtils;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Api;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * 导出文档为OpenAPI文档
 */
public class ExportToOpenApiAction extends AbstractAction {

    public static final String ACTION_TEXT = "Export To OpenAPI";

    public ExportToOpenApiAction() {
        super(false);
    }

    @Override
    public void handle(AnActionEvent event, ApidocxConfig config, List<Api> apis) {
        Module module = PsiModuleUtils.findModuleByEvent(event);
        String modulePath = PsiModuleUtils.getModulePath(module);

        OpenApiFileType fileType = OpenApiFileType.YAML;

        OpenAPI openApi = new OpenApiDataConvert().convert(apis);
        openApi.getInfo().setTitle(module.getName());
        String content = new OpenApiGenerator().generate(fileType, openApi);

        String filename = "openapi" + fileType.getSuffix();
        File file = new File(modulePath + File.separator + filename);
        try {
            FileUtilsExt.writeText(file, content);
            NotificationUtils.notifyInfo("Export to openapi successful.");
        } catch (IOException e) {
            throw new RuntimeException("Write openapi file error: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

}
