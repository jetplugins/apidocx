package io.yapix.process.yapi.process;

import com.google.common.collect.Lists;
import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.model.YapiInterfaceStatus;
import io.yapix.base.sdk.yapi.model.YapiItem;
import io.yapix.base.sdk.yapi.model.YapiMock;
import io.yapix.base.sdk.yapi.model.YapiParameter;
import io.yapix.model.Api;
import io.yapix.model.ParameterIn;
import io.yapix.model.Property;
import io.yapix.model.RequestBodyType;
import io.yapix.parse.util.JsonUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
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
        yapi.setStatus(YapiInterfaceStatus.undone.name());
        yapi.setReqHeaders(resolveParameter(api, ParameterIn.header));
        yapi.setReqQuery(resolveParameter(api, ParameterIn.query));
        yapi.setReqBodyType(resolveReqBodyType(api));
        yapi.setReqBodyForm(resolveReqBodyForm(api));
        yapi.setReqBodyOther(resolveReqBody(api));
        yapi.setReqBodyIsJsonSchema(api.getRequestBodyType() == RequestBodyType.json);
        yapi.setReqParams(resolveParameter(api, ParameterIn.path));
        yapi.setResBody(resolveResBody(api));
        return yapi;
    }

    private static String resolveReqBodyType(Api api) {
        RequestBodyType type = api.getRequestBodyType();
        if (type == RequestBodyType.form_data) {
            type = RequestBodyType.form;
        }
        return type != null ? type.name() : null;
    }

    /**
     * 解析请求参数
     */
    private static List<YapiParameter> resolveParameter(Api api, ParameterIn in) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }

        List<Property> parameters = api.getParameters().stream()
                .filter(p -> p.getIn() == in).collect(Collectors.toList());
        List<YapiParameter> data = parameters.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setType(p.getType());
            parameter.setDesc(p.getDescription());
            parameter.setExample(p.getExample());
            parameter.setRequired(p.getRequired() ? "1" : "0");
            parameter.setValue(p.getDefaultValue());
            return parameter;
        }).collect(Collectors.toList());

        // 请求头
        if (in == ParameterIn.header && api.getRequestBodyType() != null) {
            YapiParameter contentType = new YapiParameter();
            contentType.setName("Content-Type");
            contentType.setValue(api.getRequestBodyType().getContentType());
            data.add(contentType);
        }
        return data;
    }


    /**
     * 解析请求体表单
     */
    private static List<YapiParameter> resolveReqBodyForm(Api api) {
        List<Property> items = api.getRequestBodyForm();
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(p -> {
            YapiParameter parameter = new YapiParameter();
            parameter.setName(p.getName());
            parameter.setType("file".equals(p.getType()) ? "file" : "text");
            parameter.setDesc(p.getDescription());
            parameter.setRequired(p.getRequired() ? "1" : "0");
            parameter.setExample(p.getExample());
            parameter.setDesc(p.getDescription());
            return parameter;
        }).collect(Collectors.toList());
    }

    /**
     * 解析请求体
     */
    private static String resolveReqBody(Api api) {
        Property request = api.getRequestBody();
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
        Property responses = api.getResponses();
        if (responses == null) {
            return "";
        }
        YapiItem item = copyItem(responses);
        return JsonUtils.toJson(item);
    }

    private static YapiItem copyItem(Property item) {
        YapiItem yapiItem = new YapiItem();
        yapiItem.setType(item.getType());
        yapiItem.setDescription(item.getDescription());
        yapiItem.setMock(new YapiMock(item.getMock()));
        // 必填
        List<String> required = Lists.newArrayList();
        if (item.getProperties() != null) {
            for (Entry<String, Property> entry : item.getProperties().entrySet()) {
                if (entry.getValue().getRequired()) {
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
            Map<String, YapiItem> yapiProperties = new LinkedHashMap<>();
            for (Entry<String, Property> entry : item.getProperties().entrySet()) {
                String key = entry.getKey();
                Property value = entry.getValue();
                if (value.getRequired()) {
                    required.add(key);
                }
                yapiProperties.put(key, copyItem(value));
            }
            yapiItem.setProperties(yapiProperties);
        }

        return yapiItem;
    }


}
