package com.github.jetplugins.yapix.parse.parser;

import com.github.jetplugins.yapix.parse.util.PropertiesLoader;
import com.github.jetplugins.yapix.parse.util.PsiTypeUtils;
import com.intellij.psi.PsiType;
import java.util.Properties;

/**
 * Mock解析工具
 */
public final class MockParser {

    private static final String FILE = "mocks.properties";

    private MockParser() {
    }

    /**
     * 获取字段类型
     */
    public static String parseMock(PsiType type) {
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type)) {
            return null;
        }
        boolean isEnum = PsiTypeUtils.isEnum(type);
        if (isEnum) {
            return "@string";
        }
        Properties properties = PropertiesLoader.getProperties(FILE);
        return properties.getProperty(type.getCanonicalText());
    }

}
