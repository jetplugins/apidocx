package io.apidocx.base.sdk.showdoc.model;


import lombok.Data;

@Data
public class TestResult {

    private Code code;
    private String message;
    private String cookies;

    public enum Code {
        OK,
        AUTH_ERROR,
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
