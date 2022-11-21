package io.yapix.base.sdk.apifox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isRoot() {
        return "root".equals(type);
    }
}
