package com.github.jetplugins.yapix.config;

import com.github.jetplugins.yapix.constant.ProjectTypeConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * Yapi项目配置.
 */
public class YapiConfig {

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目类型
     */
    private String projectType;

    /**
     * 响应结果包装类.
     */
    private String returnClass;

    /**
     * 附加上传地址.
     */
    private String attachUpload;

    /**
     * 配置是否有效.
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(projectId);
    }

    public boolean isDubboProject() {
        return ProjectTypeConstant.dubbo.equals(projectType);
    }

    public boolean isApiProject() {
        return ProjectTypeConstant.api.equals(projectType);
    }

    //------------------ generated ------------------//

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(String returnClass) {
        this.returnClass = returnClass;
    }

    public String getAttachUpload() {
        return attachUpload;
    }

    public void setAttachUpload(String attachUpload) {
        this.attachUpload = attachUpload;
    }

    @Override
    public String toString() {
        return "YapiProjectConfig{" +
                "projectId='" + projectId + '\'' +
                ", projectType='" + projectType + '\'' +
                ", returnClass='" + returnClass + '\'' +
                ", attachUpload='" + attachUpload + '\'' +
                '}';
    }
}
