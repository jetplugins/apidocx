package io.yapix.base.sdk.eolinker.request;


import com.google.gson.annotations.SerializedName;

public class GroupAddResponse extends Response {

    /** 分组id */
    @SerializedName("groupID")
    private Long groupID;

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }
}
