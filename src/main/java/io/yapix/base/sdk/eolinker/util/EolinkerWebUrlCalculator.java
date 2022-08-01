package io.yapix.base.sdk.eolinker.util;

import io.yapix.base.sdk.eolinker.EolinkerConstants;

public class EolinkerWebUrlCalculator {

    private final String url;

    public EolinkerWebUrlCalculator(String url) {
        this.url = url;
    }

    /**
     * 计算页面接口列表地址
     */
    public String calculateApiListUrl(String projectHashKey, Long groupId) {
        String query = String.format("?projectHashKey=%s&groupID=%d", projectHashKey, groupId);
        return url + EolinkerConstants.PageApiList + query;
    }
}
