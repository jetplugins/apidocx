package io.yapix.base.sdk.eolinker.request;

public class LoginRequest {

    private String loginCall;
    private String loginPassword;
    private String verifyCode;
    private String client = "1";

    public String getLoginCall() {
        return loginCall;
    }

    public void setLoginCall(String loginCall) {
        this.loginCall = loginCall;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
