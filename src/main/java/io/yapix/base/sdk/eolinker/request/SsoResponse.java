package io.yapix.base.sdk.eolinker.request;

import lombok.Data;

@Data
public class SsoResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public boolean isOk() {
        return Integer.valueOf(0).equals(code);
    }
}
