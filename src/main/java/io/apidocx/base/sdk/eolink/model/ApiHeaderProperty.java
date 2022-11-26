package io.apidocx.base.sdk.eolink.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 请求头参数
 */
@Data
public class ApiHeaderProperty {

    /**
     * 请求头名称
     */
    private String headerName;

    private String headerValue;

    /**
     * 是否必填, 1: 必填, 0:非必填
     */
    private String paramNotNull;

    /**
     * 默认值，此处无用
     */
    @SerializedName("default")
    private int defaultValue;

    /** 说明 */
    private String paramName;

}
