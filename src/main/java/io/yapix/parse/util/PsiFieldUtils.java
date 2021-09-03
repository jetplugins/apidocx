package io.yapix.parse.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;

public class PsiFieldUtils {

    private PsiFieldUtils() {
    }

    /**
     * 获取字段默认值
     */
    public static String getFieldDefaultValue(PsiField field) {
        PsiExpression initializer = field.getInitializer();
        if (initializer == null) {
            return null;
        }
        PsiReference reference = initializer.getReference();
        if (reference == null) {
            Object constant = JavaConstantExpressionEvaluator.computeConstantExpression(initializer, false);
            return constant == null ? null : constant.toString();
        }
        PsiElement resolve = reference.resolve();
        if (resolve instanceof PsiEnumConstant) {
            // 枚举常量
            return ((PsiEnumConstant) resolve).getName();
        } else if (resolve instanceof PsiField) {
            // 引用其他字段
            return getFieldDefaultValue((PsiField) resolve);
        }
        return null;
    }

}
