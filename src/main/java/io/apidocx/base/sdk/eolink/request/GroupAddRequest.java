package io.apidocx.base.sdk.eolink.request;

import lombok.Data;

@Data
public class GroupAddRequest {

    /**
     * 分组名
     */
    private String groupName;

    /**
     * 项目标识
     */
    private String projectHashKey;

    /**
     * 父分组标识
     */
    private String parentGroupID;

    /** 空间标识 */
    private String spaceKey;

    /** 模块 */
    private Long module = 2L;

}
