package io.yapix.base.sdk.eolinker.request;

public class ApiListRequest {

    /** 分组ID */
    private Long groupID;

    /** 页码 */
    private Integer page = 1;

    /** 每页大小 */
    private Integer pageSize = 1000;

    /** 项目标识 */
    private String projectHashKey;

    /** 空间标识 */
    private String spaceKey;

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getProjectHashKey() {
        return projectHashKey;
    }

    public void setProjectHashKey(String projectHashKey) {
        this.projectHashKey = projectHashKey;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}
