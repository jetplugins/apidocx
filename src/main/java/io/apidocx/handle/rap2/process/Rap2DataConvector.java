package io.apidocx.handle.rap2.process;

import com.google.common.collect.Lists;
import io.apidocx.base.sdk.rap2.constant.BodyOption;
import io.apidocx.base.sdk.rap2.constant.PropertyPos;
import io.apidocx.base.sdk.rap2.constant.PropertyScope;
import io.apidocx.base.sdk.rap2.model.Rap2Interface;
import io.apidocx.base.sdk.rap2.model.Rap2Property;
import io.apidocx.model.Api;
import io.apidocx.model.DataTypes;
import io.apidocx.model.ParameterIn;
import io.apidocx.model.Property;
import io.apidocx.model.RequestBodyType;
import io.apidocx.parse.util.PropertiesLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Rap2数据转换
 */
public class Rap2DataConvector {

    private static final String PROPERTIES_FILE = "rap2types.properties";

    public static Rap2Interface convert(Integer projectId, Api api) {
        Rap2Interface rapApi = new Rap2Interface();
        rapApi.setRepositoryId(projectId.longValue());
        rapApi.setName(StringUtils.isNotEmpty(api.getSummary()) ? api.getSummary() : api.getPath());
        rapApi.setUrl(api.getPath());
        rapApi.setMethod(api.getMethod().name());
        rapApi.setDescription(api.getDescription());
        rapApi.setModuleName(api.getCategory());
        rapApi.setBodyOption(getBodyOption(api));
        rapApi.setStatus(200);
        // 请求参数
        List<Rap2Property> requestProperties = getRequestProperties(projectId, api);
        rapApi.setRequestProperties(requestProperties);
        // 响应参数
        List<Rap2Property> responseProperties = getResponseProperties(projectId, api);
        rapApi.setResponseProperties(responseProperties);
        // 所有数据
        List<Rap2Property> properties = Lists.newArrayListWithExpectedSize(requestProperties.size()
                + responseProperties.size());
        properties.addAll(requestProperties);
        properties.addAll(responseProperties);
        for (int i = 0; i < properties.size(); i++) {
            properties.get(i).setPriority(i + 1L);
        }
        rapApi.setProperties(properties);
        return rapApi;
    }

    /**
     * 获取请求体类型
     */
    private static String getBodyOption(Api api) {
        RequestBodyType type = api.getRequestBodyType();
        if (type == null) {
            return null;
        }
        Map<RequestBodyType, BodyOption> map = new HashMap<>();
        map.put(RequestBodyType.form, BodyOption.FORM_URLENCODED);
        map.put(RequestBodyType.json, BodyOption.RAW);
        map.put(RequestBodyType.form_data, BodyOption.FORM_DATA);
        map.put(RequestBodyType.raw, BodyOption.RAW);
        BodyOption bodyOption = map.get(type);
        return bodyOption != null ? bodyOption.name() : null;
    }

    /**
     * 获取响应参数
     */
    private static List<Rap2Property> getResponseProperties(Integer repositoryId, Api api) {
        Property item = api.getResponses();
        if (item == null) {
            return Lists.newArrayList();
        }
        List<Rap2Property> properties = doResolveBeanPropertiesUnwrapRoot(item, repositoryId, PropertyScope.response,
                PropertyPos.QUERY);
        List<Rap2Property> container = Lists.newArrayList();
        for (Rap2Property property : properties) {
            flatRap2Property(container, property);
        }
        return container;
    }

    private static List<Rap2Property> doResolveBeanPropertiesUnwrapRoot(Property item, long repositoryId,
            PropertyScope scope, long propertyPos) {
        List<Rap2Property> data = Lists.newArrayList();
        // 解除顶层root
        if (DataTypes.OBJECT.equals(item.getType()) && item.getProperties() != null) {
            for (Entry<String, Property> entry : item.getProperties().entrySet()) {
                Rap2Property property = doResolveBeanProperties(entry.getValue(), repositoryId, scope, propertyPos);
                property.setName(entry.getKey());
                data.add(property);
            }
        }
        return data;
    }

    /**
     * 获取请求参数
     */
    private static List<Rap2Property> getRequestProperties(long repositoryId, Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }
        List<Rap2Property> properties1 = doGetRequestParameterProperties(api.getParameters(), repositoryId,
                PropertyScope.request,
                PropertyPos.QUERY);
        List<Rap2Property> properties2 = doGetRequestParameterProperties(api.getRequestBodyForm(), repositoryId,
                PropertyScope.request,
                PropertyPos.BODY);
        List<Rap2Property> properties = Lists.newArrayListWithExpectedSize(properties1.size() + properties2.size());
        properties.addAll(properties1);
        properties.addAll(properties2);
        // 请求体json
        if (api.getRequestBodyType() == RequestBodyType.json) {
            List<Rap2Property> properties3 = doResolveRequestBodyProperties(api.getRequestBody(), repositoryId);
            properties.addAll(properties3);
        }
        return properties;
    }

    private static List<Rap2Property> doResolveRequestBodyProperties(Property item, long repositoryId) {
        if (item == null) {
            return Lists.newArrayList();
        }
        List<Rap2Property> properties = doResolveBeanPropertiesUnwrapRoot(item, repositoryId, PropertyScope.request,
                PropertyPos.BODY);
        List<Rap2Property> container = Lists.newArrayList();
        for (Rap2Property property : properties) {
            flatRap2Property(container, property);
        }
        return container;
    }

    private static List<Rap2Property> doGetRequestParameterProperties(List<Property> items, Long repositoryId,
            PropertyScope scope,
            long defaultPos) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<ParameterIn, Long> posMap = new HashMap<>();
        posMap.put(ParameterIn.header, PropertyPos.HEADER);
        posMap.put(ParameterIn.query, PropertyPos.QUERY);
        return items.stream().map(p -> {
            Rap2Property property = copyToRap2Property(p, scope);
            property.setRepositoryId(repositoryId);
            property.setPos(posMap.getOrDefault(p.getIn(), defaultPos));
            property.setChildren(Collections.emptyList());
            return property;
        }).collect(Collectors.toList());
    }

    /**
     * 解析标准接口模型到Rap2Property树结构
     */
    private static Rap2Property doResolveBeanProperties(Property item, long repositoryId, PropertyScope scope,
            long propertyPos) {
        Rap2Property property = copyToRap2Property(item, scope);
        property.setRepositoryId(repositoryId);
        property.setPos(propertyPos);
        List<Rap2Property> children = Lists.newArrayList();
        property.setChildren(children);

        Map<String, Property> objectProperties = null;
        if (DataTypes.OBJECT.equals(item.getType())) {
            objectProperties = item.getProperties();
        } else {
            Property arrayItem = item.getItems();
            if (DataTypes.ARRAY.equals(item.getType()) && arrayItem != null) {
                // 由于rap2只支持对象数组: 解对象数组
                if (DataTypes.OBJECT.equals(arrayItem.getType())) {
                    objectProperties = arrayItem.getProperties();
                } else {
                    Rap2Property propertyChild = doResolveBeanProperties(arrayItem, repositoryId, scope, propertyPos);
                    propertyChild.setParentId(property.getId());
                    children.add(propertyChild);
                }
            }
        }
        if (objectProperties != null) {
            for (Entry<String, Property> entry : objectProperties.entrySet()) {
                String key = entry.getKey();
                Property childItem = entry.getValue();
                Rap2Property propertyChild = doResolveBeanProperties(childItem, repositoryId, scope, propertyPos);
                propertyChild.setName(key);
                propertyChild.setParentId(property.getId());
                children.add(propertyChild);
            }
        }
        return property;
    }

    /**
     * 标准模型item转化为简单得Rap2Property属性
     */
    private static Rap2Property copyToRap2Property(Property item, PropertyScope scope) {
        Properties types = PropertiesLoader.getProperties(PROPERTIES_FILE);
        String type = types.getProperty(item.getType(), item.getType());

        Rap2Property property = new Rap2Property();
        property.setId(UUID.randomUUID().toString());
        property.setScope(scope.name());
        property.setName(item.getName());
        property.setType(type);
        property.setDescription(item.getDescriptionMore());
        property.setRequired(item.getRequired());
        property.setValue(item.getMock());
        property.setDepth(1);
        property.setParentId("-1");
        property.setMemory(true);
        property.setPriority(1L);
        return property;
    }

    /**
     * 树形结构转化为扁平结构
     */
    private static void flatRap2Property(List<Rap2Property> container, Rap2Property property) {
        if (StringUtils.isEmpty(property.getName())) {
            property.setName("[element]");
        }
        List<Rap2Property> children = property.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            property.setChildren(Collections.emptyList());
            for (Rap2Property child : children) {
                flatRap2Property(container, child);
            }
        }
        container.add(property);
    }

}
