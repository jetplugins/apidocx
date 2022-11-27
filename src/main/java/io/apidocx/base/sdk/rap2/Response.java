package io.apidocx.base.sdk.rap2;

import lombok.Data;

/**
 * Rap2通用响应结果
 */
@Data
public class Response<T> {
    /**
     * 状态信息
     */
    private String errMsg;

    /**
     * 返回结果
     */
    private T data;


    public boolean isSuccess() {
        if (errMsg != null && errMsg.isEmpty()) {
            return false;
        }
        return data != null;
    }
}
