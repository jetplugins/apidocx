package io.yapix.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 参数
 */
@Data
public class Property {

    /** 名称 */
    private String name;

    /** 类型 */
    private String type;

    /** 时间格式 */
    private String dateFormat;

    /** 描述 */
    private String description;

    /** 参数位置 */
    private ParameterIn in;

    /** 是否必须 */
    private Boolean required;

    /** 是否标记过期 */
    private Boolean deprecated;

    /** 请求示例 */
    private String example;

    /** 响应mock */
    private String mock;

    /** 默认值 */
    private String defaultValue;

    /**
     * 值列表
     */
    private List<Value> values;

    /** 当type为array */
    private Property items;

    /** 当type为array, item元素是否唯一 */
    private Boolean uniqueItems;

    /** 当type为array, 最小元素个数 */
    private Integer minItems;

    /** 当type为array, 最大元素个数 */
    private Integer maxItems;

    /** 当type为object */
    private Map<String, Property> properties;

    public boolean isArrayType() {
        return DataTypes.ARRAY.equals(type);
    }

    public boolean isObjectType() {
        return DataTypes.OBJECT.equals(type);
    }

    /**
     * 获取类型名称, 包括数组
     */
    public String getTypeWithArray() {
        if (!DataTypes.ARRAY.equals(this.type)) {
            return this.type;
        }
        if (this.items == null) {
            return DataTypes.OBJECT + "[]";
        }
        return this.items.type + "[]";
    }

    /**
     * 合并自定义配置, 自定义优先
     */
    public void mergeCustom(Property custom) {
        if (StringUtils.isNotEmpty(custom.getName())) {
            this.name = custom.getName();
        }
        if (StringUtils.isNotEmpty(custom.getType())) {
            this.type = custom.getType();
        }
        if (StringUtils.isNotEmpty(custom.getDescription())) {
            this.description = custom.getDescription();
        }
        if (custom.getRequired() != null) {
            this.required = custom.getRequired();
        }
        if (custom.getDeprecated() != null) {
            this.deprecated = custom.getDeprecated();
        }
        if (StringUtils.isNotEmpty(custom.getDefaultValue())) {
            this.defaultValue = custom.getDefaultValue();
        }
        if (StringUtils.isNotEmpty(custom.getExample())) {
            this.example = custom.getExample();
        }
        if (StringUtils.isNotEmpty(custom.getMock())) {
            this.mock = custom.getMock();
        }
        if (custom.getMaxItems() != null){
            this.maxItems = custom.getMaxItems();
        }
        if (custom.getMinItems() != null){
            this.minItems = custom.getMinItems();
        }
        if (custom.getUniqueItems() != null){
            this.uniqueItems = custom.getUniqueItems();
        }
    }

    /**
     * 获取可能的值
     */
    public List<String> getValueList() {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream().map(Value::getValue).collect(Collectors.toList());
    }

    public List<Value> getPropertyValues() {
        Property item = this;
        List<Value> values = item.getValues();
        boolean isArrayEnum = DataTypes.ARRAY.equals(item.getType())
                && item.getItems() != null
                && CollectionUtils.isNotEmpty(item.getItems().getValues());
        if (isArrayEnum) {
            values = item.getItems().getValues();
        }
        return values;
    }

    public void addProperty(String key, Property value) {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }
        this.properties.put(key, value);
    }

}
