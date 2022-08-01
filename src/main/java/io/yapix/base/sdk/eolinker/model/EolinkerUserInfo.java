package io.yapix.base.sdk.eolinker.model;

import java.util.Objects;

public class EolinkerUserInfo {

    private String userMail;
    private String spaceKey;

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EolinkerUserInfo that = (EolinkerUserInfo) o;
        return Objects.equals(userMail, that.userMail) && Objects.equals(spaceKey, that.spaceKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userMail, spaceKey);
    }
}
