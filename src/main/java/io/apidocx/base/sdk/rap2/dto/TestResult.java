package io.apidocx.base.sdk.rap2.dto;


import io.apidocx.base.sdk.rap2.model.Rap2User;
import lombok.Data;

@Data
public class TestResult {

    private Code code;
    private String message;
    private String cookies;
    private Rap2User authUser;

    public enum Code {
        OK,
        AUTH_ERROR,
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
