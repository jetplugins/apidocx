package io.yapix.base.sdk.yapi.mode;

import java.util.List;
import java.util.Map;

/**
 * 参数
 */
public class YapiItem {

    /** 类型 */
    private String type;

    /** 描述 */
    private String description;

    /** 是否必须 */
    private List<String> required;

    /** 当type为object */
    private Map<String, YapiItem> properties;

    /** 当type为array */
    private YapiItem items;

    /** 响应mock */
    private YapiMock mock;

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

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public Map<String, YapiItem> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, YapiItem> properties) {
        this.properties = properties;
    }

    public YapiItem getItems() {
        return items;
    }

    public void setItems(YapiItem items) {
        this.items = items;
    }

    public YapiMock getMock() {
        return mock;
    }

    public void setMock(YapiMock mock) {
        this.mock = mock;
    }
}


