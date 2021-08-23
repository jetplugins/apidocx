package io.yapix.base.sdk.eolinker.request;

public class GroupAddRequest {

    /** 分组名 */
    private String groupName;

    /** 项目标识 */
    private String projectHashKey;

    /** 父分组标识 */
    private String parentGroupID;

    /** 空间标识 */
    private String spaceKey;

    /** 模块 */
    private Long module = 2L;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProjectHashKey() {
        return projectHashKey;
    }

    public void setProjectHashKey(String projectHashKey) {
        this.projectHashKey = projectHashKey;
    }

    public String getParentGroupID() {
        return parentGroupID;
    }

    public void setParentGroupID(String parentGroupID) {
        this.parentGroupID = parentGroupID;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public Long getModule() {
        return module;
    }

    public void setModule(Long module) {
        this.module = module;
    }
}
