package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import lombok.Data;

/**
 * 新增菜单
 */
@Data
public class CategoryCreateRequest {

    /**
     * 项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 名字
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

    /**
     * 父级菜单id
     */
    @SerializedName("parent_id")
    private Integer parentId = -1;

    /**
     * token
     */
    private String token;

    public CategoryCreateRequest() {
    }

    public CategoryCreateRequest(String name, Integer projectId, Integer parentId) {
        this.name = name;
        this.projectId = projectId;
        this.parentId = parentId;
        if (Objects.isNull(parentId)) {
            this.parentId = -1;
        }
    }

}
