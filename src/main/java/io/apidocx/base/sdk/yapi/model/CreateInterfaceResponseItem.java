package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreateInterfaceResponseItem {

    @SerializedName("_id")
    private Integer id;

    @SerializedName("res_body")
    private String resBody;
}
