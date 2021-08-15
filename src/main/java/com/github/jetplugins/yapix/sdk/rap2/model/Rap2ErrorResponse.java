package com.github.jetplugins.yapix.sdk.rap2.model;

public class Rap2ErrorResponse {

    private Boolean isOk;

    private String errMsg;

    public Boolean getOk() {
        return isOk;
    }

    public void setOk(Boolean ok) {
        isOk = ok;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
