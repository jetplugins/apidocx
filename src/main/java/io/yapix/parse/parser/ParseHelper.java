package io.yapix.parse.parser;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.trim;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import io.yapix.model.Value;
import io.yapix.parse.constant.DocumentTags;
import io.yapix.parse.constant.JavaConstants;
import io.yapix.parse.constant.SpringConstants;
import io.yapix.parse.util.PsiAnnotationUtils;
import io.yapix.parse.util.PsiDocCommentUtils;
import io.yapix.parse.util.PsiLinkUtils;
import io.yapix.parse.util.PsiSwaggerUtils;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.StringUtilsExt;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 解析辅助工具类
 */
public class ParseHelper {

    private final Project project;
    private final Module module;

    public ParseHelper(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    //----------------------- 接口Api相关 ------------------------------------//

    /**
     * 获取接口分类
     */
    public String getDeclareApiCategory(PsiClass psiClass) {
        // 优先级: 文档注释标记@menu > @Api > 文档注释第一行
        String category = PsiDocCommentUtils.getDocCommentTagText(psiClass, DocumentTags.Category);
        if (StringUtils.isEmpty(category)) {
            category = PsiSwaggerUtils.getApiCategory(psiClass);
        }
        if (StringUtils.isEmpty(category)) {
            category = PsiDocCommentUtils.getDocCommentTitle(psiClass);
        }
        return category;
    }

    public String getDefaultApiCategory(PsiClass psiClass) {
        return StringUtilsExt.camelToLine(psiClass.getName(), null);
    }

    /**
     * 获取接口概述
     */
    public String getApiSummary(PsiMethod psiMethod) {
        // 优先级: swagger注解@ApiOperation > 文档注释标记@description >  文档注释第一行
        String summary = PsiSwaggerUtils.getApiSummary(psiMethod);

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
    public String getApiDescription(PsiMethod psiMethod) {
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
    public boolean getApiDeprecated(PsiMethod method) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(method, JavaConstants.Deprecate);
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
    public String getParameterDescription(PsiParameter parameter, Map<String, String> paramTagMap,
            List<Value> values) {
        // @ApiParam > @param
        String summary = PsiSwaggerUtils.getParameterDescription(parameter);
        if (StringUtils.isEmpty(summary)) {
            summary = paramTagMap.get(parameter.getName());
        }
        if (values != null && !values.isEmpty()) {
            String valuesText = values.stream().map(Value::getText).collect(Collectors.joining(", "));
            if (StringUtils.isEmpty(summary)) {
                summary = valuesText;
            } else {
                summary += " (" + valuesText + ")";
            }
        }
        return trim(summary);
    }

    /**
     * 获取参数是否必填
     */
    public Boolean getParameterRequired(PsiParameter parameter) {
        String[] annotations = {JavaConstants.NotNull, JavaConstants.NotBlank, JavaConstants.NotEmpty};
        for (String annotation : annotations) {
            PsiAnnotation target = PsiAnnotationUtils.getAnnotation(parameter, annotation);
            if (target != null) {
                return true;
            }
        }
        return null;
    }

    /**
     * 获取参数可能的值
     */
    public List<Value> getParameterValues(PsiParameter parameter) {
        return getTypeValues(parameter.getType());
    }

    /**
     * 获取枚举值列表
     */
    public List<Value> getEnumValues(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields())
                .filter(field -> field instanceof PsiEnumConstant)
                .map(field -> {
                    String name = field.getName();
                    String description = PsiDocCommentUtils.getDocCommentTitle(field);
                    return new Value(name, description);
                })
                .collect(Collectors.toList());
    }

    //---------------------- 字段相关 ------------------------------//

    /**
     * 获取字段名
     */
    public String getFieldName(PsiField field) {
        String property = PsiAnnotationUtils.getStringAttributeValue(field, SpringConstants.JsonProperty);
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        return field.getName();
    }

    /**
     * 获取字段描述
     */
    public String getFieldDescription(PsiField field, List<Value> values) {
        // 优先级: @ApiModelProperty > 文档注释标记@description >  文档注释第一行
        String summary = PsiSwaggerUtils.getFieldDescription(field);

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
        if (values != null && !values.isEmpty()) {
            String valuesText = values.stream().map(Value::getText).collect(Collectors.joining(", "));
            if (StringUtils.isEmpty(summary)) {
                summary = valuesText;
            } else {
                summary += " (" + valuesText + ")";
            }
        } else {
            summary = PsiLinkUtils.getLinkRemark(summary != null ? summary : "", field);
        }

        return trim(summary);
    }

    /**
     * 字段是否必填
     */
    public boolean getFieldRequired(PsiField field) {
        String[] annotations = {JavaConstants.NotNull, JavaConstants.NotBlank, JavaConstants.NotEmpty};
        for (String annotation : annotations) {
            PsiAnnotation target = PsiAnnotationUtils.getAnnotation(field, annotation);
            if (target != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字段可能的值
     */
    public List<Value> getFieldValues(PsiField field) {
        return getTypeValues(field.getType());
    }

    /**
     * 是否标记过期
     */
    public boolean getFieldDeprecated(PsiField field) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(field, JavaConstants.Deprecate);
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(field, DocumentTags.Deprecated);
        return nonNull(deprecatedTag);
    }


    /**
     * 字段是否被跳过
     */
    public boolean isFieldIgnore(PsiField field) {
        // swagger -> @ignore
        if (PsiSwaggerUtils.isFieldIgnore(field)) {
            return true;
        }
        PsiDocTag ignoreTag = PsiDocCommentUtils.findTagByName(field, DocumentTags.Ignore);
        return ignoreTag != null;
    }

    //----------------------------- 类型 -----------------------------//
    public String getTypeDescription(PsiType type, List<Value> values) {
        if (values != null && !values.isEmpty()) {
            return values.stream().map(Value::getText).collect(Collectors.joining(", "));
        } else if (type != null) {
            return type.getPresentableText();
        }
        return null;
    }

    /**
     * 获取指定类型可能的值
     */
    public List<Value> getTypeValues(PsiType psiType) {
        boolean isEnum = PsiTypeUtils.isEnum(psiType);
        if (isEnum) {
            PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(this.project, this.module, psiType);
            if (enumPsiClass != null) {
                return getEnumValues(enumPsiClass);
            }
        }
        return null;
    }
}
