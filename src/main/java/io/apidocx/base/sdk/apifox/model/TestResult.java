package io.apidocx.base.sdk.apifox.model;

import lombok.Data;

@Data
public class TestResult {

    private Code code;
    private String message;
    private String accessToken;

    public enum Code {
        OK,
        AUTH_ERROR,
        NETWORK_ERROR
    }
}
