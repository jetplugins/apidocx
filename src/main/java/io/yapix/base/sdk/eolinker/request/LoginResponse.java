package io.yapix.base.sdk.eolinker.request;

public class LoginResponse {

    private Integer isFirstTimeLogin;
    private String spaceKey;

    public Integer getIsFirstTimeLogin() {
        return isFirstTimeLogin;
    }

    public void setIsFirstTimeLogin(Integer isFirstTimeLogin) {
        this.isFirstTimeLogin = isFirstTimeLogin;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}
