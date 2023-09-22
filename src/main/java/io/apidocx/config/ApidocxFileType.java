package io.apidocx.config;

import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.PropertiesLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * 插件配置文件类型注册
 * 使用文件检测，存在一直索引的问题。导致鼠标右键无法出来，idea代码提示失效
 */
public class ApidocxFileType extends LanguageFileType {

    private final static String ID = "yapix";

    public static final ApidocxFileType INSTANCE = new ApidocxFileType();

    public ApidocxFileType() {
        super(PropertiesLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return ID;
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return ID;
    }

    @Override
    public @NotNull String getDescription() {
        return PropertiesFileType.INSTANCE.getDescription();
    }

    @Override
    public Icon getIcon() {
        // 使用PropertiesFileType 属性，保持向后的兼容性
        return PropertiesFileType.INSTANCE.getIcon();
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        // 使用PropertiesFileType 属性，保持向后的兼容性
        return PropertiesFileType.INSTANCE.getCharset(file, content);
    }

}
