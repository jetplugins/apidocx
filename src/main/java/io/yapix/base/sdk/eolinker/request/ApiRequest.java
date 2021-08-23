package io.yapix.base.sdk.eolinker.request;

public class ApiRequest {

    /** ID */
    private Long apiID;

    /** 项目标识 */
    private String projectHashKey;

    /** 空间标识 */
    private String spaceKey;

    public Long getApiID() {
        return apiID;
    }

    public void setApiID(Long apiID) {
        this.apiID = apiID;
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
