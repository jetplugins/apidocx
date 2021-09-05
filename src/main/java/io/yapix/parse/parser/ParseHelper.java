package io.yapix.parse.parser;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.trim;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import io.yapix.parse.constant.DocumentTags;
import io.yapix.parse.constant.JavaConstants;
import io.yapix.parse.constant.SpringConstants;
import io.yapix.parse.constant.SwaggerConstants;
import io.yapix.parse.util.PsiDocCommentUtils;
import io.yapix.parse.util.PsiLinkUtils;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.StringUtilsExt;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 解析辅助工具类
 */
public class ParseHelper {

    private ParseHelper() {
    }

    //----------------------- 接口Api相关 ------------------------------------//

    /**
     * 获取接口分类
     */
    public static String getApiCategory(PsiClass psiClass) {
        // 优先级: 文档注释标记@menu > 文档注释第一行 > 类名按照羊肉串风格命名
        String category = PsiDocCommentUtils.getDocCommentTagText(psiClass, DocumentTags.Category);
        if (StringUtils.isEmpty(category)) {
            category = PsiDocCommentUtils.getDocCommentTitle(psiClass);
        }
        if (StringUtils.isEmpty(category)) {
            category = StringUtilsExt.camelToLine(psiClass.getName(), null);
        }
        return category;
    }

    /**
     * 获取接口概述
     */
    public static String getApiSummary(PsiMethod psiMethod) {
        // 优先级: swagger注解@ApiOperation > 文档注释标记@description >  文档注释第一行
        String summary = null;

        PsiAnnotation apiOptAnnotation = psiMethod.getAnnotation(SwaggerConstants.ApiOperation);
        if (apiOptAnnotation != null) {
            summary = AnnotationUtil.getStringAttributeValue(apiOptAnnotation);
        }

        PsiDocComment comment = psiMethod.getDocComment();
        if (comment != null) {
            if (StringUtils.isEmpty(summary)) {
                String[] tags = {DocumentTags.Description, DocumentTags.DescriptionYapiUpload};
                for (String tag : tags) {
                    summary = PsiDocCommentUtils.getDocCommentTagText(psiMethod, tag);
                    if (StringUtils.isNotEmpty(summary)) {
                        break;
                    }
                }
            }

            if (StringUtils.isEmpty(summary)) {
                summary = Arrays.stream(comment.getDescriptionElements())
                        .filter(o -> o instanceof PsiDocToken)
                        .map(PsiElement::getText)
                        .findFirst()
                        .map(String::trim)
                        .orElse(null);
            }
        }
        return trim(summary);
    }

    /**
     * 获取接口描述
     */
    public static String getApiDescription(PsiMethod psiMethod) {
        String description = psiMethod.getText();
        if (psiMethod.getBody() != null) {
            description = description.replace(psiMethod.getBody().getText(), "");
        }
        description = description.replace("<", "&lt;").replace(">", "&gt;");
        return "   <pre><code>    " + description + "</code></pre>";
    }

    /**
     * 获取接口是否标记过期
     */
    public static boolean getApiDeprecated(PsiMethod method) {
        PsiAnnotation annotation = method.getAnnotation(JavaConstants.Deprecate);
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(method, DocumentTags.Deprecated);
        return nonNull(deprecatedTag);
    }

    //------------------------ 参数Parameter ----------------------------//

    /**
     * 获取参数描述
     */
    public static String getParameterDescription(PsiParameter parameter, Map<String, String> paramTagMap) {
        String summary = paramTagMap.get(parameter.getName());
        // 枚举
        PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(parameter.getProject(), parameter.getType());
        if (enumPsiClass != null) {
            if (StringUtils.isEmpty(summary)) {
                summary = getEnumConstantsDescription(enumPsiClass);
            } else {
                summary += (": " + getEnumConstantsDescription(enumPsiClass));
            }
        }
        return trim(summary);
    }

    /**
     * 获取参数是否必填
     */
    public static Boolean getParameterRequired(PsiParameter parameter) {
        String[] annotations = {JavaConstants.NotNull, JavaConstants.NotBlank, JavaConstants.NotEmpty};
        for (String annotation : annotations) {
            PsiAnnotation target = parameter.getAnnotation(annotation);
            if (target != null) {
                return true;
            }
        }
        return null;
    }

    /**
     * 获取枚举类常量描述, 格式: 字段名(xxx),字段名(xxx),
     */
    public static String getEnumConstantsDescription(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder();
        for (PsiField field : psiClass.getFields()) {
            if (field instanceof PsiEnumConstant) {
                sb.append(field.getName());
                String description = PsiDocCommentUtils.getDocCommentTitle(field);
                if (StringUtils.isNotEmpty(description)) {
                    sb.append("(").append(description).append(")");
                }
                sb.append(", ");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    //---------------------- 字段相关 ------------------------------//

    /**
     * 获取字段名
     */
    public static String getFieldName(PsiField field) {
        PsiAnnotation jsonProperty = field.getAnnotation(SpringConstants.JsonProperty);
        if (jsonProperty != null) {
            String property = AnnotationUtil.getStringAttributeValue(jsonProperty, "value");
            if (StringUtils.isNotBlank(property)) {
                return property;
            }
        }
        return field.getName();
    }

    /**
     * 获取字段描述
     */
    public static String getFieldDescription(PsiField field) {
        // 优先级: swagger注解@ApiParam > 文档注释标记@description >  文档注释第一行
        String summary = null;

        PsiAnnotation swaggerAnnotation = field.getAnnotation(SwaggerConstants.ApiParam);
        if (swaggerAnnotation != null) {
            summary = AnnotationUtil.getStringAttributeValue(swaggerAnnotation);
        }

        PsiDocComment comment = field.getDocComment();
        if (comment != null) {
            if (StringUtils.isEmpty(summary)) {
                summary = Arrays.stream(comment.getDescriptionElements())
                        .filter(o -> o instanceof PsiDocToken)
                        .map(PsiElement::getText)
                        .findFirst()
                        .map(String::trim)
                        .orElse(null);
            }
        }
        // 枚举
        PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(field.getProject(), field.getType());
        if (enumPsiClass != null) {
            if (StringUtils.isEmpty(summary)) {
                summary = getEnumConstantsDescription(enumPsiClass);
            } else {
                summary += (": " + getEnumConstantsDescription(enumPsiClass));
            }
        }

        // @link
        if (enumPsiClass == null) {
            summary = PsiLinkUtils.getLinkRemark(summary != null ? summary : "", field);
        }
        return trim(summary);
    }

    /**
     * 字段是否必填
     */
    public static boolean getFieldRequired(PsiField field) {
        String[] annotations = {JavaConstants.NotNull, JavaConstants.NotBlank, JavaConstants.NotEmpty};
        for (String annotation : annotations) {
            PsiAnnotation target = field.getAnnotation(annotation);
            if (target != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否标记过期
     */
    public static boolean getFieldDeprecated(PsiField field) {
        PsiAnnotation annotation = field.getAnnotation(JavaConstants.Deprecate);
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(field, DocumentTags.Deprecated);
        return nonNull(deprecatedTag);
    }
}
