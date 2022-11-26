package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

/**
 * 接口详细信息
 */
@Data
public class ApiInterface {

    private Integer id;

    /**
     * 项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 路径
     */
    private String path;

    /**
     * 请求路径参数
     */
    @SerializedName("req_params")
    private List<ApiParameter> reqParams;

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
    private List<ApiParameter> reqQuery;

    /**
     * header
     */
    @SerializedName("req_headers")
    private List<ApiParameter> reqHeaders;

    /**
     * 请求参数 form 类型
     */
    @SerializedName("req_body_form")
    private List<ApiParameter> reqBodyForm;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类id
     */
    private Integer catid;

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


}
