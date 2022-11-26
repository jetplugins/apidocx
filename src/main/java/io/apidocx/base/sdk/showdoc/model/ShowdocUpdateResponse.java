package io.apidocx.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 开发api更新文档响应参数
 */
@Data
public class ShowdocUpdateResponse {

    @SerializedName("page_id")
    private String pageId;

    @SerializedName("author_uid")
    private String authorUid;

    @SerializedName("author_username")
    private String authorUsername;

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("cat_id")
    private String catId;

    @SerializedName("page_title")
    private String pageTitle;

    @SerializedName("page_comments")
    private String pageComments;

    @SerializedName("page_content")
    private String pageContent;

    @SerializedName("s_number")
    private String sNumber;

    private String addtime;

}
