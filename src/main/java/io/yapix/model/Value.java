package io.yapix.model;

import org.apache.commons.lang3.StringUtils;

public class Value {

    /** 值 */
    private String value;

    /** 描述 */
    private String description;

    /**
     * 获取描述文本, 例如： READ: 红色
     */
    public String getText() {
        if (StringUtils.isEmpty(description)) {
            return value;
        }
        return value + ": " + description;
    }

    //----------------- generated --------------------//

    public Value(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
