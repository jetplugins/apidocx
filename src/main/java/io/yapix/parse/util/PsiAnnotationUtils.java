package io.yapix.parse.util;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import java.util.Collections;
import java.util.List;

/**
 * 注解相关工具类
 */
public class PsiAnnotationUtils {

    private PsiAnnotationUtils() {
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation getAnnotation(PsiModifierListOwner element, String fqn) {
        return element.getAnnotation(fqn);
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation getAnnotationIncludeExtends(PsiClass element, String fqn) {
        PsiAnnotation annotation = element.getAnnotation(fqn);
        if (annotation == null) {
            for (PsiClassType type : element.getExtendsListTypes()) {
                PsiClass psiClass = type.resolve();
                if (psiClass == null) {
                    continue;
                }
                annotation = psiClass.getAnnotation(fqn);
                if (annotation != null) {
                    break;
                }
            }
        }
        return annotation;
    }

    /**
     * 获取指定元素注解的value属性值
     */
    public static String getStringAttributeValue(PsiModifierListOwner element, String fqn) {
        return getStringAttributeValue(element, fqn, "value");
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValue(PsiModifierListOwner element, String fqn, String attribute) {
        PsiAnnotation annotation = getAnnotation(element, fqn);
        if (annotation == null) {
            return null;
        }
        return getStringAttributeValueByAnnotation(annotation, attribute);
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValueByAnnotation(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(attribute);
        if (attributeValue == null) {
            return null;
        }
        return getAnnotationMemberValue(attributeValue);
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValueByAnnotation(PsiAnnotation annotation) {
        return getStringAttributeValueByAnnotation(annotation, "value");
    }

    public static List<String> getStringArrayAttribute(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue memberValue = annotation.findAttributeValue(attribute);
        if (memberValue == null) {
            return Collections.emptyList();
        }

        List<String> paths = Lists.newArrayListWithExpectedSize(1);
        if (memberValue instanceof PsiArrayInitializerMemberValue) {
            PsiArrayInitializerMemberValue theMemberValue = (PsiArrayInitializerMemberValue) memberValue;
            PsiAnnotationMemberValue[] values = theMemberValue.getInitializers();
            for (PsiAnnotationMemberValue value : values) {
                String text = getAnnotationMemberValue(value);
                paths.add(text);
            }
        } else {
            String text = getAnnotationMemberValue(memberValue);
            paths.add(text);
        }
        return paths;
    }

    /**
     * 获取注解值
     */
    public static String getAnnotationMemberValue(PsiAnnotationMemberValue memberValue) {
        PsiReference reference = memberValue.getReference();
        if (memberValue instanceof PsiExpression) {
            Object constant = JavaConstantExpressionEvaluator
                    .computeConstantExpression((PsiExpression) memberValue, false);
            return constant == null ? null : constant.toString();
        }
        if (reference != null) {
            PsiElement resolve = reference.resolve();
            if (resolve instanceof PsiEnumConstant) {
                // 枚举常量
                return ((PsiEnumConstant) resolve).getName();
            } else if (resolve instanceof PsiField) {
                // 引用其他字段
                return PsiFieldUtils.getFieldDefaultValue((PsiField) resolve);
            }
        }
        return "";
    }

}
