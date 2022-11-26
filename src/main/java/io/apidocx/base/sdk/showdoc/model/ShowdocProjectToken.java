package io.apidocx.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 项目开放平台token
 */
@Data
public class ShowdocProjectToken {

    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("api_token")
    private String apiToken;

}
