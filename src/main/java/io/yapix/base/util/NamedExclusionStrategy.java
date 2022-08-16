package io.yapix.base.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.Set;

/**
 * 按照字段名称过滤的策略
 */
public class NamedExclusionStrategy implements ExclusionStrategy {

    private final Set<String> skipFields;

    public NamedExclusionStrategy(Set<String> skipFields) {
        this.skipFields = skipFields;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return skipFields.contains(fieldAttributes.getName());
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
