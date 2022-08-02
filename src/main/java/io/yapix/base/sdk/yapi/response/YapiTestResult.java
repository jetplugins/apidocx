package io.yapix.base.sdk.yapi.response;

import io.yapix.base.sdk.yapi.model.AuthCookies;
import lombok.Data;

@Data
public class YapiTestResult {

    private Code code;
    private String message;
    private AuthCookies authCookies;

    public enum Code {
        OK,
        AUTH_ERROR,
        NETWORK_ERROR
    }
}
