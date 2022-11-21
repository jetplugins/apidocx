package io.yapix.base.sdk.apifox.model;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class ApifoxAuthResponse<T> {
    private boolean success;
    private String errorCode;
    private String errorMessage;
    private T data;
}
