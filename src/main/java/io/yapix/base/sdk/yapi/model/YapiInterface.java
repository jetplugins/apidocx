package io.yapix.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * 接口详细信息
 */
public class YapiInterface implements Serializable {

    private Integer id;

    /**
     项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 请求方式
     */
    private String method = "POST";

    /**
     * 路径
     */
    private String path;

    /**
     * 请求路径参数
     */
    @SerializedName("req_params")
    private List<YapiParameter> reqParams;

    /**
     * 菜单名称
     */
    private String menu;

    /**
     * tag
     */
    private List<String> tag;

    /**
     * 项目 token  唯一标识
     */
    private String token;

    /**
     * 请求参数
     */
    @SerializedName("req_query")
    private List<YapiParameter> reqQuery;

    /**
     * header
     */
    @SerializedName("req_headers")
    private List<YapiParameter> reqHeaders;

    /**
     * 请求参数 form 类型
     */
    @SerializedName("req_body_form")
    private List<YapiParameter> reqBodyForm;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类id
     */
    private String catid;

    /**
     * 请求数据类型   raw,form,json
     */
    @SerializedName("req_body_type")
    private String reqBodyType = "json";

    /**
     * 请求数据body
     */
    @SerializedName("req_body_other")
    private String reqBodyOther;

    /**
     * 请求参数body 是否为json_schema
     */
    @SerializedName("req_body_is_json_schema")
    private boolean reqBodyIsJsonSchema;


    /**
     * 状态 undone,默认done
     */
    private String status = "undone";

    /**
     * 返回参数类型  json
     */
    @SerializedName("res_body_type")
    private String resBodyType = "json";

    /**
     * 返回参数
     */
    @SerializedName("res_body")
    private String resBody;

    /**
     * 返回参数是否为json_schema
     */
    @SerializedName("res_body_is_json_schema")
    private boolean resBodyIsJsonSchema = true;

    /**
     * 创建的用户名
     */
    @SerializedName("edit_uid")
    private Integer editUid = 11;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 邮件开关
     */
    @SerializedName("switch_notice")
    private boolean switchNotice;

    private String message = " ";

    /**
     * 文档描述
     */
    private String desc;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<YapiParameter> getReqParams() {
        return reqParams;
    }

    public void setReqParams(List<YapiParameter> reqParams) {
        this.reqParams = reqParams;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<YapiParameter> getReqQuery() {
        return reqQuery;
    }

    public void setReqQuery(List<YapiParameter> reqQuery) {
        this.reqQuery = reqQuery;
    }

    public List<YapiParameter> getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(List<YapiParameter> reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public List<YapiParameter> getReqBodyForm() {
        return reqBodyForm;
    }

    public void setReqBodyForm(List<YapiParameter> reqBodyForm) {
        this.reqBodyForm = reqBodyForm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getReqBodyType() {
        return reqBodyType;
    }

    public void setReqBodyType(String reqBodyType) {
        this.reqBodyType = reqBodyType;
    }

    public String getReqBodyOther() {
        return reqBodyOther;
    }

    public void setReqBodyOther(String reqBodyOther) {
        this.reqBodyOther = reqBodyOther;
    }

    public boolean isReqBodyIsJsonSchema() {
        return reqBodyIsJsonSchema;
    }

    public void setReqBodyIsJsonSchema(boolean reqBodyIsJsonSchema) {
        this.reqBodyIsJsonSchema = reqBodyIsJsonSchema;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResBodyType() {
        return resBodyType;
    }

    public void setResBodyType(String resBodyType) {
        this.resBodyType = resBodyType;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }

    public boolean isResBodyIsJsonSchema() {
        return resBodyIsJsonSchema;
    }

    public void setResBodyIsJsonSchema(boolean resBodyIsJsonSchema) {
        this.resBodyIsJsonSchema = resBodyIsJsonSchema;
    }

    public Integer getEditUid() {
        return editUid;
    }

    public void setEditUid(Integer editUid) {
        this.editUid = editUid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSwitchNotice() {
        return switchNotice;
    }

    public void setSwitchNotice(boolean switchNotice) {
        this.switchNotice = switchNotice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
