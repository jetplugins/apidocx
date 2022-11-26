package io.apidocx.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 开发api更新文档请求参数
 */
@Data
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

}
