package io.yapix.base.sdk.yapi.model;

/**
 * 表单参数
 */
public class YapiParameter {

    /** 参数名字 */
    private String name;

    /**
     * 类型
     */
    private String type;

    /** 描述 */
    private String desc;

    /** 是否必填 */
    private String required;

    /** 示例 */
    private String example;

    /** 值 */
    private String value;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof YapiParameter)) {
            return false;
        }

        YapiParameter that = (YapiParameter) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) {
            return false;
        }
        if (required != null ? !required.equals(that.required) : that.required != null) {
            return false;
        }
        if (example != null ? !example.equals(that.example) : that.example != null) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (example != null ? example.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
