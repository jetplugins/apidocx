package com.github.jetplugins.yapix.parse;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.trim;

import com.github.jetplugins.yapix.constant.SwaggerConstants;
import com.github.jetplugins.yapix.model.DocTags;
import com.github.jetplugins.yapix.util.DesUtil;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * 解析辅助工具类
 */
public class ParseHelper {

    private ParseHelper() {
    }

    /**
     * 获取文档分类
     */
    public static String apiCategory(PsiClass controller) {
        // 优先级: 文档注释标记@menu > 文档注释第一行 > 类名按照羊肉串风格命名
        String category = null;
        PsiDocComment comment = controller.getDocComment();
        if (comment != null) {
            PsiDocTag categoryTag = comment.findTagByName(DocTags.Category);
            if (categoryTag != null) {
                category = categoryTag.getText().trim();
            }

            if (StringUtils.isEmpty(category)) {
                category = Arrays.stream(comment.getDescriptionElements())
                        .filter(o -> o instanceof PsiDocToken)
                        .map(PsiElement::getText)
                        .findFirst()
                        .map(String::trim)
                        .orElse(null);
            }
        }
        if (StringUtils.isEmpty(category)) {
            category = DesUtil.camelToLine(controller.getName(), null);
        }
        return category;
    }

    /**
     * 获取文档标题
     */
    public static String apiSummary(PsiMethod method) {
        // 优先级: swagger注解@ApiOperation > 文档注释标记@description >  文档注释第一行
        String summary = null;

        PsiAnnotation apiOptAnnotation = method.getAnnotation(SwaggerConstants.API_OPERATION);
        if (apiOptAnnotation != null) {
            summary = AnnotationUtil.getStringAttributeValue(apiOptAnnotation);
        }

        PsiDocComment comment = method.getDocComment();
        if (comment != null) {
            if (StringUtils.isEmpty(summary)) {
                PsiDocTag descriptionTag = comment.findTagByName(DocTags.Description);
                if (descriptionTag != null) {
                    summary = descriptionTag.getText();
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

    public static String apiDescription(PsiMethod method) {
        String description = method.getText();
        if (method.getBody() != null) {
            description = description.replace(method.getBody().getText(), "");
        }
        description = description.replace("<", "&lt;").replace(">", "&gt;");
        return "   <pre><code>    " + description + "</code></pre>";
    }

    /**
     * 是否标记过期
     */
    public static boolean isDeprecated(PsiMethod method) {
        PsiAnnotation annotation = method.getAnnotation("java.lang.Deprecated");
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = findTagByName(method, DocTags.Deprecated);
        return nonNull(deprecatedTag);
    }

    public static String getDataType(PsiType type) {
        return TypeUtils.getType(type);
    }

    /**
     * 获取文档注释上的标记
     */
    private static PsiDocTag findTagByName(PsiJavaDocumentedElement element, String tagName) {
        PsiDocComment comment = element.getDocComment();
        if (comment != null) {
            return comment.findTagByName(tagName);
        }
        return null;
    }
}
