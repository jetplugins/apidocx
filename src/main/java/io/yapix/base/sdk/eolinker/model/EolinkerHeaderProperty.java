package io.yapix.base.sdk.eolinker.model;

import com.google.gson.annotations.SerializedName;

/**
 * 请求头参数
 */
public class EolinkerHeaderProperty {

    /** 请求头名称 */
    private String headerName;

    private String headerValue;

    /** 是否必填, 1: 必填, 0:非必填 */
    private String paramNotNull;

    /** 默认值，此处无用 */
    @SerializedName("default")
    private int defaultValue;

    /** 说明 */
    private String paramName;

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setParamNotNull(String paramNotNull) {
        this.paramNotNull = paramNotNull;
    }

    public String getParamNotNull() {
        return paramNotNull;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

}
