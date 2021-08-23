package io.yapix.base.sdk.eolinker.request;

public class GroupListRequest {

    /** 项目标识 */
    private String projectHashKey;

    /** 空间标识 */
    private String spaceKey;

    /** 模块 */
    private Long module = 2L;

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

    public Long getModule() {
        return module;
    }

    public void setModule(Long module) {
        this.module = module;
    }
}
