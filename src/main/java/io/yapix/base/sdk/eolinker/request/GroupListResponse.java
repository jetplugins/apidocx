package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.model.EolinkerApiGroup;
import java.util.List;

public class GroupListResponse extends Response {

    /** 分组列表 */
    private List<EolinkerApiGroup> groupList;

    public List<EolinkerApiGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<EolinkerApiGroup> groupList) {
        this.groupList = groupList;
    }
}
