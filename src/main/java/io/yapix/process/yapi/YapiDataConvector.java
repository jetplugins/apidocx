package io.yapix.process.yapi;

import com.google.common.collect.Lists;
import io.yapix.base.sdk.yapi.mode.YapiInterface;
import io.yapix.base.sdk.yapi.mode.YapiInterfaceStatus;
import io.yapix.base.sdk.yapi.mode.YapiItem;
import io.yapix.base.sdk.yapi.mode.YapiMock;
import io.yapix.base.sdk.yapi.mode.YapiParameter;
import io.yapix.model.Api;
import io.yapix.model.Item;
import io.yapix.model.ParameterIn;
import io.yapix.model.RequestBodyType;
import io.yapix.parse.util.JsonUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

/**
 * Yapi数据转换
 */
public class YapiDataConvector {

    public static YapiInterface convert(Integer projectId, Api api) {
        YapiInterface yapi = new YapiInterface();
        yapi.setProjectId(projectId);
        yapi.setTitle(StringUtils.isNotEmpty(api.getSummary()) ? api.getSummary() : api.getPath());
        yapi.setPath(api.getPath());
        yapi.setMethod(api.getMethod().name());
        yapi.setDesc(api.getDescription());
        yapi.setMenu(api.getCategory());
        if (api.getDeprecated()) {
            yapi.setStatus(YapiInterfaceStatus.deprecated.name());
        }
        yapi.setReqHeaders(resolveReqHeaders(api));
        yapi.setReqQuery(resolveReqQuery(api));
        yapi.setReqBodyType(api.getRequestBodyType() != null ? api.getRequestBodyType().name() : null);
        yapi.setReqBodyForm(resolveReqBodyForm(api));
        yapi.setReqBodyOther(resolveReqBody(api));
        yapi.setReqBodyIsJsonSchema(api.getRequestBodyType() == RequestBodyType.json);
        yapi.setReqParams(resolveReqParams(api));
        yapi.setResBody(resolveResBody(api));
        return yapi;
    }

    /**
     * 解析请求参数
     */
    private static List<YapiParameter> resolveReqQuery(Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }

        List<Item> parameters = api.getParameters().stream()
                .filter(p -> ParameterIn.query == p.getIn()).collect(Collectors.toList());
        return parameters.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setDesc(p.getDescription());
            parameter.setExample(p.getExample());
            parameter.setRequired(p.isRequired() ? "1" : "0");
            return parameter;
        }).collect(Collectors.toList());
    }


    /**
     * 解析请求参数
     */
    private static List<YapiParameter> resolveReqHeaders(Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }

        List<Item> parameters = api.getParameters().stream()
                .filter(p -> ParameterIn.header == p.getIn()).collect(Collectors.toList());
        List<YapiParameter> headers = parameters.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setDesc(p.getDescription());
            parameter.setExample(p.getExample());
            parameter.setValue(p.getDefaultValue());
            return parameter;
        }).collect(Collectors.toList());

        // 请求头
        if (api.getRequestBodyType() != null) {
            YapiParameter contentType = new YapiParameter();
            contentType.setName("Content-Type");
            contentType.setValue(api.getRequestBodyType().getContentType());
            headers.add(contentType);
        }
        return headers;
    }

    /**
     * 解析路径参数
     */
    private static List<YapiParameter> resolveReqParams(Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }

        List<Item> parameters = api.getParameters().stream()
                .filter(p -> ParameterIn.path == p.getIn()).collect(Collectors.toList());
        return parameters.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setDesc(p.getDescription());
            parameter.setExample(p.getExample());
            parameter.setRequired("1");
            return parameter;
        }).collect(Collectors.toList());
    }

    /**
     * 解析请求体表单
     */
    private static List<YapiParameter> resolveReqBodyForm(Api api) {
        List<Item> items = api.getRequestBodyForm();
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setDesc(p.getDescription());
            parameter.setRequired(p.isRequired() ? "1" : "0");
            parameter.setExample(p.getExample());
            return parameter;
        }).collect(Collectors.toList());
    }

    /**
     * 解析请求体
     */
    private static String resolveReqBody(Api api) {
        Item request = api.getRequestBody();
        if (request == null) {
            return "";
        }
        YapiItem item = copyItem(request);
        return JsonUtils.toJson(item);
    }

    /**
     * 解析响应体
     */
    private static String resolveResBody(Api api) {
        Item responses = api.getResponses();
        if (responses == null) {
            return "";
        }
        YapiItem item = copyItem(responses);
        return JsonUtils.toJson(item);
    }

    private static YapiItem copyItem(Item item) {
        YapiItem yapiItem = new YapiItem();
        yapiItem.setType(item.getType());
        yapiItem.setDescription(item.getDescription());
        yapiItem.setMock(new YapiMock(item.getMock()));
        // 必填
        List<String> required = Lists.newArrayList();
        if (item.getProperties() != null) {
            for (Entry<String, Item> entry : item.getProperties().entrySet()) {
                if (entry.getValue().isRequired()) {
                    required.add(entry.getKey());
                }
            }
        }
        yapiItem.setRequired(required);
        // 数组
        if (item.getItems() != null) {
            yapiItem.setItems(copyItem(item.getItems()));
        }
        // 对象
        if (item.getProperties() != null) {
            Map<String, YapiItem> yapiProperties = new HashMap<>();
            for (Entry<String, Item> entry : item.getProperties().entrySet()) {
                String key = entry.getKey();
                Item value = entry.getValue();
                if (value.isRequired()) {
                    required.add(key);
                }
                yapiProperties.put(key, copyItem(value));
            }
            yapiItem.setProperties(yapiProperties);
        }

        return yapiItem;
    }


}
