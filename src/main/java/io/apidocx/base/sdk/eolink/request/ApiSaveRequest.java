package io.apidocx.base.sdk.eolink.request;

import com.google.gson.JsonObject;
import io.apidocx.base.sdk.eolink.model.ApiBase;
import io.apidocx.base.sdk.eolink.model.ApiHeaderProperty;
import io.apidocx.base.sdk.eolink.model.ApiProperty;
import io.apidocx.base.sdk.eolink.model.ApiResponseItem;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiSaveRequest extends ApiBase {

    /**
     * 项目标识
     */
    private String projectHashKey;

    /**
     * 空间标识
     */
    private String spaceKey;


    /**
     * 请求头参数
     */
    private List<ApiHeaderProperty> apiHeader;

    /**
     * 请求查询参数
     */
    private List<ApiProperty> apiUrlParam;

    /** 请求路径参数 */
    private List<ApiProperty> apiRestfulParam;

    /** 请求体参数 */
    private List<ApiProperty> apiRequestParam;

    /**
     * 响应数据
     */
    private List<ApiResponseItem> apiResultParam;

    /** 响应头信息 */
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
