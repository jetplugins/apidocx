package io.apidocx.handle.apifox.process;


import io.apidocx.base.sdk.apifox.model.ApiDetail;
import io.apidocx.base.sdk.apifox.model.ApiDetail.Mock;
import io.apidocx.base.sdk.apifox.model.ApiDetail.Parameter;
import io.apidocx.base.sdk.apifox.model.ApiDetail.Parameters;
import io.apidocx.base.sdk.apifox.model.ApiDetail.RequestBody;
import io.apidocx.base.sdk.apifox.model.ApiDetail.Response;
import io.apidocx.base.sdk.apifox.model.ApiDetail.Schema;
import io.apidocx.model.Api;
import io.apidocx.model.ParameterIn;
import io.apidocx.model.Property;
import io.apidocx.model.RequestBodyType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang.RandomStringUtils;

public class ApifoxDataConvector {

    public ApiDetail convert(Long projectId, Api api) {
        ApiDetail apiDetail = new ApiDetail();
        apiDetail.setProjectId(projectId);
        apiDetail.setName(api.getSummary());
        apiDetail.setMethod(api.getMethod().name().toLowerCase());
        apiDetail.setPath(api.getPath());
        apiDetail.setType("http");
        apiDetail.setStatus("released");
        apiDetail.setParameters(getRequestParameters(api));
        apiDetail.setRequestBody(getRequestBody(api));
        apiDetail.setResponses(getResponses(api));
        return apiDetail;
    }

    private Parameters getRequestParameters(Api api) {
        List<Parameter> queryList = buildParameters(api.getParametersByIn(ParameterIn.query));
        List<Parameter> pathList = buildParameters(api.getParametersByIn(ParameterIn.path));
        List<Parameter> headerList = buildParameters(api.getParametersByIn(ParameterIn.header));
        List<Parameter> cookiesList = buildParameters(api.getParametersByIn(ParameterIn.cookie));

        Parameters parameters = new Parameters();
        parameters.setQuery(queryList);
        parameters.setPath(pathList);
        parameters.setHeader(headerList);
        parameters.setCookie(cookiesList);
        return parameters;
    }

    private RequestBody getRequestBody(Api api) {
        RequestBodyType bodyType = api.getRequestBodyType();
        if (bodyType == null) {
            return null;
        }
        RequestBody requestBody = new RequestBody();
        requestBody.setType(bodyType.getContentType());
        if (bodyType == RequestBodyType.form) {
            requestBody.setParameters(buildParameters(api.getRequestBodyForm()));
        } else if (bodyType == RequestBodyType.json) {
            requestBody.setJsonSchema(buildSchema(api.getRequestBody()));
        }
        return requestBody;
    }

    private List<Response> getResponses(Api api) {
        Response response = new Response();
        response.setCode(200);
        response.setName("OK");
        response.setContentType("json");
        if (api.getResponses() != null) {
            response.setJsonSchema(buildSchema(api.getResponses()));
        }
        List<Response> responses = new ArrayList<>();
        responses.add(response);
        return responses;
    }

    private List<Parameter> buildParameters(List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            return Collections.emptyList();
        }
        return properties.stream().map(this::buildParameter).collect(Collectors.toList());
    }

    private Parameter buildParameter(Property property) {
        Parameter parameter = new Parameter();
        parameter.setId(RandomStringUtils.randomAlphabetic(10));
        parameter.setName(property.getName());
        parameter.setType(property.getType());
        parameter.setDescription(property.getDescription());
        parameter.setRequired(property.getRequired());
        parameter.setExample(property.getExample());
        return parameter;
    }

    private Schema buildSchema(Property p) {
        Schema schema = new Schema();
        schema.setType(p.getType());
        schema.setDescription(p.getDescription());
        schema.setExample(p.getExample());
        String mock = p.getMock();
        if (mock != null && !mock.isEmpty()) {
            schema.setMock(new Mock(mock));
        }
        if (p.isArrayType()) {
            schema.setMinItems(p.getMinLength());
            schema.setMaxItems(p.getMaxLength());
            schema.setUniqueItems(p.getUniqueItems());
        } else if (p.isObjectType()) {
            schema.setMinProperties(p.getMinLength());
            schema.setMaxProperties(p.getMaxLength());
        } else if (p.isStringType()) {
            schema.setMinLength(p.getMinLength());
            schema.setMaxLength(p.getMaxLength());
        } else if (p.isNumberOrIntegerType()) {
            schema.setMinimum(p.getMinimum());
            schema.setMaximum(p.getMaximum());
        }

        // 特殊类型转换
        switch (p.getType()) {
            case "datetime":
                schema.setType("string");
                schema.setFormat("date-time");
                break;
            case "file":
                schema.setType("string");
                schema.setFormat("binary");
                break;
        }

        if (p.getProperties() != null) {
            List<String> required = p.getProperties().entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().getRequired() == Boolean.TRUE).map(Entry::getKey).collect(Collectors.toList());
            schema.setRequired(required);

            for (Entry<String, Property> entry : p.getProperties().entrySet()) {
                Schema propertySchema = buildSchema(entry.getValue());
                schema.addProperty(entry.getKey(), propertySchema);
            }
        }

        if (p.getItems() != null) {
            schema.setItems(buildSchema(p.getItems()));
        }

        return schema;
    }
}
