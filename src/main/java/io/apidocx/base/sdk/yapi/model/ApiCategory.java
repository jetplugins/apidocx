package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 分类列表
 */
@Data
public class ApiCategory {

    /**
     * id
     */
    @SerializedName("_id")
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 描述
     */
    private String desc;

    /**
     * uid
     */
    private Integer uid;

    /**
     * 排序
     */
    private Integer index;

}
