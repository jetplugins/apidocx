package com.github.jetplugins.yapix.sdk.rap2.model;

import java.util.Date;

/**
 * 接口信息
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBodyOption() {
        return bodyOption;
    }

    public void setBodyOption(String bodyOption) {
        this.bodyOption = bodyOption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getLockerId() {
        return lockerId;
    }

    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }

    public Rap2User getLocker() {
        return locker;
    }

    public void setLocker(Rap2User locker) {
        this.locker = locker;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
