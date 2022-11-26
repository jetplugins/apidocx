package io.apidocx.base.sdk.rap2.util;

public class Rap2WebUrlCalculator {

    private String url;

    public Rap2WebUrlCalculator(String url) {
        this.url = url;
    }

    /**
     * 计算仓库编辑地址
     */
    public String calculateEditorUrl(long repositoryId, Long moduleId, Long interfaceId) {
        StringBuilder sb = new StringBuilder(url).append("/repository/editor?id=").append(repositoryId);
        if (moduleId != null) {
            sb.append("&mod=").append(moduleId);
        }
        if (interfaceId != null) {
            sb.append("&itf=").append(interfaceId);
        }
        return sb.toString();
    }
}
