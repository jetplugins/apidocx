package io.yapix.base.sdk.eolinker.model;

import com.google.gson.JsonObject;
import java.util.List;

/**
 * 接口信息
 */
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

    public EolinkerApiBase getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(EolinkerApiBase baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<EolinkerHeaderProperty> getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(List<EolinkerHeaderProperty> headerInfo) {
        this.headerInfo = headerInfo;
    }

    public List<EolinkerProperty> getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(List<EolinkerProperty> urlParam) {
        this.urlParam = urlParam;
    }

    public List<EolinkerProperty> getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(List<EolinkerProperty> requestInfo) {
        this.requestInfo = requestInfo;
    }

    public List<EolinkerProperty> getRestfulParam() {
        return restfulParam;
    }

    public void setRestfulParam(List<EolinkerProperty> restfulParam) {
        this.restfulParam = restfulParam;
    }

    public List<EolinkerResponseItem> getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(List<EolinkerResponseItem> resultInfo) {
        this.resultInfo = resultInfo;
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
