package io.yapix.base.util;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import io.yapix.parse.util.PropertiesLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileUtilsExt {
    private FileUtilsExt() {
    }

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
     * 写入文件，1.自动创建目录， 2.写入文件, 3.vf刷新
     */
    public static VirtualFile writeProperties(File file, Properties properties, String comments) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(file);) {
            properties.store(new OutputStreamWriter(fos, StandardCharsets.UTF_8), comments);
            return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        }
    }

    public static String readTextInResource(String relativeFile) throws IOException {
        InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(relativeFile);
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}
