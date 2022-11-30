package io.apidocx.base.sdk.apifox.model;

import com.google.gson.annotations.Expose;
import java.util.List;
import lombok.Data;

@Data
public class ApiTreeItem {
    private String key;
    private String name;
    private String type;
    private ApiInfo api;
    private FolderInfo folder;
    private List<ApiTreeItem> children;

    @Expose(serialize = false, deserialize = false)
    private String folderPath;

    public boolean isApiType() {
        return "apiDetail".equals(type);
    }

    public boolean isFolderType() {
        return "apiDetailFolder".equals(type);
    }

    @Data
    public static class FolderInfo {
        private Long id;
        private Long parentId;
        private String type;
    }

    @Data
    public static class ApiInfo {
        private Long id;
        private Long folderId;
        private String name;
        private String method;
        private String path;
        private List<String> tags;
        private String status;
    }
}
