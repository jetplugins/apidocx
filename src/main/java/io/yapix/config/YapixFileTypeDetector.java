package io.yapix.config;

import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry.FileTypeDetector;
import com.intellij.openapi.util.io.ByteSequence;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件配置文件类型检测
 */
public class YapixFileTypeDetector implements FileTypeDetector {

    @Nullable
    @Override
    public FileType detect(
            @NotNull VirtualFile file,
            @NotNull ByteSequence firstBytes,
            @Nullable CharSequence firstCharsIfText) {
        if (DefaultConstants.FILE_NAME.equals(file.getName())) {
            return PropertiesFileType.INSTANCE;
        }
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
