package com.github.jetplugins.yapix.dto;

/**
 * header
 *
 * @author chengsheng@qbb6.com
 * @date 2019/5/9 10:11 PM
 */
public class YapiHeaderDTO {

    /**
     * 是否必填
     */
    private String required = "0";

    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private String value;
    /**
     * 描述
     */
    private String desc;
    /**
     * 示例
     */
    private String example;

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
