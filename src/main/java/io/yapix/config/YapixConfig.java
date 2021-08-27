package io.yapix.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Properties;

/**
 * Yapix配置类, 对应文件.yapix
 */
public class YapixConfig {

    /** yapi项目id */
    private String yapiProjectId;

    /** rap2项目id */
    private String rap2ProjectId;

    /** eolinker项目hashKey */
    private String eolinkerProjectId;

    /** 返回值包装类 */
    private String returnWrapType;

    /** 返回值解包装类 */
    private List<String> returnUnwrapTypes;

    /** 参数忽略类 */
    private List<String> parameterIgnoreTypes;

    /**
     * 解析配置
     */
    public static YapixConfig fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String yapiProjectId = properties.getProperty("yapiProjectId", "");
        String rap2ProjectId = properties.getProperty("rap2ProjectId", "");
        String eolinkerProjectId = properties.getProperty("eolinkerProjectId", "");
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");

        YapixConfig config = new YapixConfig();
        config.yapiProjectId = yapiProjectId.trim();
        config.rap2ProjectId = rap2ProjectId.trim();
        config.eolinkerProjectId = eolinkerProjectId.trim();
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        return config;
    }

    /**
     * 合并配置
     */
    public static YapixConfig getMergeSettings(YapixConfig settings, YapixConfig internal) {
        YapixConfig config = new YapixConfig();
        config.setYapiProjectId(settings.getYapiProjectId());
        config.setRap2ProjectId(settings.getRap2ProjectId());
        config.setEolinkerProjectId(settings.getEolinkerProjectId());
        config.setReturnWrapType(settings.getReturnWrapType());

        List<String> returnUnwrapTypes = Lists.newArrayList();
        returnUnwrapTypes.addAll(internal.getReturnUnwrapTypes());
        if (settings.getReturnUnwrapTypes() != null) {
            returnUnwrapTypes.addAll(settings.getReturnUnwrapTypes());
        }
        config.setReturnUnwrapTypes(returnUnwrapTypes);

        List<String> parameterIgnoreTypes = Lists.newArrayList();
        if (settings.getParameterIgnoreTypes() != null) {
            config.setReturnUnwrapTypes(returnUnwrapTypes);
            parameterIgnoreTypes.addAll(settings.getParameterIgnoreTypes());
        }
        parameterIgnoreTypes.addAll(internal.getParameterIgnoreTypes());
        config.setParameterIgnoreTypes(parameterIgnoreTypes);
        return config;
    }

    //-----------------------generated------------------------------//

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

    public String getEolinkerProjectId() {
        return eolinkerProjectId;
    }

    public void setEolinkerProjectId(String eolinkerProjectId) {
        this.eolinkerProjectId = eolinkerProjectId;
    }
}
