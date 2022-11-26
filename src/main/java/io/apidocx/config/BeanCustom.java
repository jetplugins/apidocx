package io.apidocx.config;

import io.apidocx.model.Property;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

/**
 * 自定义bean信息
 */
@Data
public class BeanCustom {

    /**
     * 包含的字段名
     */
    private Set<String> includes;

    /**
     * 不包含的字段名, includes优先
     */
    private Set<String> excludes;

    /**
     * 字段信息配置
     */
    private Map<String, Property> fields;

    /**
     * 某个字段是否需要处理
     */
    public boolean isNeedHandleField(String fieldName) {
        if (CollectionUtils.isEmpty(includes) && CollectionUtils.isEmpty(excludes)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(includes)) {
            return includes.contains(fieldName);
        }
        return !excludes.contains(fieldName);
    }

    public Property getFieldProperty(String fieldName) {
        if (fields != null) {
            return fields.get(fieldName);
        }
        return null;
    }

}
