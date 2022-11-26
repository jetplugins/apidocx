package io.apidocx.config;

import com.google.common.collect.Lists;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry.FileTypeDetector;
import com.intellij.openapi.util.io.ByteSequence;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件配置文件类型检测
 */
public class ApidocxFileTypeDetector implements FileTypeDetector {

    @Nullable
    @Override
    public FileType detect(
            @NotNull VirtualFile file,
            @NotNull ByteSequence firstBytes,
            @Nullable CharSequence firstCharsIfText) {
        if (DefaultConstants.FILE_NAME.equals(file.getName())) {
            return getFileType();
        }
        return null;
    }

    public int getVersion() {
        return 0;
    }

    @Nullable
    public Collection<? extends FileType> getDetectedFileTypes() {
        return Lists.newArrayList(getFileType());
    }

    private FileType getFileType() {
        try {
            return PropertiesFileType.INSTANCE;
        } catch (Exception e) {
            return null;
        }
    }
}
