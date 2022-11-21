package io.yapix.base.sdk.apifox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isApiType() {
        return "apiDetail".equals(type);
    }

    @JsonIgnore
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
