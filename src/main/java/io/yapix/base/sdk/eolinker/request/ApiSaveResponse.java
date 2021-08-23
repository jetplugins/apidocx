package io.yapix.base.sdk.eolinker.request;

public class ApiSaveResponse extends Response {

    private Long apiID;

    private Long groupID;

    public Long getApiID() {
        return apiID;
    }

    public void setApiID(Long apiID) {
        this.apiID = apiID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }
}
