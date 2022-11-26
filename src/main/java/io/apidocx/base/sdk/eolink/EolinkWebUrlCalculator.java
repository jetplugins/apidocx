package io.apidocx.base.sdk.eolink;

public class EolinkWebUrlCalculator {

    private final String url;

    public EolinkWebUrlCalculator(String url) {
        this.url = url;
    }

    /**
     * 计算页面接口列表地址
     */
    public String calculateApiListUrl(String projectHashKey, Long groupId) {
        String query = String.format("?projectHashKey=%s&groupID=%d", projectHashKey, groupId);
        return url + EolinkConstants.PageApiList + query;
    }
}
