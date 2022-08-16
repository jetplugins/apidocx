package io.yapix.process.openapi;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import io.swagger.v3.oas.models.OpenAPI;
import io.yapix.action.AbstractAction;
import io.yapix.base.util.FileUtilsExt;
import io.yapix.base.util.NamedExclusionStrategy;
import io.yapix.base.util.NotificationUtils;
import io.yapix.base.util.PsiModuleUtils;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
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
    public void handle(AnActionEvent event, YapixConfig config, List<Api> apis) {
        Module module = PsiModuleUtils.findModuleByEvent(event);
        String modulePath = PsiModuleUtils.getModulePath(module);

        OpenAPI openApi = new OpenApiGenerator().generate(apis);
        openApi.getInfo().setTitle(module.getName());

        File file = new File(modulePath + File.separator + "openapi.json");
        try {
            String json = buildGson().toJson(openApi);
            FileUtilsExt.writeText(file, json);
            NotificationUtils.notifyInfo("Export to openapi successful.");
        } catch (IOException e) {
            throw new RuntimeException("Write openapi file error: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(ACTION_TEXT);
    }

    @NotNull
    private static Gson buildGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new NamedExclusionStrategy(Sets.newHashSet("exampleSetFlag", "specVersion")))
                .setPrettyPrinting()
                .create();
    }


}
