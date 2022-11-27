package io.apidocx.base.util;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import io.apidocx.parse.util.PropertiesLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@UtilityClass
public class FileUtilsExt {

    /**
     * 写入文件，1.自动创建目录， 2.写入文件, 3.vf刷新
     */
    public static VirtualFile writeText(File file, String text) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileUtils.writeStringToFile(file, text, StandardCharsets.UTF_8);
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
    }

    /**
     * 读取文本文件
     */
    public static String readTextInResource(String relativeFile) throws IOException {
        InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(relativeFile);
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}
