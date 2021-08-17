package io.yapix.base.sdk.rap2.request;


import io.yapix.base.sdk.rap2.model.AuthCookies;
import io.yapix.base.sdk.rap2.model.Rap2User;

public class Rap2TestResult {

    private Code code;
    private AuthCookies authCookies;
    private Rap2User authUser;

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

    public Rap2User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(Rap2User authUser) {
        this.authUser = authUser;
    }

    public enum Code {
        OK,
        AUTH_ERROR,
        AUTH_CAPTCHA_ERROR,
        NETWORK_ERROR
    }
}
