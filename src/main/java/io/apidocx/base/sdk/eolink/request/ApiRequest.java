package io.apidocx.base.sdk.eolink.request;

import lombok.Data;

@Data
public class ApiRequest {

    /**
     * ID
     */
    private Long apiID;

    /**
     * 项目标识
     */
    private String projectHashKey;

    /**
     * 空间标识
     */
    private String spaceKey;

}
