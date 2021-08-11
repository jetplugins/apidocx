package com.github.jetplugins.yapix.parse;

import com.github.jetplugins.yapix.model.DataTypes;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 字段类型工具类.
 */
public final class TypeUtils {

    private static volatile Properties typeProperties = null;

    private TypeUtils() {
    }

    /**
     * 是否是数组类型
     */
    public static boolean isArray(PsiType type) {
        return type instanceof PsiArrayType;
    }

    /**
     * 是否是集合类型或其子类型
     */
    public static boolean isCollection(PsiType type) {
        return isTargetType(type, CommonConstants.COLLECTION_CLASS);
    }

    /**
     * 是否是Map，以及其子类型
     *
     * @param type
     * @return
     */
    public static boolean isMap(PsiType type) {
        return isTargetType(type, CommonConstants.MAP_CLASS);
    }

    /**
     * 是否是枚举类型
     */
    public static boolean isEnum(PsiType type) {
        return isTargetType(type, CommonConstants.ENUM_CLASS);
    }

    /**
     * 是否是集合类型
     */
    private static boolean isTargetType(PsiType type, String targetType) {
        String canonicalText = type.getCanonicalText();
        if (canonicalText.equals(CommonConstants.OBJECT_CLASS)) {
            return false;
        }
        if (canonicalText.startsWith(targetType)) {
            return true;
        }
        PsiType[] superTypes = type.getSuperTypes();
        for (PsiType superType : superTypes) {
            if (isTargetType(superType, targetType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字段类型
     */
    public static String getType(PsiType type) {
        // 数组类型处理
        if (TypeUtils.isArray(type) || TypeUtils.isCollection(type)) {
            return DataTypes.ARRAY;
        }
        return getSimpleType(type);
    }

    /**
     * 获取定义的简单类型(非数组，非嵌套对象)
     */
    public static String getSimpleType(PsiType type) {
        boolean isEnum = TypeUtils.isEnum(type);
        if (isEnum) {
            return CommonConstants.TYPE_NAME_ENUM;
        }
        Properties typeProperties = getTypeProperties();
        return typeProperties.getProperty(type.getCanonicalText(), CommonConstants.TYPE_NAME_OBJECT);
    }


    private static Properties getTypeProperties() {
        if (typeProperties == null) {
            synchronized (TypeUtils.class) {
                InputStream is = TypeUtils.class.getClassLoader()
                        .getResourceAsStream(CommonConstants.FIELD_TYPE_PROPERTIES_FILE);
                Properties properties = new Properties();
                try {
                    properties.load(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TypeUtils.typeProperties = properties;
            }
        }
        return typeProperties;
    }

}
