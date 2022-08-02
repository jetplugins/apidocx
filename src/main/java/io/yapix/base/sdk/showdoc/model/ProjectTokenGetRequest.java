package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ProjectTokenGetRequest {

    @SerializedName("item_id")
    private String itemId;

}
