package io.apidocx.base.sdk.eolink.model;

import com.google.gson.JsonObject;
import java.util.List;
import lombok.Data;

/**
 * 接口信息
 */
@Data
public class ApiInfo {

    /**
     * 基础信息
     */
    private ApiBase baseInfo;

    /**
     * 请求头参数
     */
    private List<ApiHeaderProperty> headerInfo;

    /**
     * 请求查询参数
     */
    private List<ApiProperty> urlParam;

    /**
     * 请求路径参数
     */
    private List<ApiProperty> restfulParam;

    /** 请求体参数 */
    private List<ApiProperty> requestInfo;

    /** 响应数据 */
    private List<ApiResponseItem> resultInfo;

    /**
     * 响应头信息
     */
    private List<ApiHeaderProperty> responseHeader;

    /** 响应参数类型 */
    private Integer resultParamType;

    /** 响应参数Json类型 */
    private Integer resultParamJsonType;

    private String fileID;
    private JsonObject requestParamSetting;
    private JsonObject resultParamSetting;

    /** 自定义信息 */
    private JsonObject customInfo;

    private String soapVersion;

    /** 标签 */
    private List<Long> tagID;

    /** http还是https */
    private String apiType;

    /** 附件列表 */
    private List<JsonObject> fileList;

}
