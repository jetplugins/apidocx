package com.github.jetplugins.yapix.sdk.yapi.mode;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * 分类列表
 *
 * @author chengsheng@qbb6.com
 * @date 2019/2/1 10:30 AM
 */
public class YapiCategory implements Serializable {

    /**
     * id
     */
    @SerializedName("_id")
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 项目id
     */
    @SerializedName("project_id")
    private Integer projectId;

    /**
     * 描述
     */
    private String desc;

    /**
     * uid
     */
    private Integer uid;

    /**
     * 排序
     */
    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "YapiCatResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectId=" + projectId +
                ", desc='" + desc + '\'' +
                ", uid=" + uid +
                ", index=" + index +
                '}';
    }
}
