package io.yapix.base.sdk.rap2.model;

import java.util.List;

/**
 * 仓库信息
 */
public class Rap2Repository {

    private Long id;
    private String name;
    private String description;
    private List<Rap2Module> modules;

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

    public List<Rap2Module> getModules() {
        return modules;
    }

    public void setModules(List<Rap2Module> modules) {
        this.modules = modules;
    }
}
