package io.yapix.base.sdk.eolinker.request;

import com.google.gson.JsonObject;
import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import io.yapix.base.sdk.eolinker.model.EolinkerHeaderProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerResponseItem;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiSaveRequest extends EolinkerApiBase {

    /** 项目标识 */
    private String projectHashKey;

    /** 空间标识 */
    private String spaceKey;


    /** 请求头参数 */
    private List<EolinkerHeaderProperty> apiHeader;

    /** 请求查询参数 */
    private List<EolinkerProperty> apiUrlParam;

    /** 请求路径参数 */
    private List<EolinkerProperty> apiRestfulParam;

    /** 请求体参数 */
    private List<EolinkerProperty> apiRequestParam;

    /** 响应数据 */
    private List<EolinkerResponseItem> apiResultParam;

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
