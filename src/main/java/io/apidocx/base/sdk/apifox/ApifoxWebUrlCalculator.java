package io.apidocx.base.sdk.apifox;

import lombok.experimental.UtilityClass;

/**
 * 页面路径计算工具
 */
@UtilityClass
public class ApifoxWebUrlCalculator {

    public String projectUrl(String url, Long projectId) {
        return url + "/web/project/" + projectId;
    }

    public String apiUrl(String url, Long projectId, Long apiId) {
        return projectUrl(url, projectId) + "/apis/api-" + apiId;
    }
}
