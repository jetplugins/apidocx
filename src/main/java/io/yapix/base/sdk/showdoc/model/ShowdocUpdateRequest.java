package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 开发api更新文档请求参数
 */
public class ShowdocUpdateRequest {

    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("api_token")
    private String apiToken;

    @SerializedName("cat_name")
    private String catName;

    @SerializedName("page_title")
    private String pageTitle;

    @SerializedName("page_content")
    private String pageContent;

    @SerializedName("s_number")
    private Integer sNumber;

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

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public Integer getsNumber() {
        return sNumber;
    }

    public void setsNumber(Integer sNumber) {
        this.sNumber = sNumber;
    }
}
