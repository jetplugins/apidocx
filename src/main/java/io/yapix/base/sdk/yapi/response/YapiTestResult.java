package io.yapix.base.sdk.yapi.response;

import io.yapix.base.sdk.yapi.model.AuthCookies;

public class YapiTestResult {

    private Code code;
    private String message;
    private AuthCookies authCookies;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthCookies getAuthCookies() {
        return authCookies;
    }

    public void setAuthCookies(AuthCookies authCookies) {
        this.authCookies = authCookies;
    }

    public enum Code {
        OK,
        AUTH_ERROR,
        NETWORK_ERROR
    }
}
