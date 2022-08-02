package io.yapix.base.sdk.rap2.request;


import io.yapix.base.sdk.rap2.model.AuthCookies;
import io.yapix.base.sdk.rap2.model.Rap2User;
import lombok.Data;

@Data
public class Rap2TestResult {

    private Code code;
    private String message;
    private AuthCookies authCookies;
    private Rap2User authUser;

    public enum Code {
        OK,
        AUTH_ERROR,
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
