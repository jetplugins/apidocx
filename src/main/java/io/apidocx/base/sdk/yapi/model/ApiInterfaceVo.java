package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

/**
 * 接口描述信息
 */
@Data
public class ApiInterfaceVo {

    @SerializedName("_id")
    private int id;

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("catid")
    private int catid;

    @SerializedName("title")
    private String title;

    @SerializedName("method")
    private String method;

    @SerializedName("path")
    private String path;

    @SerializedName("tag")
    private List<?> tag;

    @SerializedName("edit_uid")
    private int editUid;

    @SerializedName("status")
    private String status;

    @SerializedName("api_opened")
    private boolean apiOpened;

    @SerializedName("index")
    private int index;

    @SerializedName("uid")
    private int uid;

    @SerializedName("add_time")
    private int addTime;

    @SerializedName("up_time")
    private int upTime;

    //----------------generated----------------//

}
