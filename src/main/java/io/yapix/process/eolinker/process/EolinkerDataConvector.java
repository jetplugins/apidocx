package io.yapix.process.eolinker.process;

import com.google.common.collect.Lists;
import io.yapix.base.sdk.eolinker.constant.ApiStatus;
import io.yapix.base.sdk.eolinker.constant.EolinkerRequired;
import io.yapix.base.sdk.eolinker.constant.RequestParamType;
import io.yapix.base.sdk.eolinker.constant.RequestType;
import io.yapix.base.sdk.eolinker.constant.ResultParamType;
import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.model.EolinkerHeaderProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerProperty;
import io.yapix.base.sdk.eolinker.model.EolinkerResponseItem;
import io.yapix.model.Api;
import io.yapix.model.DataTypes;
import io.yapix.model.HttpMethod;
import io.yapix.model.ParameterIn;
import io.yapix.model.Property;
import io.yapix.model.RequestBodyType;
import io.yapix.parse.util.PropertiesLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Eolinker数据转换
 */
public class EolinkerDataConvector {

    private static final String PROPERTIES_FILE = "yapix/eolinkertypes.properties";

    public static EolinkerApiInfo convert(String projectId, Api api) {
        EolinkerApiInfo eapi = new EolinkerApiInfo();
        eapi.setApiType("http");
        eapi.setBaseInfo(getApiBaseInfo(api));
        eapi.setHeaderInfo(getHeaderInfo(api));
        eapi.setUrlParam(getEolinkerProperties(api.getParameters(), ParameterIn.query));
        eapi.setRestfulParam(getEolinkerProperties(api.getParameters(), ParameterIn.path));
        eapi.setRequestInfo(getRequestInfo(api));
        ArrayList<EolinkerResponseItem> objects = new ArrayList<>();
        objects.add(new EolinkerResponseItem(getResultInfo(api)));
        eapi.setResultInfo(objects);
        eapi.setResultParamType(ResultParamType.JSON);
        if (api.getResponses() != null && DataTypes.ARRAY.equals(api.getResponses().getType())) {
            eapi.setResultParamJsonType(1);
        } else {
            eapi.setResultParamJsonType(0);
        }
        return eapi;
    }

    /**
     * 获取接口基础信息
     */
    private static EolinkerApiBase getApiBaseInfo(Api api) {
        EolinkerApiBase eapiBase = new EolinkerApiBase();
        eapiBase.setApiName(StringUtils.isNotEmpty(api.getSummary()) ? api.getSummary() : api.getPath());
        eapiBase.setApiURI(api.getPath());
        eapiBase.setApiRequestType(getApiRequestType(api.getMethod()));
        eapiBase.setApiName(api.getSummary());
        eapiBase.setGroupName(api.getCategory());
        eapiBase.setApiStatus(ApiStatus.publish);
        eapiBase.setApiRequestParamType(getApiRequestParamType(api.getRequestBodyType()));
        if (api.getRequestBodyType() == RequestBodyType.json) {
            if (DataTypes.ARRAY.equals(api.getRequestBody().getType())) {
                eapiBase.setApiRequestParamJsonType(1);
            } else {
                eapiBase.setApiRequestParamJsonType(0);
            }
        }
        return eapiBase;
    }

    private static Integer getApiRequestType(HttpMethod method) {
        Map<HttpMethod, Integer> map = new HashMap<>();
        map.put(HttpMethod.GET, RequestType.Get);
        map.put(HttpMethod.POST, RequestType.Post);
        map.put(HttpMethod.PUT, RequestType.Put);
        map.put(HttpMethod.DELETE, RequestType.Delete);
        map.put(HttpMethod.HEAD, RequestType.Head);
        map.put(HttpMethod.OPTIONS, RequestType.Options);
        return map.get(method);
    }

    private static Integer getApiRequestParamType(RequestBodyType type) {
        if (type == null) {
            return null;
        }
        Map<RequestBodyType, Integer> map = new HashMap<>();
        map.put(RequestBodyType.json, RequestParamType.JSON);
        map.put(RequestBodyType.form, RequestParamType.FORM_DATA);
        map.put(RequestBodyType.form_data, RequestParamType.FORM_DATA);
        map.put(RequestBodyType.raw, RequestParamType.RAW);
        return map.get(type);
    }

    /**
     * 获取请求头信息
     */
    private static List<EolinkerHeaderProperty> getHeaderInfo(Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }
        List<Property> headers = api.getParameters().stream().filter(p -> p.getIn() == ParameterIn.header)
                .collect(Collectors.toList());
        List<EolinkerHeaderProperty> collect = headers.stream().map(p -> {
            EolinkerHeaderProperty parameter = new EolinkerHeaderProperty();
            parameter.setHeaderName(p.getName());
            parameter.setParamName(p.getDescription());
            parameter.setParamNotNull(p.getRequired() ? EolinkerRequired.YES : EolinkerRequired.NO);
            parameter.setHeaderValue(p.getDefaultValue());
            return parameter;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取查询参数
     */
    private static List<EolinkerProperty> getEolinkerProperties(List<Property> items, ParameterIn in) {
        if (items == null) {
            return Collections.emptyList();
        }
        List<Property> parameters = items.stream()
                .filter(p -> in == null || p.getIn() == in).collect(Collectors.toList());
        List<EolinkerProperty> data = parameters.stream().map(p -> copyToEolinkerProperty(p))
                .collect(Collectors.toList());
        return data;
    }

    private static List<EolinkerProperty> getResultInfo(Api api) {
        if (api.getResponses() == null) {
            return Collections.emptyList();
        }
        return doResolveBeanPropertiesUnwrapRoot(api.getResponses());
    }

    /**
     * 获取请求体
     */
    private static List<EolinkerProperty> getRequestInfo(Api api) {
        RequestBodyType requestBodyType = api.getRequestBodyType();
        if (requestBodyType == RequestBodyType.json || api.getRequestBody() != null) {
            return doResolveBeanPropertiesUnwrapRoot(api.getRequestBody());
        }
        if ((requestBodyType == RequestBodyType.form || requestBodyType == RequestBodyType.form_data)
                && api.getRequestBodyForm() != null) {
            return getEolinkerProperties(api.getRequestBodyForm(), null);
        }
        return Collections.emptyList();
    }

    private static List<EolinkerProperty> doResolveBeanPropertiesUnwrapRoot(Property item) {
        List<EolinkerProperty> data = Lists.newArrayList();
        // 解除顶层root
        if (DataTypes.OBJECT.equals(item.getType()) && item.getProperties() != null) {
            for (Entry<String, Property> entry : item.getProperties().entrySet()) {
                EolinkerProperty property = doResolveBeanProperties(entry.getValue());
                property.setParamKey(entry.getKey());
                data.add(property);
            }
        }
        return data;
    }


    /**
     * 解析标准接口模型到EolinkerProperty树结构
     */
    private static EolinkerProperty doResolveBeanProperties(Property item) {
        EolinkerProperty property = copyToEolinkerProperty(item);
        List<EolinkerProperty> children = Lists.newArrayList();
        property.setChildList(children);

        Map<String, Property> objectProperties = null;
        if (DataTypes.OBJECT.equals(item.getType())) {
            objectProperties = item.getProperties();
        } else {
            Property arrayItem = item.getItems();
            if (DataTypes.ARRAY.equals(item.getType()) && arrayItem != null) {
                // 由于eolinker只支持对象数组: 解对象数组
                if (DataTypes.OBJECT.equals(arrayItem.getType())) {
                    objectProperties = arrayItem.getProperties();
                } else {
                    EolinkerProperty propertyChild = doResolveBeanProperties(arrayItem);
                    children.add(propertyChild);
                }
            }
        }
        if (objectProperties != null) {
            for (Entry<String, Property> entry : objectProperties.entrySet()) {
                String key = entry.getKey();
                Property childItem = entry.getValue();
                EolinkerProperty propertyChild = doResolveBeanProperties(childItem);
                propertyChild.setParamKey(key);
                children.add(propertyChild);
            }
        }
        return property;
    }

    /**
     * 标准模型item转化为简单得Rap2Property属性
     */
    private static EolinkerProperty copyToEolinkerProperty(Property item) {
        Properties types = PropertiesLoader.getProperties(PROPERTIES_FILE);
        String type = types.getProperty(item.getType(), item.getType());

        EolinkerProperty property = new EolinkerProperty();
        property.setParamKey(item.getName());
        property.setParamType(type);
        property.setParamName(item.getDescription());
        property.setParamNotNull(item.getRequired() ? EolinkerRequired.YES : EolinkerRequired.NO);
        property.setDefaultValue(item.getDefaultValue());
        return property;
    }

}
