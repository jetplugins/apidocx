package io.yapix.base.sdk.showdoc.model;


import lombok.Data;

@Data
public class ShowdocTestResult {

    private Code code;
    private String message;
    private AuthCookies authCookies;

    public enum Code {
        OK,
        AUTH_ERROR,
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
