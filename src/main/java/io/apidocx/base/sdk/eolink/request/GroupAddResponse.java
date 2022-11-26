package io.apidocx.base.sdk.eolink.request;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GroupAddResponse extends Response {

    /** 分组id */
    @SerializedName("groupID")
    private Long groupID;

}
