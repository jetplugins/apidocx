package io.apidocx.base.sdk.eolink.request;

import lombok.Data;

@Data
public class SsoResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return Integer.valueOf(0).equals(code);
    }
}
