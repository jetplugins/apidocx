package io.yapix.base.sdk.yapi.mode;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

/**
 * 新增菜单
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/1 10:44 AM
 */
public class YapiCategoryAddRequest implements Serializable {

    /**
     * 项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 名字
     */
    private String name;

    /**
     * 描述
     */
    private String desc = "工具上传临时文件夹";

    /**
     * 父级菜单id
     */
    @SerializedName("parent_id")
    private Integer parentId = -1;

    /**
     * token
     */
    private String token;

    public YapiCategoryAddRequest() {
    }

    public YapiCategoryAddRequest(String name, Integer projectId, Integer parentId) {
        this.name = name;
        this.projectId = projectId;
        this.parentId = parentId;
        if (Objects.isNull(parentId)) {
            this.parentId = -1;
        }
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "YapiCategoryAddRequest{" +
                "projectId=" + projectId +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", parentId=" + parentId +
                ", token='" + token + '\'' +
                '}';
    }
}
