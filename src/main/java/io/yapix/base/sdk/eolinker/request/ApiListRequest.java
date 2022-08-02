package io.yapix.base.sdk.eolinker.request;

import lombok.Data;

@Data
public class ApiListRequest {

    /**
     * 分组ID
     */
    private Long groupID;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 1000;

    /** 项目标识 */
    private String projectHashKey;

    /** 空间标识 */
    private String spaceKey;

}
