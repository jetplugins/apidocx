package io.yapix.parse.parser;

import com.intellij.psi.PsiType;
import io.yapix.model.DataTypes;
import io.yapix.parse.util.PropertiesLoader;
import io.yapix.parse.util.PsiTypeUtils;
import java.util.Properties;

/**
 * 字段类型工具类.
 */
public final class DataTypeParser {

    private static final String FILE = "types.properties";

    private DataTypeParser() {
    }

    /**
     * 获取字段类型
     */
    public static String parseType(PsiType type) {
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type)) {
            return DataTypes.ARRAY;
        }
        boolean isEnum = PsiTypeUtils.isEnum(type);
        if (isEnum) {
            return DataTypes.STRING;
        }
        Properties typeProperties = PropertiesLoader.getProperties(FILE);
        return typeProperties.getProperty(type.getCanonicalText(), DataTypes.OBJECT);
    }

}
