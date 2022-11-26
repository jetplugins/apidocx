package io.apidocx.base.sdk.rap2.dto;

import lombok.Data;

/**
 * 接口更新请求参数
 */
@Data
public class InterfaceUpdateRequest {

    /**
     * 接口id
     */
    private Long id;

    /** 名称 */
    private String name;

    /** 请求地址 */
    private String url;

    /** 请求方式 */
    private String method;

    /** 描述 */
    private String description;

    /** 状态 */
    private int status;

}
