package io.yapix.base.sdk.showdoc.model;


public class ShowdocTestResult {

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
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
