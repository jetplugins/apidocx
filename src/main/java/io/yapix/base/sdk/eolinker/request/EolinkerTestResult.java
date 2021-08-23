package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;

public class EolinkerTestResult {

    private Code code;
    private HttpSession authSession;

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public HttpSession getAuthSession() {
        return authSession;
    }

    public void setAuthSession(HttpSession authSession) {
        this.authSession = authSession;
    }

    public enum Code {
        OK,
        AUTH_ERROR,
        NETWORK_ERROR
    }
}
