package io.yapix.config;

import io.yapix.model.Property;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;

/**
 * 自定义bean信息
 */
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

    //---------------generated------------------//

    public Set<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public Map<String, Property> getFields() {
        return fields;
    }

    public void setFields(Map<String, Property> fields) {
        this.fields = fields;
    }
}
