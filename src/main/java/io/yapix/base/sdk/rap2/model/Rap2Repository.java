package io.yapix.base.sdk.rap2.model;

import java.util.Date;
import java.util.List;

/**
 * 仓库信息
 */
public class Rap2Repository {

    private Long id;
    private String name;
    private String description;
    private String logo;
    private String token;
    private Boolean visibility;
    private Long ownerId;
    private Long organizationId;
    private Long creatorId;
    private String lockerId;
    private Date createdAt;
    private Date updatedAt;
    private String deletedAt;
    private Rap2User creator;
    private Rap2User owner;
    private Rap2User locker;
    private List<String> members;
    private Rap2Organization organization;
    private List<String> collaborators;
    private List<Rap2Module> modules;
    private Boolean canUserEdit;

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Rap2User getCreator() {
        return creator;
    }

    public void setCreator(Rap2User creator) {
        this.creator = creator;
    }

    public Rap2User getOwner() {
        return owner;
    }

    public void setOwner(Rap2User owner) {
        this.owner = owner;
    }

    public Rap2User getLocker() {
        return locker;
    }

    public void setLocker(Rap2User locker) {
        this.locker = locker;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Rap2Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Rap2Organization organization) {
        this.organization = organization;
    }

    public List<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<String> collaborators) {
        this.collaborators = collaborators;
    }

    public List<Rap2Module> getModules() {
        return modules;
    }

    public void setModules(List<Rap2Module> modules) {
        this.modules = modules;
    }

    public Boolean getCanUserEdit() {
        return canUserEdit;
    }

    public void setCanUserEdit(Boolean canUserEdit) {
        this.canUserEdit = canUserEdit;
    }
}
