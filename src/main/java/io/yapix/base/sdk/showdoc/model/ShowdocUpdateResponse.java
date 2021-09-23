package io.yapix.base.sdk.showdoc.model;

import com.google.gson.annotations.SerializedName;

/**
 * 开发api更新文档响应参数
 */
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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageComments() {
        return pageComments;
    }

    public void setPageComments(String pageComments) {
        this.pageComments = pageComments;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public String getsNumber() {
        return sNumber;
    }

    public void setsNumber(String sNumber) {
        this.sNumber = sNumber;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
