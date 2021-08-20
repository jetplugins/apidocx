package io.yapix.rap2.process;

import com.google.common.collect.Lists;
import io.yapix.base.sdk.rap2.constant.BodyOption;
import io.yapix.base.sdk.rap2.constant.PropertyPos;
import io.yapix.base.sdk.rap2.constant.PropertyScope;
import io.yapix.base.sdk.rap2.model.Rap2Interface;
import io.yapix.base.sdk.rap2.model.Rap2Property;
import io.yapix.model.Api;
import io.yapix.model.DataTypes;
import io.yapix.model.Item;
import io.yapix.model.ParameterIn;
import io.yapix.model.RequestBodyType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Rap2数据转换
 */
public class Rap2DataConvector {

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
            properties.get(i).setMemory(true);
        }
        rapApi.setProperties(properties);

        return rapApi;
    }

    public static String getBodyOption(Api api) {
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
     * 解析响应参数
     */
    private static List<Rap2Property> getResponseProperties(Integer repositoryId, Api api) {
        Item item = api.getResponses();
        if (item == null) {
            return Lists.newArrayList();
        }
        Rap2Property property = doResolveBeanProperties(item, repositoryId, PropertyScope.response, PropertyPos.QUERY);
        if (StringUtils.isEmpty(property.getName())) {
            property.setName("root");
        }
        List<Rap2Property> container = Lists.newArrayList();
        flatRap2Property(container, property);
        return container;
    }

    /**
     * 解析请求参数
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
            List<Rap2Property> properties3 = resolveRequestBodyProperties(api.getRequestBody(), repositoryId);
            properties.addAll(properties3);
        }
        return properties;
    }

    private static List<Rap2Property> doGetRequestParameterProperties(List<Item> items, Long repositoryId,
            PropertyScope scope,
            long defaultPos) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<ParameterIn, Long> posMap = new HashMap<>();
        posMap.put(ParameterIn.header, PropertyPos.HEADER);
        posMap.put(ParameterIn.query, PropertyPos.QUERY);
        return items.stream().map(p -> {
            Rap2Property property = new Rap2Property();
            property.setRepositoryId(repositoryId);
            property.setScope(scope.name());
            property.setName(p.getName());
            property.setType(p.getType());
            property.setDescription(p.getDescription());
            property.setRequired(p.isRequired());
            property.setValue(p.getDefaultValue());
            property.setDepth(1);
            property.setParentId("-1");
            property.setChildren(Collections.emptyList());
            property.setPos(posMap.getOrDefault(p.getIn(), defaultPos));
            return property;
        }).collect(Collectors.toList());
    }

    private static List<Rap2Property> resolveRequestBodyProperties(Item item, long repositoryId) {
        Rap2Property property = doResolveBeanProperties(item, repositoryId, PropertyScope.request, PropertyPos.BODY);
        if (StringUtils.isEmpty(property.getName())) {
            property.setName("root");
        }
        List<Rap2Property> container = Lists.newArrayList();
        flatRap2Property(container, property);
        return container;
    }

    /**
     * 解析标准接口模型到Rap2Property树结构
     */
    private static Rap2Property doResolveBeanProperties(Item item, long repositoryId, PropertyScope scope,
            long propertyPos) {
        Rap2Property property = new Rap2Property();
        property.setId(UUID.randomUUID().toString());
        property.setRepositoryId(repositoryId);
        property.setScope(scope.name());
        property.setName(item.getName());
        property.setType(item.getType());
        property.setDescription(item.getDescription());
        property.setRequired(item.isRequired());
        property.setValue(item.getDefaultValue());
        property.setDepth(1);
        property.setParentId("-1");

        List<Rap2Property> children = Lists.newArrayList();
        if (DataTypes.OBJECT.equals(item.getType()) && item.getProperties() != null) {
            for (Entry<String, Item> entry : item.getProperties().entrySet()) {
                String key = entry.getKey();
                Item childItem = entry.getValue();
                Rap2Property propertyChild = doResolveBeanProperties(childItem, repositoryId, scope, propertyPos);
                propertyChild.setName(key);
                propertyChild.setParentId(property.getId());
                children.add(propertyChild);
            }
        } else if (DataTypes.ARRAY.equals(item.getType()) && item.getItems() != null) {
            Rap2Property propertyChild = doResolveBeanProperties(item.getItems(), repositoryId, scope, propertyPos);
            propertyChild.setParentId(property.getId());
            children.add(propertyChild);
        }
        property.setChildren(children);
        property.setPos(propertyPos);
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
