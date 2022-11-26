package io.apidocx.parse.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import com.intellij.psi.util.PsiTypesUtil;

public class PsiFieldUtils {

    private PsiFieldUtils() {
    }

    /**
     * 获取字段声明的默认值
     */
    public static String getFieldDeclaredValue(PsiField field) {
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
            return getFieldDeclaredValue((PsiField) resolve);
        }
        return null;
    }

    /**
     * 获取字段默认值
     */
    public static String getFieldDefaultValue(PsiField field) {
        PsiExpression initializer = field.getInitializer();
        if (initializer != null) {
            return getFieldDeclaredValue(field);
        } else {
            PsiType fieldType = field.getType();
            // 可能解析不到从文本解析
            String dv = getDefaultValueFromFieldText(field);
            if (dv != null) {
                return dv;
            } else if (fieldType instanceof PsiPrimitiveType) {
                return PsiTypesUtil.getDefaultValueOfType(fieldType);
            }
            return null;
        }
    }

    private static String getDefaultValueFromFieldText(PsiField field) {
        String text = field.getText();
        int nameIdx = text.lastIndexOf(field.getName());
        text = text.substring(nameIdx + field.getName().length());
        // 从字段文本解析出默认值: private int page = 1;
        // 暂时不处理枚举类
        int eqIdx = text.lastIndexOf('=');
        if (eqIdx == -1) {
            return null;
        }

        String subText = text.substring(eqIdx + 1).trim();
        int beginIdx = 0, endIdx = subText.length();
        if (subText.endsWith(";")) {
            endIdx--;
        }
        if (subText.startsWith("\"")) {
            beginIdx++;
            endIdx--;
        }
        return subText.substring(beginIdx, endIdx);
    }

}
