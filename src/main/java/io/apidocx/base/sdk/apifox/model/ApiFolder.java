package io.apidocx.base.sdk.apifox.model;

import com.google.gson.annotations.Expose;
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

    @Expose(serialize = false, deserialize = false)
    private String folderPath;

    public boolean isRoot() {
        return "root".equals(type);
    }
}
