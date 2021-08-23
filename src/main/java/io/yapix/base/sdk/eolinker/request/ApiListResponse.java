package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import java.util.List;

public class ApiListResponse extends Response {

    private Integer itemNum;

    private List<EolinkerApiBase> apiList;

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setApiList(List<EolinkerApiBase> apiList) {
        this.apiList = apiList;
    }

    public List<EolinkerApiBase> getApiList() {
        return apiList;
    }

}
