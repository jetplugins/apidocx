package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;

public class ProjectTokenGetRequest {

    @SerializedName("item_id")
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
