package io.apidocx.base.sdk.apifox.model;

import java.util.List;
import lombok.Data;

@Data
public class ApiFolder {

    private Long id;

    private String name;

    private String description;

    private Long parentId;

    private List<ApiFolder> children;

    private String type;

    public boolean isRoot() {
        return "root".equals(type);
    }
}
