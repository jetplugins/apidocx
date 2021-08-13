package com.github.jetplugins.yapix.parse.model;

import com.github.jetplugins.yapix.model.Item;
import com.github.jetplugins.yapix.model.RequestBodyType;
import java.util.List;

/**
 * 请求参数信息
 */
public class RequestParseInfo {

    private List<Item> parameters;
    private RequestBodyType requestBodyType;
    private Item requestBody;
    private List<Item> requestBodyForm;

    public List<Item> getParameters() {
        return parameters;
    }

    public void setParameters(List<Item> parameters) {
        this.parameters = parameters;
    }

    public RequestBodyType getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public Item getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Item requestBody) {
        this.requestBody = requestBody;
    }

    public List<Item> getRequestBodyForm() {
        return requestBodyForm;
    }

    public void setRequestBodyForm(List<Item> requestBodyForm) {
        this.requestBodyForm = requestBodyForm;
    }
}
