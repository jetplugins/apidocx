package io.yapix.base.sdk.apifox;

public class ApifoxWebUrlCalculator {

    private final String url;

    public ApifoxWebUrlCalculator(String url) {
        this.url = url;
    }

    public String projectUrl(Long projectId) {
        return this.url + "/web/project/" + projectId;
    }

    public String apiUrl(Long projectId, Long apiId) {
        return projectUrl(projectId) + "/apis/api-" + apiId;
    }
}
