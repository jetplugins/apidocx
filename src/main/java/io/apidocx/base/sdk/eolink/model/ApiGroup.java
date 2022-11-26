package io.apidocx.base.sdk.eolink.model;

import lombok.Data;

/**
 * 接口分组信息
 */
@Data
public class ApiGroup {

    private Long groupID;
    private Long parentGroupID;
    private String groupName;
    private Integer groupDepth;

}
