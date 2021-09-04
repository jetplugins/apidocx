package io.yapix.parse.parser;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.yapix.base.util.JsonUtils;
import io.yapix.config.MockRule;
import io.yapix.config.YapixConfig;
import io.yapix.model.DataTypes;
import io.yapix.model.Property;
import io.yapix.parse.constant.DocumentTags;
import io.yapix.parse.util.PropertiesLoader;
import io.yapix.parse.util.PsiDocCommentUtils;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.PsiUtils;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * Mock解析
 */
public class MockParser {

    private static final String FILE = "mocks.properties";

    private final YapixConfig settings;

    public MockParser(YapixConfig settings) {
        this.settings = settings;
    }

    /**
     * 获取字段类型
     */
    public String parseMock(Property property, PsiType type, PsiField field, String filedName) {
        // 自定义标记
        if (field != null) {
            String mock = PsiDocCommentUtils.getTagText(field, DocumentTags.Mock);
            if (StringUtils.isNotEmpty(mock)) {
                return mock;
            }
        }

        // 时间类型
        if (DateParser.isDateType(type)) {
            String mock = "@integer(1210573684000, 1896710400000)";
            if (DataTypes.STRING.equals(property.getType())) {
                mock = "@datetime";
                if (StringUtils.isNotEmpty(property.getDateFormat())) {
                    mock = String.format("@datetime(\"%s\")", property.getDateFormat());
                }
            }
            return mock;
        }

        // 枚举类型
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        if (psiClass != null && psiClass.isEnum()) {
            return "@pick(" + JsonUtils.toJson(PsiUtils.getEnumFieldNames(psiClass)) + ")";
        }
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type)) {
            return null;
        }

        // 自定义规则
        String mock = matchRulesMock(property.getType(), filedName);
        if (StringUtils.isNotEmpty(mock)) {
            return mock;
        }

        // 规定规则
        Properties properties = PropertiesLoader.getProperties(FILE);
        return properties.getProperty(type.getCanonicalText());
    }

    /**
     * 匹配自定义mock规则
     */
    private String matchRulesMock(String type, String filedName) {
        if (settings.getMockRules() == null) {
            return null;
        }
        for (MockRule rule : settings.getMockRules()) {
            if (rule != null && rule.match(type, filedName)) {
                return rule.getMock();
            }
        }
        return null;
    }

}
