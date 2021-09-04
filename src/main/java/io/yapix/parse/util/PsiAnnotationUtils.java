package io.yapix.parse.util;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiModifierListOwner;

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
     * 获取指定元素注解的某个属性值
     */
    public static String getAnnotationStringAttributeValue(PsiModifierListOwner element, String fqn, String attribute) {
        PsiAnnotation annotation = getAnnotation(element, fqn);
        if (annotation == null) {
            return null;
        }
        return AnnotationUtil.getStringAttributeValue(annotation, attribute);
    }

}
