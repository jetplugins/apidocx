package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.model.EolinkerUserInfo;

public class GetUserInfoResponse extends Response {

    private EolinkerUserInfo userInfo;

    public EolinkerUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(EolinkerUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
