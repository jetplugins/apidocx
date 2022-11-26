package io.apidocx.base.sdk.rap2.model;

import java.util.Date;
import lombok.Data;

/**
 * 接口信息
 */
@Data
public class Rap2InterfaceBase {

    /** 接口id */
    private Long id;

    /** 所属模块 */
    private Long moduleId;

    /** 所属模块 */
    private String moduleName;

    /** 所属项目 */
    private Long repositoryId;

    /** 名称 */
    private String name;

    /** 请求地址 */
    private String url;

    /** 请求方式 */
    private String method;

    private String bodyOption;

    /** 描述 */
    private String description;

    /** 优先级 */
    private Long priority;

    /** 状态 */
    private int status;

    /** 创建人 */
    private Long creatorId;

    private String lockerId;

    private Rap2User locker;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    /** 删除时间 */
    private Date deletedAt;

}
