package io.apidocx.parse.util;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import io.apidocx.parse.constant.SwaggerConstants;

/**
 * Swagger解析相关工具
 */
public class PsiSwaggerUtils {

    private PsiSwaggerUtils() {
    }

    public static String getApiCategory(PsiClass psiClass) {
        PsiAnnotation api = PsiAnnotationUtils.getAnnotation(psiClass, SwaggerConstants.Api);
        if (api == null) {
            return null;
        }
        return PsiAnnotationUtils.getStringAttributeValueByAnnotation(api, "tags");
    }

    public static String getApiSummary(PsiMethod psiMethod) {
        PsiAnnotation apiOperation = PsiAnnotationUtils.getAnnotation(psiMethod, SwaggerConstants.ApiOperation);
        if (apiOperation == null) {
            return null;
        }
        return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiOperation);
    }

    public static String getParameterDescription(PsiParameter psiParameter) {
        PsiAnnotation apiParam = PsiAnnotationUtils.getAnnotation(psiParameter, SwaggerConstants.ApiParam);
        if (apiParam == null) {
            return null;
        }
        return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiParam);
    }

    public static String getFieldDescription(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerConstants.ApiModelProperty);
        if (apiModelProperty == null) {
            return null;
        }
        return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiModelProperty);
    }

    public static boolean isFieldIgnore(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerConstants.ApiModelProperty);
        if (apiModelProperty == null) {
            return false;
        }
        Boolean hidden = AnnotationUtil.getBooleanAttributeValue(apiModelProperty, "hidden");
        return Boolean.TRUE.equals(hidden);
    }
}
