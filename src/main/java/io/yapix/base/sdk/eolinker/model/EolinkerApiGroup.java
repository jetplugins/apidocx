package io.yapix.base.sdk.eolinker.model;

/**
 * 接口分组信息
 */
public class EolinkerApiGroup {

    private Long groupID;
    private Long parentGroupID;
    private String groupName;
    private Integer groupDepth;

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setParentGroupID(long parentGroupID) {
        this.parentGroupID = parentGroupID;
    }

    public long getParentGroupID() {
        return parentGroupID;
    }

    public void setGroupDepth(int groupDepth) {
        this.groupDepth = groupDepth;
    }

    public int getGroupDepth() {
        return groupDepth;
    }

}
