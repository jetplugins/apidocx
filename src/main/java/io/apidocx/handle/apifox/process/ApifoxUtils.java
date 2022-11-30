package io.apidocx.handle.apifox.process;

import io.apidocx.base.sdk.apifox.model.ApiFolder;
import io.apidocx.base.sdk.apifox.model.ApiTreeItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class ApifoxUtils {

    private static final String FOLDER_SPLITTER = "/";

    /**
     * 扁平化树目录结构
     */
    public static List<ApiFolder> flatFolders(List<ApiFolder> folders) {
        List<ApiFolder> data = new ArrayList<>();
        for (ApiFolder folder : folders) {
            folder.setFolderPath(folder.getName());
            data.add(folder);
            List<ApiFolder> children = folder.getChildren();
            if (children != null && !children.isEmpty()) {
                List<ApiFolder> flatItems = flatFolders(children);
                flatItems.forEach(one -> {
                    if (one.getFolderPath() == null) {
                        one.setFolderPath(folder.getFolderPath());
                    } else {
                        one.setFolderPath(folder.getFolderPath() + FOLDER_SPLITTER + one.getFolderPath());
                    }
                });
                data.addAll(flatItems);
            }
        }
        return data;
    }

    /**
     * 转化为扁平的api树
     */
    public static List<ApiTreeItem> flatApiTree(List<ApiTreeItem> items) {
        List<ApiTreeItem> data = new ArrayList<>();
        for (ApiTreeItem item : items) {
            data.add(item);
            if (item.isFolderType()) {
                item.setFolderPath(item.getName());
            }
            List<ApiTreeItem> children = item.getChildren();
            if (children != null && !children.isEmpty()) {
                List<ApiTreeItem> flatItems = flatApiTree(children);
                flatItems.forEach(one -> {
                    if (one.getFolderPath() == null) {
                        one.setFolderPath(item.getFolderPath());
                    } else {
                        one.setFolderPath(item.getFolderPath() + FOLDER_SPLITTER + one.getFolderPath());
                    }
                });
                data.addAll(flatItems);
            }
        }
        return data;
    }

    /**
     * 目录路径
     */
    public static String folderPath(String path) {
        if (path == null) {
            return null;
        }
        List<String> names = ApifoxUtils.splitFolderPaths(path);
        return ApifoxUtils.getFolderPath(names);
    }

    /**
     * 分割为多目录
     */
    public static List<String> splitFolderPaths(String path) {
        return Arrays.stream(path.split(FOLDER_SPLITTER))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    /**
     * 转换成目录路径
     */
    public static String getFolderPath(List<String> names) {
        return String.join(FOLDER_SPLITTER, names);
    }


}
