package io.apidocx.base.sdk.eolink.model;

import lombok.Data;

/**
 * 接口基础信息
 */
@Data
public class ApiBase {

    /**
     * 接口id
     */
    private Long apiID;

    /**
     * 分组id
     */
    private Long groupID;

    /**
     * 名称
     */
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

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String apiUpdateTime;

    /** 创建人姓名 */
    private String creator;

    /** 更新人姓名 */
    private String updater;

    /** 接口管理人姓名 */
    private String apiManager;

    /** 接口管理人id */
    private Long apiManagerConnID;

}
