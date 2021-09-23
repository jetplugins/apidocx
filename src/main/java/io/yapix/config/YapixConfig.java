package io.yapix.config;

import static io.yapix.config.DefaultConstants.FILE_NAME;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.yapix.parse.util.PropertiesLoader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

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

    /** showdoc项目id */
    private String showdocProjectId;

    /** YApi服务地址: 用于统一登录场景 */
    private String yapiUrl;

    /** YApi项目token: 用于统一登录场景 */
    private String yapiProjectToken;

    /** 返回值包装类 */
    private String returnWrapType;

    /** 返回值解包装类 */
    private List<String> returnUnwrapTypes;

    /** 参数忽略类 */
    private List<String> parameterIgnoreTypes;

    /**
     * 自定义bean配置
     */
    private Map<String, BeanCustom> beans;

    /** 智能mock规则 */
    private List<MockRule> mockRules;

    /** 时间格式: 查询参数和表单 */
    private String dateTimeFormatMvc;

    /** 时间格式: Json */
    private String dateTimeFormatJson;

    private static final Pattern BEANS_PATTERN = Pattern.compile("^beans\\[(.+)]$");

    /**
     * 解析配置
     */
    public static YapixConfig fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String yapiProjectId = properties.getProperty("yapiProjectId", "");
        String yapiUrl = properties.getProperty("yapiUrl", "");
        String yapiProjectToken = properties.getProperty("yapiProjectToken", "");
        String rap2ProjectId = properties.getProperty("rap2ProjectId", "");
        String eolinkerProjectId = properties.getProperty("eolinkerProjectId", "");
        String showdocProjectId = properties.getProperty("showdocProjectId", "");
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");
        String mockRules = properties.getProperty("mockRules");
        String dateTimeFormatMvc = properties.getProperty("dateTimeFormatMvc", "");
        String dateTimeFormatJson = properties.getProperty("dateTimeFormatJson", "");

        YapixConfig config = new YapixConfig();
        config.yapiUrl = yapiUrl.trim();
        config.yapiProjectToken = yapiProjectToken.trim();
        config.yapiProjectId = yapiProjectId.trim();
        config.rap2ProjectId = rap2ProjectId.trim();
        config.eolinkerProjectId = eolinkerProjectId.trim();
        config.showdocProjectId = showdocProjectId.trim();
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        config.dateTimeFormatMvc = dateTimeFormatMvc;
        config.dateTimeFormatJson = dateTimeFormatJson;

        // 解析自定义bean配置: beans[xxx].json=xxx
        Gson gson = new Gson();
        Map<String, BeanCustom> beans = Maps.newHashMap();
        config.setBeans(beans);
        for (String p : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(p);
            if (StringUtils.isEmpty(propertyValue)) {
                continue;
            }
            Matcher matcher = BEANS_PATTERN.matcher(p);
            if (!matcher.matches()) {
                continue;
            }
            String beanType = matcher.group(1);
            BeanCustom beanCustom = gson.fromJson(propertyValue, BeanCustom.class);
            beans.put(beanType, beanCustom);
        }

        // 智能mock规则
        if (StringUtils.isNotEmpty(mockRules)) {
            Type type = new TypeToken<List<MockRule>>() {
            }.getType();
            config.mockRules = gson.fromJson(mockRules, type);
        }
        return config;
    }

    /**
     * 合并配置
     */
    public YapixConfig getMergedInternalConfig() {
        Properties properties = PropertiesLoader.getProperties(FILE_NAME);
        YapixConfig internal = YapixConfig.fromProperties(properties);
        YapixConfig settings = this;

        YapixConfig config = new YapixConfig();
        config.setYapiUrl(settings.getYapiUrl());
        config.setYapiProjectId(settings.getYapiProjectId());
        config.setYapiProjectToken(settings.getYapiProjectToken());
        config.setRap2ProjectId(settings.getRap2ProjectId());
        config.setEolinkerProjectId(settings.getEolinkerProjectId());
        config.setShowdocProjectId(settings.getShowdocProjectId());
        config.setReturnWrapType(settings.getReturnWrapType());
        config.setDateTimeFormatMvc(settings.getDateTimeFormatMvc());
        config.setDateTimeFormatJson(settings.getDateTimeFormatJson());

        // 时间格式
        if (StringUtils.isBlank(settings.getDateTimeFormatMvc())) {
            config.setDateTimeFormatMvc(internal.getDateTimeFormatMvc());
        }
        if (StringUtils.isBlank(settings.getDateTimeFormatJson())) {
            config.setDateTimeFormatJson(internal.getDateTimeFormatJson());
        }

        // 解包装类型
        List<String> returnUnwrapTypes = Lists.newArrayList();
        returnUnwrapTypes.addAll(internal.getReturnUnwrapTypes());
        if (settings.getReturnUnwrapTypes() != null) {
            returnUnwrapTypes.addAll(settings.getReturnUnwrapTypes());
        }
        config.setReturnUnwrapTypes(returnUnwrapTypes);

        // 忽略参数类型
        List<String> parameterIgnoreTypes = Lists.newArrayList();
        if (settings.getParameterIgnoreTypes() != null) {
            config.setReturnUnwrapTypes(returnUnwrapTypes);
            parameterIgnoreTypes.addAll(settings.getParameterIgnoreTypes());
        }
        parameterIgnoreTypes.addAll(internal.getParameterIgnoreTypes());
        config.setParameterIgnoreTypes(parameterIgnoreTypes);

        // 自定义bean配置
        Map<String, BeanCustom> beans = Maps.newHashMap();
        if (internal.getBeans() != null) {
            beans.putAll(internal.getBeans());
        }
        if (settings.getBeans() != null) {
            beans.putAll(settings.getBeans());
        }
        config.setBeans(beans);

        // mock规则
        List<MockRule> mockRules = Lists.newArrayList();
        if (settings.getMockRules() != null) {
            mockRules.addAll(settings.getMockRules());
        }
        if (internal.getMockRules() != null) {
            mockRules.addAll(internal.getMockRules());
        }
        config.setMockRules(mockRules);
        return config;
    }

    //-----------------------generated------------------------------//

    public String getYapiProjectId() {
        return yapiProjectId;
    }

    public void setYapiProjectId(String yapiProjectId) {
        this.yapiProjectId = yapiProjectId;
    }

    public String getYapiUrl() {
        return yapiUrl;
    }

    public void setYapiUrl(String yapiUrl) {
        this.yapiUrl = yapiUrl;
    }

    public String getYapiProjectToken() {
        return yapiProjectToken;
    }

    public void setYapiProjectToken(String yapiProjectToken) {
        this.yapiProjectToken = yapiProjectToken;
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

    public Map<String, BeanCustom> getBeans() {
        return beans;
    }

    public void setBeans(Map<String, BeanCustom> beans) {
        this.beans = beans;
    }

    public List<MockRule> getMockRules() {
        return mockRules;
    }

    public void setMockRules(List<MockRule> mockRules) {
        this.mockRules = mockRules;
    }

    public String getDateTimeFormatMvc() {
        return dateTimeFormatMvc;
    }

    public void setDateTimeFormatMvc(String dateTimeFormatMvc) {
        this.dateTimeFormatMvc = dateTimeFormatMvc;
    }

    public String getDateTimeFormatJson() {
        return dateTimeFormatJson;
    }

    public void setDateTimeFormatJson(String dateTimeFormatJson) {
        this.dateTimeFormatJson = dateTimeFormatJson;
    }

    public String getShowdocProjectId() {
        return showdocProjectId;
    }

    public void setShowdocProjectId(String showdocProjectId) {
        this.showdocProjectId = showdocProjectId;
    }
}
