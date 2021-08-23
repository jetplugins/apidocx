package io.yapix.base.sdk.eolinker.model;

import java.util.Date;

/**
 * 接口基础信息
 */
public class EolinkerApiBase {

    /** 接口id */
    private Long apiID;

    /** 分组id */
    private Long groupID;

    /** 名称 */
    private String apiName;

    /** 请求方式 */
    private Integer apiRequestType;

    /** 路径 */
    private String apiURI;

    private Integer apiProtocol;

    /** 接口状态 */
    private Integer apiStatus;

    /** 标签信息, 多个逗号分割 */
    private String apiTag;

    /** 请求参数类型 */
    private Integer apiRequestParamType;

    /** 请求参数类型是raw的说明 */
    private String apiRequestRaw;

    /** 请求参数类型是binary的说明 */
    private String apiRequestBinary;

    /** 请求参数类型是json的子类型：对象，还是数组 */
    private Integer apiRequestParamJsonType;

    private String advancedSetting;

    /** 前置脚本 */
    private String beforeInject;

    /** 后置脚本 */
    private String afterInject;

    //-------------------------不那么重要的------------------------//

    private String sampleURI;

    /** Mock标识 */
    private String mockCode;

    /** 分组路径Path值 */
    private String groupPath;

    /**
     * 分组名称
     */
    private String groupName;

    /** 成功mock */
    private String apiSuccessStatusCode;
    private String apiSuccessContentType;
    private String apiSuccessMock;

    /** 失败mock */
    private String apiFailureStatusCode;
    private String apiFailureContentType;
    private String apiFailureMock;

    private Integer starred;

    private Integer removed;

    /** 额外说明类型 */
    private Integer apiNoteType;

    /** 额外说明文本值 */
    private String apiNoteRaw;

    /** 额外说明原值 */
    private String apiNote;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date apiUpdateTime;

    /** 创建人姓名 */
    private String creator;

    /** 更新人姓名 */
    private String updater;

    /** 接口管理人姓名 */
    private String apiManager;

    /** 接口管理人id */
    private Long apiManagerConnID;

    public Long getApiID() {
        return apiID;
    }

    public void setApiID(Long apiID) {
        this.apiID = apiID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getApiRequestType() {
        return apiRequestType;
    }

    public void setApiRequestType(Integer apiRequestType) {
        this.apiRequestType = apiRequestType;
    }

    public String getApiURI() {
        return apiURI;
    }

    public void setApiURI(String apiURI) {
        this.apiURI = apiURI;
    }

    public Integer getApiProtocol() {
        return apiProtocol;
    }

    public void setApiProtocol(Integer apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public Integer getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(Integer apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getApiTag() {
        return apiTag;
    }

    public void setApiTag(String apiTag) {
        this.apiTag = apiTag;
    }

    public Integer getApiRequestParamType() {
        return apiRequestParamType;
    }

    public void setApiRequestParamType(Integer apiRequestParamType) {
        this.apiRequestParamType = apiRequestParamType;
    }

    public String getApiRequestRaw() {
        return apiRequestRaw;
    }

    public void setApiRequestRaw(String apiRequestRaw) {
        this.apiRequestRaw = apiRequestRaw;
    }

    public String getApiRequestBinary() {
        return apiRequestBinary;
    }

    public void setApiRequestBinary(String apiRequestBinary) {
        this.apiRequestBinary = apiRequestBinary;
    }

    public Integer getApiRequestParamJsonType() {
        return apiRequestParamJsonType;
    }

    public void setApiRequestParamJsonType(Integer apiRequestParamJsonType) {
        this.apiRequestParamJsonType = apiRequestParamJsonType;
    }

    public String getAdvancedSetting() {
        return advancedSetting;
    }

    public void setAdvancedSetting(String advancedSetting) {
        this.advancedSetting = advancedSetting;
    }

    public String getBeforeInject() {
        return beforeInject;
    }

    public void setBeforeInject(String beforeInject) {
        this.beforeInject = beforeInject;
    }

    public String getAfterInject() {
        return afterInject;
    }

    public void setAfterInject(String afterInject) {
        this.afterInject = afterInject;
    }

    public String getSampleURI() {
        return sampleURI;
    }

    public void setSampleURI(String sampleURI) {
        this.sampleURI = sampleURI;
    }

    public String getMockCode() {
        return mockCode;
    }

    public void setMockCode(String mockCode) {
        this.mockCode = mockCode;
    }

    public String getGroupPath() {
        return groupPath;
    }

    public void setGroupPath(String groupPath) {
        this.groupPath = groupPath;
    }

    public String getApiSuccessStatusCode() {
        return apiSuccessStatusCode;
    }

    public void setApiSuccessStatusCode(String apiSuccessStatusCode) {
        this.apiSuccessStatusCode = apiSuccessStatusCode;
    }

    public String getApiSuccessContentType() {
        return apiSuccessContentType;
    }

    public void setApiSuccessContentType(String apiSuccessContentType) {
        this.apiSuccessContentType = apiSuccessContentType;
    }

    public String getApiSuccessMock() {
        return apiSuccessMock;
    }

    public void setApiSuccessMock(String apiSuccessMock) {
        this.apiSuccessMock = apiSuccessMock;
    }

    public String getApiFailureStatusCode() {
        return apiFailureStatusCode;
    }

    public void setApiFailureStatusCode(String apiFailureStatusCode) {
        this.apiFailureStatusCode = apiFailureStatusCode;
    }

    public String getApiFailureContentType() {
        return apiFailureContentType;
    }

    public void setApiFailureContentType(String apiFailureContentType) {
        this.apiFailureContentType = apiFailureContentType;
    }

    public String getApiFailureMock() {
        return apiFailureMock;
    }

    public void setApiFailureMock(String apiFailureMock) {
        this.apiFailureMock = apiFailureMock;
    }

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public Integer getRemoved() {
        return removed;
    }

    public void setRemoved(Integer removed) {
        this.removed = removed;
    }

    public Integer getApiNoteType() {
        return apiNoteType;
    }

    public void setApiNoteType(Integer apiNoteType) {
        this.apiNoteType = apiNoteType;
    }

    public String getApiNoteRaw() {
        return apiNoteRaw;
    }

    public void setApiNoteRaw(String apiNoteRaw) {
        this.apiNoteRaw = apiNoteRaw;
    }

    public String getApiNote() {
        return apiNote;
    }

    public void setApiNote(String apiNote) {
        this.apiNote = apiNote;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getApiUpdateTime() {
        return apiUpdateTime;
    }

    public void setApiUpdateTime(Date apiUpdateTime) {
        this.apiUpdateTime = apiUpdateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getApiManager() {
        return apiManager;
    }

    public void setApiManager(String apiManager) {
        this.apiManager = apiManager;
    }

    public Long getApiManagerConnID() {
        return apiManagerConnID;
    }

    public void setApiManagerConnID(Long apiManagerConnID) {
        this.apiManagerConnID = apiManagerConnID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
