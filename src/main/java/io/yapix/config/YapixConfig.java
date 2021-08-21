package io.yapix.config;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * Yapix配置类, 对应文件.yapix
 */
public class YapixConfig {

    /** 项目id */
    private String projectId;

    /** yapi项目id */
    private String yapiProjectId;

    /** rap2项目id */
    private String rap2ProjectId;

    /** 返回值包装类 */
    private String returnWrapType;

    /** 返回值解包装类 */
    private List<String> returnUnwrapTypes;

    /** 参数忽略类 */
    private List<String> parameterIgnoreTypes;

    public static YapixConfig fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String projectId = properties.getProperty("projectId", "");
        String yapiProjectId = properties.getProperty("yapiProjectId", "");
        String rap2ProjectId = properties.getProperty("rap2ProjectId", "");
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");

        YapixConfig config = new YapixConfig();
        config.projectId = projectId;
        config.yapiProjectId = yapiProjectId.trim();
        config.rap2ProjectId = rap2ProjectId.trim();
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        return config;
    }

    /**
     * 获取项目id
     */
    public String getProjectIdByPlatform(ApiPlatformType type) {
        if (type == ApiPlatformType.YAPI && StringUtils.isNotEmpty(yapiProjectId)) {
            return yapiProjectId;
        }
        if (type == ApiPlatformType.RAP2 && StringUtils.isNotEmpty(rap2ProjectId)) {
            return rap2ProjectId;
        }
        return projectId;
    }

    //-----------------------generated------------------------------//


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getYapiProjectId() {
        return yapiProjectId;
    }

    public void setYapiProjectId(String yapiProjectId) {
        this.yapiProjectId = yapiProjectId;
    }

    public String getRap2ProjectId() {
        return rap2ProjectId;
    }

    public void setRap2ProjectId(String rap2ProjectId) {
        this.rap2ProjectId = rap2ProjectId;
    }

    public String getReturnWrapType() {
        return returnWrapType;
    }

    public void setReturnWrapType(String returnWrapType) {
        this.returnWrapType = returnWrapType;
    }

    public List<String> getReturnUnwrapTypes() {
        return returnUnwrapTypes;
    }

    public void setReturnUnwrapTypes(List<String> returnUnwrapTypes) {
        this.returnUnwrapTypes = returnUnwrapTypes;
    }

    public List<String> getParameterIgnoreTypes() {
        return parameterIgnoreTypes;
    }

    public void setParameterIgnoreTypes(List<String> parameterIgnoreTypes) {
        this.parameterIgnoreTypes = parameterIgnoreTypes;
    }
}
