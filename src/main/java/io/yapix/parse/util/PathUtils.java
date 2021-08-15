package io.yapix.parse.util;

public class PathUtils {

    /**
     * 路径处理, 会增加前缀
     */
    public static String path(String path) {
        if (path == null || path.equals("")) {
            return path;
        }
        if (path.startsWith("/") && !path.endsWith("/")) {
            return path;
        }
        return "/" + path;
    }

    /**
     * 路径拼接
     */
    public static String path(String path, String subPath) {
        return path(path) + path(subPath);
    }

}
