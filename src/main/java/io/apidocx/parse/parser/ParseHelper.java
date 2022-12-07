package io.apidocx.parse.parser;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.trim;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.javadoc.PsiDocMethodOrFieldRef;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import io.apidocx.model.Value;
import io.apidocx.parse.constant.DocumentTags;
import io.apidocx.parse.constant.JavaConstants;
import io.apidocx.parse.constant.SpringConstants;
import io.apidocx.parse.model.Jsr303Info;
import io.apidocx.parse.model.TypeParseContext;
import io.apidocx.parse.util.InternalUtils;
import io.apidocx.parse.util.PsiAnnotationUtils;
import io.apidocx.parse.util.PsiDocCommentUtils;
import io.apidocx.parse.util.PsiFieldUtils;
import io.apidocx.parse.util.PsiSwaggerUtils;
import io.apidocx.parse.util.PsiTypeUtils;
import io.apidocx.parse.util.PsiUtils;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;
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
        // 优先级: 文档注释标记@module > @menu > @Api > 文档注释第一行
        String category = PsiDocCommentUtils.getDocCommentTagText(psiClass, DocumentTags.Module);
        if (StringUtils.isEmpty(category)) {
            category = PsiDocCommentUtils.getDocCommentTagText(psiClass, DocumentTags.Category);
        }
        if (StringUtils.isEmpty(category)) {
            category = PsiSwaggerUtils.getApiCategory(psiClass);
        }
        if (StringUtils.isEmpty(category)) {
            category = PsiDocCommentUtils.getDocCommentTitle(psiClass);
        }
        return category;
    }

    public String getDefaultApiCategory(PsiClass psiClass) {
        return InternalUtils.camelToLine(psiClass.getName(), null);
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
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(method, Deprecated.class.getName());
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(method, DocumentTags.Deprecated);
        return nonNull(deprecatedTag);
    }

    /**
     * 获取接口标签
     */
    public List<String> getApiTags(PsiMethod method) {
        String tagsContent = PsiDocCommentUtils.getDocCommentTagText(method, DocumentTags.Tags);
        if (tagsContent == null) {
            return Collections.emptyList();
        }
        List<String> tags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(tagsContent)
                .stream().distinct().collect(Collectors.toList());
        return tags;
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
        }

        return trim(summary);
    }

    /**
     * 字段是否必填
     */
    public boolean getFieldRequired(TypeParseContext context, PsiField field) {
        List<String> validateGroups = context.getJsr303ValidateGroups();
        String[] annotations = {JavaConstants.NotNull, JavaConstants.NotBlank, JavaConstants.NotEmpty};
        for (String annotation : annotations) {
            PsiAnnotation target = PsiAnnotationUtils.getAnnotation(field, annotation);
            if (target == null) {
                continue;
            }

            List<String> groups = PsiAnnotationUtils.getStringArrayAttribute(target, "groups");
            boolean validateJsr303 = CollectionUtils.isEmpty(validateGroups) || CollectionUtils.intersection(groups, validateGroups).size() > 0;
            if (validateJsr303) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字段可能的值
     */
    public List<Value> getFieldValues(PsiField field) {
        // 枚举类
        boolean isEnum = PsiTypeUtils.isEnum(field.getType());
        if (isEnum) {
            PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(this.project, this.module, field.getType());
            if (enumPsiClass != null) {
                return getEnumValues(enumPsiClass);
            }
        }

        List<Value> values = Lists.newArrayList();
        // 解析: @link文档标记
        if (field.getDocComment() != null) {
            PsiDocTag[] linkTags = Arrays.stream(field.getDocComment().getDescriptionElements())
                    .filter(e -> e instanceof PsiInlineDocTag)
                    .filter(e -> ((PsiInlineDocTag) e).getName().equals(DocumentTags.Link))
                    .toArray(PsiDocTag[]::new);
            for (PsiDocTag tag : linkTags) {
                List<Value> tagValues = doGetFieldValueByTag(tag);
                if (tagValues.size() > 0) {
                    values.addAll(tagValues);
                }
            }
        }

        // 解析: @see 文档标记
        PsiDocTag[] tags = PsiDocCommentUtils.findTagsByName(field, DocumentTags.See);
        for (PsiDocTag tag : tags) {
            List<Value> tagValues = doGetFieldValueByTag(tag);
            if (tagValues.size() > 0) {
                values.addAll(tagValues);
            }
        }
        return values.stream().filter(distinctByKey(Value::getValue)).collect(Collectors.toList());
    }

    private List<Value> doGetFieldValueByTag(PsiDocTag tag) {
        PsiElement[] elements = tag.getDataElements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }
        PsiElement targetElement = elements[0];
        if (tag instanceof PsiInlineDocTag) {
            PsiElement psiElement = Arrays.stream(elements)
                    .filter(e -> e instanceof LazyParseablePsiElement || e instanceof PsiDocMethodOrFieldRef)
                    .findFirst().orElse(null);
            if (psiElement != null) {
                targetElement = psiElement;
            }
        }

        // 找到引用的类和字段
        PsiClass psiClass = null;
        PsiReference psiReference = targetElement.getReference();
        PsiElement firstChild = targetElement.getFirstChild();
        if (firstChild != null && !(firstChild instanceof PsiJavaCodeReferenceElement)) {
            firstChild = firstChild.getFirstChild();
        }
        if ((firstChild instanceof PsiJavaCodeReferenceElement) && firstChild.getReference() != null) {
            psiClass = PsiUtils.findPsiClass(project, module, firstChild.getReference().getCanonicalText());
        }
        if (psiClass == null) {
            return Collections.emptyList();
        }

        // 枚举类
        boolean isEnum = psiClass.isEnum();
        if (isEnum) {
            if (psiReference == null) {
                return getEnumValues(psiClass);
            } else {
                String fieldName = psiReference.getCanonicalText();
                PsiField field = Arrays.stream(psiClass.getFields())
                        .filter(one -> one.getName().equals(fieldName))
                        .findFirst().orElse(null);
                if (field == null) {
                    return Collections.emptyList();
                }
                if (field instanceof PsiEnumConstant) {
                    return Lists.newArrayList(new Value(field.getName(), PsiDocCommentUtils.getDocCommentTitle(field)));
                }

                // 解析枚举表达式
                int fieldIndex = -1;
                for (PsiMethod constructor : psiClass.getConstructors()) {
                    JvmParameter[] parameters = constructor.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].getName().equals(fieldName)) {
                            fieldIndex = i;
                        }
                    }
                }
                if (fieldIndex == -1) {
                    return Collections.emptyList();
                }

                PsiField[] enumFields = PsiUtils.getEnumFields(psiClass);
                List<Value> values = Lists.newArrayListWithExpectedSize(enumFields.length);
                for (PsiField enumField : enumFields) {
                    PsiElement expressionList = Arrays.stream(enumField.getChildren())
                            .filter(e -> e instanceof PsiExpressionList)
                            .findFirst().orElse(null);
                    if (expressionList == null) {
                        continue;
                    }
                    PsiLiteralExpression[] psiLiteralExpressions = Arrays.stream(expressionList.getChildren())
                            .filter(e -> e instanceof PsiLiteralExpression)
                            .toArray(PsiLiteralExpression[]::new);
                    if (psiLiteralExpressions.length > fieldIndex) {
                        String value = psiLiteralExpressions[fieldIndex].getText();
                        String description = PsiDocCommentUtils.getDocCommentTitle(enumField);
                        if (StringUtils.isEmpty(description)) {
                            description = enumField.getName();
                        }
                        values.add(new Value(value, description));
                    }
                }
                return values;
            }
        }

        // 常量类
        PsiField[] fields = PsiUtils.getStaticOrFinalFields(psiClass);
        if (psiReference != null) {
            fields = Arrays.stream(fields)
                    .filter(f -> f.getName().equals(psiReference.getCanonicalText()))
                    .toArray(PsiField[]::new);
        }
        List<Value> values = Lists.newArrayListWithExpectedSize(fields.length);
        for (PsiField f : fields) {
            String value = PsiFieldUtils.getFieldDeclaredValue(f);
            if (value == null) {
                continue;
            }
            String description = PsiDocCommentUtils.getDocCommentTitle(f);
            values.add(new Value(value, description));
        }
        return values;
    }

    /**
     * 是否标记过期
     */
    public boolean getFieldDeprecated(PsiField field) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(field, Deprecated.class.getName());
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
        // swagger -> @ignore -> @JsonIgnore
        if (PsiSwaggerUtils.isFieldIgnore(field)) {
            return true;
        }

        PsiDocTag ignoreTag = PsiDocCommentUtils.findTagByName(field, DocumentTags.Ignore);
        if (ignoreTag != null) {
            return true;
        }

        String jsonIgnore = PsiAnnotationUtils.getStringAttributeValue(field, SpringConstants.JsonIgnore);
        if ("true".equals(jsonIgnore)) {
            return true;
        }
        return false;
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

    /**
     * 获取未忽略的字段
     */
    public List<PsiField> getFields(PsiClass psiClass) {
        PsiField[] fields = PsiUtils.getFields(psiClass);
        List<String> includeProperties = PsiAnnotationUtils.getStringArrayAttribute(psiClass, SpringConstants.JsonIncludeProperties, "value");
        if (includeProperties != null) {
            Set<String> includePropertiesSet = Sets.newHashSet(includeProperties);
            return Arrays.stream(fields)
                    .filter(filed -> includePropertiesSet.contains(filed.getName()))
                    .filter(field -> !isFieldIgnore(field))
                    .collect(Collectors.toList());
        }

        List<String> ignoreProperties = PsiAnnotationUtils.getStringArrayAttribute(psiClass, SpringConstants.JsonIgnoreProperties, "value");
        if (ignoreProperties != null && !ignoreProperties.isEmpty()) {
            Set<String> ignorePropertiesSet = Sets.newHashSet(ignoreProperties);
            return Arrays.stream(fields)
                    .filter(filed -> !ignorePropertiesSet.contains(filed.getName()))
                    .filter(field -> !isFieldIgnore(field))
                    .collect(Collectors.toList());
        }

        return Arrays.stream(fields)
                .filter(field -> !isFieldIgnore(field))
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Jsr303Info getJsr303Info(PsiModifierListOwner element) {
        Jsr303Info data = new Jsr303Info();
        // @Size
        PsiAnnotation sizeAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.Size);
        if (sizeAnnotation != null) {
            Integer minSize = PsiAnnotationUtils.getIntegerAttributeValueByAnnotation(sizeAnnotation, "min");
            Integer maxSize = PsiAnnotationUtils.getIntegerAttributeValueByAnnotation(sizeAnnotation, "max");
            data.setMinLength(minSize);
            data.setMaxLength(maxSize);
        }

        // @Min, @DecimalMin, @Positive, @PositiveOrZero
        BigDecimal minValue = null, decimalMinValue = null, positiveValue = null, positiveOrZeroValue = null;
        PsiAnnotation minAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.Min);
        if (minAnnotation != null) {
            minValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(minAnnotation, "value");
        }
        PsiAnnotation decimalMinAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.DecimalMin);
        if (decimalMinAnnotation != null) {
            decimalMinValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(decimalMinAnnotation, "value");
        }
        PsiAnnotation positiveAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.Positive);
        if (positiveAnnotation != null) {
            positiveValue = BigDecimal.ZERO;
        }
        PsiAnnotation positiveOrZeroAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.PositiveOrZero);
        if (positiveOrZeroAnnotation != null) {
            positiveOrZeroValue = BigDecimal.ZERO;
        }
        BigDecimal min = Stream.of(minValue, decimalMinValue, positiveValue, positiveOrZeroValue)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(null);
        data.setMinimum(min);

        // @Max, @DecimalMax, @Negative, @NegativeOrZero
        BigDecimal maxValue = null, decimalMaxValue = null, negativeValue = null, negativeOrZeroValue = null;
        PsiAnnotation maxAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.Max);
        if (maxAnnotation != null) {
            maxValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(maxAnnotation, "value");
        }
        PsiAnnotation decimalMaxAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.DecimalMax);
        if (decimalMaxAnnotation != null) {
            decimalMaxValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(decimalMaxAnnotation, "value");
        }
        PsiAnnotation negativeAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.Negative);
        if (negativeAnnotation != null) {
            negativeValue = BigDecimal.ZERO;
        }
        PsiAnnotation negativeOrZeroAnnotation = PsiAnnotationUtils.getAnnotation(element, JavaConstants.NegativeOrZero);
        if (negativeOrZeroAnnotation != null) {
            negativeOrZeroValue = BigDecimal.ZERO;
        }
        BigDecimal max = Stream.of(maxValue, decimalMaxValue, negativeValue, negativeOrZeroValue)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(null);
        data.setMaximum(max);

        return data;
    }

    public boolean isMethodIgnored(PsiMethod method) {
        return PsiDocCommentUtils.findTagByName(method, DocumentTags.Ignore) != null;
    }

    public boolean isClassIgnored(PsiClass psiClass) {
        return PsiDocCommentUtils.findTagByName(psiClass, DocumentTags.Ignore) != null;
    }
}
