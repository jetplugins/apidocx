package com.github.jetplugins.yapix.model;

import java.util.List;

/**
 * 接口信息
 */
public class Api {

    /** 路径 */
    private String path;

    /** 请求方法 */
    private String method;

    /** 概述标题 */
    private String summary;

    /** 描述 */
    private String description;

    /** 是否标记过期 */
    private Boolean deprecated;

    /** 参数 */
    private List<Item> parameters;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /** 请求体参数 */
    private Item requestBody;

    /** 响应体 */
    private Item responses;

    /** 分类 */
    private String category;

    //--------------generated----------------//

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public List<Item> getParameters() {
        return parameters;
    }

    public void setParameters(List<Item> parameters) {
        this.parameters = parameters;
    }

    public String getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(String requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public Item getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Item requestBody) {
        this.requestBody = requestBody;
    }

    public Item getResponses() {
        return responses;
    }

    public void setResponses(Item responses) {
        this.responses = responses;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
