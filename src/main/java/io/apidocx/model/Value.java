package io.apidocx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@AllArgsConstructor
public class Value {

    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
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

}
