package io.yapix.process.rap2;

import io.yapix.base.sdk.rap2.constant.BodyOption;
import io.yapix.base.sdk.rap2.constant.PropertyScope;
import io.yapix.base.sdk.rap2.model.Rap2Interface;
import io.yapix.base.sdk.rap2.model.Rap2Property;
import io.yapix.model.Api;
import io.yapix.model.RequestBodyType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

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
        rapApi.setRequestProperties(getRequestProperties(projectId, api));
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

    private static List<Rap2Property> getRequestProperties(long repositoryId, Api api) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }
        List<Rap2Property> headers = api.getParameters().stream().map(p -> {
            Rap2Property property = new Rap2Property();
            property.setRepositoryId(repositoryId);
            property.setScope(PropertyScope.request.name());
            property.setName(p.getName());
            property.setType(p.getType());
            property.setDescription(p.getDescription());
            property.setRequired(p.isRequired());
            property.setRule(p.getMock());
            property.setValue(p.getDefaultValue());
            property.setDepth(1);
            property.setParentId(-1L);
            property.setChildren(Collections.emptyList());
            return property;
        }).collect(Collectors.toList());
        return headers;
    }
}
