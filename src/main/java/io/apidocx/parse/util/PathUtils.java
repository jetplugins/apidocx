package io.apidocx.parse.util;

/**
 * URL中路径处理工具
 */
public class PathUtils {

    /**
     * 路径处理, 会增加前缀
     */
    public static String path(String path) {
        if (path == null || path.equals("")) {
            return null;
        }
        if (path.startsWith("/")) {
            return path.trim();
        }
        return "/" + path.trim();
    }

    /**
     * 路径拼接
     */
    public static String path(String path, String subPath) {
        path = path(path);
        subPath = path(subPath);
        if (path == null) {
            return subPath;
        }
        if (subPath == null) {
            return path;
        }
        if (path.endsWith("/") && subPath.startsWith("/")) {
            return path + subPath.substring(1);
        }
        return path + subPath;
    }

    /**
     * 清除路径变量中的正则表达式
     */
    public static String clearPathPattern(String path) {
        StringBuilder thePath = new StringBuilder();

        char[] chars = path.toCharArray();

        int pairCount = 0;                  // 匹配的括号对数量
        boolean inExpress = false;          // 是否在表达式中
        boolean inExpressPatten = false;    // 是否在表达式的正则表达式中
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '{' && !inExpress) {
                pairCount++;
                inExpress = true;
            } else if (c == '/' && inExpress) {
                inExpress = false;
            } else if (c == '}' && inExpress) {
                pairCount--;
                if (pairCount == 0) {
                    inExpress = false;
                }
            } else {
                if (inExpress && c == ':') {
                    inExpressPatten = true;
                }
            }

            boolean isPathVariablePattern = inExpress && inExpressPatten;
            if (!isPathVariablePattern) {
                thePath.append(c);
            }
        }

        return thePath.toString();
    }
}
