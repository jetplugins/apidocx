package io.yapix.base.sdk.yapi.response;

import io.yapix.base.sdk.yapi.mode.AuthCookies;

public class YapiTestResult {

    private YapiTestValue code;
    private AuthCookies authCookies;

    public YapiTestValue getCode() {
        return code;
    }

    public void setCode(YapiTestValue code) {
        this.code = code;
    }

    public AuthCookies getAuthCookies() {
        return authCookies;
    }

    public void setAuthCookies(AuthCookies authCookies) {
        this.authCookies = authCookies;
    }
}
