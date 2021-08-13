package com.github.jetplugins.yapix.model;

import java.util.Map;

/**
 * 参数
 */
public class Item {

    /** 名称 */
    private String name;

    /** 类型 */
    private String type;

    /** 描述 */
    private String description;

    /** 参数位置 */
    private ParameterIn in;

    /** 是否必须 */
    private boolean required;

    /** 是否标记过期 */
    private boolean deprecated;

    /** 当type为object */
    private Map<String, Item> properties;

    /** 当type为array */
    private Item items;

    /**
     * 请求示例
     */
    private String example;

    /** 响应mock */
    private String mock;

    /** 默认值 */
    private String defaultValue;

    public boolean isArrayType() {
        return DataTypes.ARRAY.equals(type);
    }

    public boolean isObjectType() {
        return DataTypes.OBJECT.equals(type);
    }

    //---------------------------generated-------------------------------//


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParameterIn getIn() {
        return in;
    }

    public void setIn(ParameterIn in) {
        this.in = in;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Map<String, Item> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Item> properties) {
        this.properties = properties;
    }

    public Item getItems() {
        return items;
    }

    public void setItems(Item items) {
        this.items = items;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
