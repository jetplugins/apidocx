package io.yapix.base.sdk.eolinker.model;

import com.google.gson.JsonObject;
import java.util.List;
import lombok.Data;

/**
 * 接口信息
 */
@Data
public class EolinkerApiInfo {

    /** 基础信息 */
    private EolinkerApiBase baseInfo;

    /** 请求头参数 */
    private List<EolinkerHeaderProperty> headerInfo;

    /** 请求查询参数 */
    private List<EolinkerProperty> urlParam;

    /** 请求路径参数 */
    private List<EolinkerProperty> restfulParam;

    /** 请求体参数 */
    private List<EolinkerProperty> requestInfo;

    /** 响应数据 */
    private List<EolinkerResponseItem> resultInfo;

    /** 响应头信息 */
    private List<EolinkerHeaderProperty> responseHeader;

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
