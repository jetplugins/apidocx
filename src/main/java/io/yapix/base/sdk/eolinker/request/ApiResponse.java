package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;

public class ApiResponse extends Response {

    private EolinkerApiInfo apiInfo;
    private int hasUnreadComment;

    public EolinkerApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(EolinkerApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

    public int getHasUnreadComment() {
        return hasUnreadComment;
    }

    public void setHasUnreadComment(int hasUnreadComment) {
        this.hasUnreadComment = hasUnreadComment;
    }
}
