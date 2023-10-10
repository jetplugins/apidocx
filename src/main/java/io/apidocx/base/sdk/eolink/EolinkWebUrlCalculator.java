package io.apidocx.base.sdk.eolink;

public class EolinkWebUrlCalculator {

    private final String url;

    public EolinkWebUrlCalculator(String url) {
        this.url = url;
    }

    /**
     * 计算页面接口列表地址
     */
    public String calculateApiListUrl(String spaceKey, String projectHashKey, Long groupId, Long apiId) {
        if (groupId == null) {
            groupId = -1L;
        }
        if (apiId != null) {
            String pageTpl = "/home/api-studio/inside/%s/api/%s/detail/%s?spaceKey=%s";
            String page = String.format(pageTpl, projectHashKey, groupId, apiId, spaceKey);
            return url + page;
        } else {
            String pageTpl = "/home/api-studio/inside/%s/api/%s/list?spaceKey=%s";
            String page = String.format(pageTpl, projectHashKey, groupId, spaceKey);
            return url + page;
        }
    }
}
