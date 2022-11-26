package io.apidocx.base.sdk.yapi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * Yapi通用响应结果
 */
@Data
public class Response<T> {

    /**
     * 状态码
     */
    @SerializedName("errcode")
    private Integer errorCode;
    /**
     * 状态信息
     */
    @SerializedName("errmsg")
    private String errorMessage;
    /**
     * 返回结果
     */
    private T data;
    /**
     * 分类
     */
    private String catId;

    public boolean isSuccess() {
        return errorCode != null && errorCode == 0;
    }
}
