package io.apidocx.base.sdk.apifox;

import lombok.Data;

/**
 * 响应参数
 */
@Data
public class Response<T> {

    /**
     * 是否请求成功
     */
    private boolean success;

    /**
     * 数据
     */
    private T data;

    /**
     * 错误信息
     */
    private Error error;

    private String errorCode;
    private String errorMessage;


    @Data
    public static class Error {
        private Integer code;
        private String message;
    }

    public String getErrorCode() {
        return this.error != null ? this.error.getCode() + "" : this.errorCode;
    }

    public String getErrorMessage() {
        return this.error != null ? this.error.getMessage() : this.errorMessage;
    }
}
