package io.yapix.base.sdk.rap2.request;


import io.yapix.base.sdk.rap2.model.AuthCookies;

public class Rap2TestResult {

    private Code code;
    private AuthCookies authCookies;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
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
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
