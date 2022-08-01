package io.yapix.base.sdk.eolinker.request;

import com.google.gson.JsonObject;
import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import io.yapix.base.sdk.eolinker.model.EolinkerHeaderProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerResponseItem;
import java.util.List;

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

    public String getProjectHashKey() {
        return projectHashKey;
    }

    public void setProjectHashKey(String projectHashKey) {
        this.projectHashKey = projectHashKey;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public List<EolinkerHeaderProperty> getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(List<EolinkerHeaderProperty> apiHeader) {
        this.apiHeader = apiHeader;
    }

    public List<EolinkerProperty> getApiUrlParam() {
        return apiUrlParam;
    }

    public void setApiUrlParam(List<EolinkerProperty> apiUrlParam) {
        this.apiUrlParam = apiUrlParam;
    }

    public List<EolinkerProperty> getApiRestfulParam() {
        return apiRestfulParam;
    }

    public void setApiRestfulParam(List<EolinkerProperty> apiRestfulParam) {
        this.apiRestfulParam = apiRestfulParam;
    }

    public List<EolinkerProperty> getApiRequestParam() {
        return apiRequestParam;
    }

    public void setApiRequestParam(List<EolinkerProperty> apiRequestParam) {
        this.apiRequestParam = apiRequestParam;
    }

    public List<EolinkerResponseItem> getApiResultParam() {
        return apiResultParam;
    }

    public void setApiResultParam(List<EolinkerResponseItem> apiResultParam) {
        this.apiResultParam = apiResultParam;
    }

    public List<EolinkerHeaderProperty> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(List<EolinkerHeaderProperty> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Integer getResultParamType() {
        return resultParamType;
    }

    public void setResultParamType(Integer resultParamType) {
        this.resultParamType = resultParamType;
    }

    public Integer getResultParamJsonType() {
        return resultParamJsonType;
    }

    public void setResultParamJsonType(Integer resultParamJsonType) {
        this.resultParamJsonType = resultParamJsonType;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public JsonObject getRequestParamSetting() {
        return requestParamSetting;
    }

    public void setRequestParamSetting(JsonObject requestParamSetting) {
        this.requestParamSetting = requestParamSetting;
    }

    public JsonObject getResultParamSetting() {
        return resultParamSetting;
    }

    public void setResultParamSetting(JsonObject resultParamSetting) {
        this.resultParamSetting = resultParamSetting;
    }

    public JsonObject getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(JsonObject customInfo) {
        this.customInfo = customInfo;
    }

    public String getSoapVersion() {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion) {
        this.soapVersion = soapVersion;
    }

    public List<Long> getTagID() {
        return tagID;
    }

    public void setTagID(List<Long> tagID) {
        this.tagID = tagID;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public List<JsonObject> getFileList() {
        return fileList;
    }

    public void setFileList(List<JsonObject> fileList) {
        this.fileList = fileList;
    }
}
