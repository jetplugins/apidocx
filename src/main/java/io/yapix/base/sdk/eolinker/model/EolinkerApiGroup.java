package io.yapix.base.sdk.eolinker.model;

import lombok.Data;

/**
 * 接口分组信息
 */
@Data
public class EolinkerApiGroup {

    private Long groupID;
    private Long parentGroupID;
    private String groupName;
    private Integer groupDepth;

}
