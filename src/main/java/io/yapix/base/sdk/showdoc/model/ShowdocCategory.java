package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 分类
 */
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

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getsNumber() {
        return sNumber;
    }

    public void setsNumber(String sNumber) {
        this.sNumber = sNumber;
    }

    public String getParentCatId() {
        return parentCatId;
    }

    public void setParentCatId(String parentCatId) {
        this.parentCatId = parentCatId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<ShowdocCategory> getSub() {
        return sub;
    }

    public void setSub(List<ShowdocCategory> sub) {
        this.sub = sub;
    }
}
