package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.model.EolinkerApiGroup;
import java.util.List;

public class GroupListResponse extends Response {

    /** 分组列表 */
    private List<EolinkerApiGroup> apiGroupData;

    public List<EolinkerApiGroup> getApiGroupData() {
        return apiGroupData;
    }

    public void setApiGroupData(List<EolinkerApiGroup> apiGroupData) {
        this.apiGroupData = apiGroupData;
    }
}
