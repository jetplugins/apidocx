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
        if (api != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(api, "tags");
        }
        PsiAnnotation tag = PsiAnnotationUtils.getAnnotation(psiClass, SwaggerConstants.Tag);
        if (tag != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(tag, "name");
        }

        return null;
    }

    public static String getApiSummary(PsiMethod psiMethod) {
        PsiAnnotation apiOperation = PsiAnnotationUtils.getAnnotation(psiMethod, SwaggerConstants.ApiOperation);
        if (apiOperation != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiOperation);
        }
        PsiAnnotation operation = PsiAnnotationUtils.getAnnotation(psiMethod, SwaggerConstants.Operation);
        if (operation != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(operation, "summary");
        }

        return null;
    }

    public static String getParameterDescription(PsiMethod method, PsiParameter psiParameter) {
        PsiAnnotation apiParam = PsiAnnotationUtils.getAnnotation(psiParameter, SwaggerConstants.ApiParam);
        if (apiParam != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiParam);
        }
        PsiAnnotation parameter = PsiAnnotationUtils.getAnnotation(psiParameter, SwaggerConstants.Parameter);
        if (parameter != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(parameter, "description");
        }
        PsiAnnotation[] parameters = PsiAnnotationUtils.getAnnotations(psiParameter, SwaggerConstants.Parameter);
        for (PsiAnnotation p : parameters) {
            String name = PsiAnnotationUtils.getStringAttributeValueByAnnotation(p, "name");
            if (name != null && name.equals(psiParameter.getName())) {
                return PsiAnnotationUtils.getStringAttributeValueByAnnotation(p, "description");
            }
        }

        return null;
    }

    public static String getFieldDescription(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerConstants.ApiModelProperty);
        if (apiModelProperty != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiModelProperty);
        }
        PsiAnnotation schema = PsiAnnotationUtils.getAnnotation(psiField, SwaggerConstants.Schema);
        if (schema != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(schema, "description");
        }
        return null;
    }

    public static boolean isFieldIgnore(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerConstants.ApiModelProperty);
        if (apiModelProperty != null) {
            Boolean hidden = AnnotationUtil.getBooleanAttributeValue(apiModelProperty, "hidden");
            return Boolean.TRUE.equals(hidden);
        }
        return false;
    }
}
