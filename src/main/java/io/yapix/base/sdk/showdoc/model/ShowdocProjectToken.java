package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 项目开放平台token
 */
public class ShowdocProjectToken {

    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("api_token")
    private String apiToken;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
