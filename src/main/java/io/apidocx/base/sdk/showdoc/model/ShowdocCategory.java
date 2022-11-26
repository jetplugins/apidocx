package io.apidocx.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

/**
 * 分类
 */
@Data
public class ShowdocCategory {

    @SerializedName("cat_id")
    private String catId;

    @SerializedName("cat_name")
    private String catName;

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("s_number")
    private String sNumber;

    @SerializedName("parent_cat_id")
    private String parentCatId;

    private String level;

    private List<ShowdocCategory> sub;

}
