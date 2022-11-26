package io.apidocx.base.sdk.eolink.util;

import io.apidocx.base.sdk.eolink.model.ApiBase;
import io.apidocx.base.sdk.eolink.model.ApiInfo;
import io.apidocx.base.sdk.eolink.request.ApiSaveRequest;

public class ApiConverter {

    private ApiConverter() {
    }

    public static ApiSaveRequest convertApiSaveRequest(String projectHashKey, ApiInfo api) {
        ApiSaveRequest data = new ApiSaveRequest();
        data.setProjectHashKey(projectHashKey);
        // 基本信息
        ApiBase baseApi = api.getBaseInfo();
        data.setApiID(baseApi.getApiID());
        data.setGroupID(baseApi.getGroupID());
        data.setApiName(baseApi.getApiName());
        data.setApiRequestType(baseApi.getApiRequestType());
        data.setApiURI(baseApi.getApiURI());
        data.setApiProtocol(baseApi.getApiProtocol());
        data.setApiStatus(baseApi.getApiStatus());
        data.setApiTag(baseApi.getApiTag());
        data.setApiRequestParamType(baseApi.getApiRequestParamType());
        data.setApiRequestRaw(baseApi.getApiRequestRaw());
        data.setApiRequestBinary(baseApi.getApiRequestBinary());
        data.setApiRequestParamJsonType(baseApi.getApiRequestParamJsonType());
        data.setAdvancedSetting(baseApi.getAdvancedSetting());
        data.setBeforeInject(baseApi.getBeforeInject());
        data.setAfterInject(baseApi.getAfterInject());
        data.setSampleURI(baseApi.getSampleURI());
        data.setMockCode(baseApi.getMockCode());
        data.setGroupPath(baseApi.getGroupPath());
        data.setGroupName(baseApi.getGroupName());
        data.setApiSuccessStatusCode(baseApi.getApiSuccessStatusCode());
        data.setApiSuccessContentType(baseApi.getApiSuccessContentType());
        data.setApiSuccessMock(baseApi.getApiSuccessMock());
        data.setApiFailureStatusCode(baseApi.getApiFailureStatusCode());
        data.setApiFailureContentType(baseApi.getApiFailureContentType());
        data.setApiFailureMock(baseApi.getApiFailureMock());
        data.setStarred(baseApi.getStarred());
        data.setRemoved(baseApi.getRemoved());
        data.setApiNoteType(baseApi.getApiNoteType());
        data.setApiNoteRaw(baseApi.getApiNoteRaw());
        data.setApiNote(baseApi.getApiNote());
        data.setCreateTime(baseApi.getCreateTime());
        data.setApiUpdateTime(baseApi.getApiUpdateTime());
        data.setCreator(baseApi.getCreator());
        data.setUpdater(baseApi.getUpdater());
        data.setApiManager(baseApi.getApiManager());
        data.setApiManagerConnID(baseApi.getApiManagerConnID());
        // 详细信息
        data.setApiHeader(api.getHeaderInfo());
        data.setApiUrlParam(api.getUrlParam());
        data.setApiRestfulParam(api.getRestfulParam());
        data.setApiRequestParam(api.getRequestInfo());
        data.setApiResultParam(api.getResultInfo());
        data.setResponseHeader(api.getResponseHeader());
        data.setResultParamType(api.getResultParamType());
        data.setResultParamJsonType(api.getResultParamJsonType());
        data.setFileID(api.getFileID());
        data.setRequestParamSetting(api.getRequestParamSetting());
        data.setResultParamSetting(api.getResultParamSetting());
        data.setCustomInfo(api.getCustomInfo());
        data.setSoapVersion(api.getSoapVersion());
        data.setTagID(api.getTagID());
        data.setApiType(api.getApiType());
        data.setFileList(api.getFileList());
        return data;
    }
}
