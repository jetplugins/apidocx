package io.yapix.base.sdk.rap2.model;

import java.util.Date;
import java.util.List;

/**
 * 模块
 */
public class Rap2Module {

    /** 主键 */
    private Long id;
    /** 名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 排序 */
    private Long priority;
    /** 创建人id */
    private Long creatorId;
    /** 仓库id */
    private Long repositoryId;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;

    private List<Rap2InterfaceBase> interfaces;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
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

    public List<Rap2InterfaceBase> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Rap2InterfaceBase> interfaces) {
        this.interfaces = interfaces;
    }
}
