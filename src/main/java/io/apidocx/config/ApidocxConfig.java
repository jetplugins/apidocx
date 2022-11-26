package io.apidocx.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.apidocx.parse.util.PropertiesLoader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 对应文件.yapix
 */
@Data
public class ApidocxConfig {

    /**
     * 严格模式: 未指定分类、接口名不处理
     */
    private boolean strict = true;

    /**
     * yapi项目id
     */
    private String yapiProjectId;

    /**
     * rap2项目id
     */
    private String rap2ProjectId;

    /**
     * eolink项目hashKey
     */
    private String eolinkProjectId;

    /**
     * showdoc项目id
     */
    private String showdocProjectId;

    /**
     * apifox项目id
     */
    private String apifoxProjectId;

    /**
     * YApi服务地址: 用于统一登录场景
     */
    private String yapiUrl;

    /**
     * YApi项目token: 用于统一登录场景
     */
    private String yapiProjectToken;

    /**
     * 返回值包装类
     */
    private String returnWrapType;

    /**
     * 返回值解包装类
     */
    private List<String> returnUnwrapTypes;

    /**
     * 参数忽略类
     */
    private List<String> parameterIgnoreTypes;

    /**
     * 自定义bean配置
     */
    private Map<String, BeanCustom> beans;

    /**
     * 智能mock规则
     */
    private List<MockRule> mockRules;

    /**
     * 自定义注解值，简化@RequestBody注解
     */
    private RequestBodyParamType requestBodyParamType;

    /**
     * 时间格式: 查询参数和表单
     */
    private String dateTimeFormatMvc;

    /**
     * 时间格式: Json
     */
    private String dateTimeFormatJson;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 时间格式
     */
    private String timeFormat;

    private static final Pattern BEANS_PATTERN = Pattern.compile("^beans\\[(.+)]$");

    @Data
    public static class RequestBodyParamType {
        /**
         * 注解类型
         */
        private String annotation;

        /**
         * 注解属性
         */
        private String property;

        public RequestBodyParamType(String type) {
            String[] splits = type.split("#");
            this.annotation = splits[0];
            if (splits.length > 1) {
                this.property = splits[1];
            } else {
                this.property = "value";
            }
        }
    }

    /**
     * 解析配置
     */
    public static ApidocxConfig fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String strict = properties.getProperty("strict", "");
        String yapiProjectId = properties.getProperty("yapiProjectId", "");
        String yapiUrl = properties.getProperty("yapiUrl", "");
        String yapiProjectToken = properties.getProperty("yapiProjectToken", "");
        String rap2ProjectId = properties.getProperty("rap2ProjectId", "");
        String eolinkerProjectId = properties.getProperty("eolinkerProjectId", "");
        String showdocProjectId = properties.getProperty("showdocProjectId", "");
        String apifoxProjectId = properties.getProperty("apifoxProjectId", "");
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");
        String mockRules = properties.getProperty("mockRules");
        String dateTimeFormatMvc = properties.getProperty("dateTimeFormatMvc", "");
        String dateTimeFormatJson = properties.getProperty("dateTimeFormatJson", "");
        String dateFormat = properties.getProperty("dateFormat", "");
        String timeFormat = properties.getProperty("timeFormat", "");
        String requestBodyParamType = properties.getProperty("requestBodyParamType", "");

        ApidocxConfig config = new ApidocxConfig();
        if (StringUtils.isNotEmpty(strict)) {
            config.strict = Boolean.parseBoolean(strict);
        }
        config.yapiUrl = yapiUrl.trim();
        config.yapiProjectToken = yapiProjectToken.trim();
        config.yapiProjectId = yapiProjectId.trim();
        config.rap2ProjectId = rap2ProjectId.trim();
        config.eolinkProjectId = eolinkerProjectId.trim();
        config.showdocProjectId = showdocProjectId.trim();
        config.apifoxProjectId = apifoxProjectId.trim();
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        config.dateTimeFormatMvc = dateTimeFormatMvc;
        config.dateTimeFormatJson = dateTimeFormatJson;
        config.dateFormat = dateFormat;
        config.timeFormat = timeFormat;
        if (StringUtils.isNotEmpty(requestBodyParamType)) {
            config.requestBodyParamType = new RequestBodyParamType(requestBodyParamType);
        }

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
    public static ApidocxConfig getMergedInternalConfig(ApidocxConfig settings) {
        Properties properties = PropertiesLoader.getProperties(".yapix");
        ApidocxConfig internal = ApidocxConfig.fromProperties(properties);

        ApidocxConfig config = new ApidocxConfig();
        config.setStrict(settings.isStrict());
        config.setYapiUrl(settings.getYapiUrl());
        config.setYapiProjectId(settings.getYapiProjectId());
        config.setYapiProjectToken(settings.getYapiProjectToken());
        config.setRap2ProjectId(settings.getRap2ProjectId());
        config.setEolinkProjectId(settings.getEolinkProjectId());
        config.setShowdocProjectId(settings.getShowdocProjectId());
        config.setApifoxProjectId(settings.getApifoxProjectId());
        config.setReturnWrapType(settings.getReturnWrapType());
        config.setDateTimeFormatMvc(settings.getDateTimeFormatMvc());
        config.setDateTimeFormatJson(settings.getDateTimeFormatJson());
        config.setDateFormat(settings.getDateFormat());
        config.setTimeFormat(settings.getTimeFormat());
        config.setRequestBodyParamType(settings.getRequestBodyParamType());

        // 时间格式
        if (StringUtils.isBlank(settings.getDateTimeFormatMvc())) {
            config.setDateTimeFormatMvc(internal.getDateTimeFormatMvc());
        }
        if (StringUtils.isBlank(settings.getDateTimeFormatJson())) {
            config.setDateTimeFormatJson(internal.getDateTimeFormatJson());
        }
        if (StringUtils.isBlank(settings.getDateFormat())) {
            config.setDateFormat(internal.getDateFormat());
        }
        if (StringUtils.isBlank(settings.getTimeFormat())) {
            config.setTimeFormat(internal.getTimeFormat());
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

}
