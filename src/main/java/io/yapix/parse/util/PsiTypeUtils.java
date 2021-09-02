package io.yapix.parse.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import io.yapix.parse.constant.JavaConstants;
import java.util.Objects;

/**
 * PsiType相关工具.
 */
public class PsiTypeUtils {

    private PsiTypeUtils() {
    }

    /**
     * 是否为空类型
     */
    public static boolean isVoid(String name) {
        return Objects.equals(name, "void") || Objects.equals(name, "java.lang.Void");
    }

    /**
     * 是否是字节数组
     */
    public static boolean isBytes(PsiType type) {
        return Objects.equals("byte[]", type.getCanonicalText());
    }

    /**
     * 是否是数组类型
     */
    public static boolean isArray(PsiType type) {
        return type instanceof PsiArrayType;
    }

    /**
     * 是否是集合类型或其子类型
     */
    public static boolean isCollection(PsiType type) {
        return isTargetType(type, JavaConstants.Collection);
    }

    /**
     * 是否是Map，以及其子类型
     */
    public static boolean isMap(PsiType type) {
        return isTargetType(type, JavaConstants.Map);
    }

    /**
     * 是否是枚举类型
     */
    public static boolean isEnum(PsiType type) {
        return isTargetType(type, JavaConstants.Enum);
    }

    /**
     * 是否是集合类型
     */
    private static boolean isTargetType(PsiType type, String targetType) {
        String canonicalText = type.getCanonicalText();
        if (canonicalText.equals(JavaConstants.Object)) {
            return false;
        }
        if (canonicalText.startsWith(targetType)) {
            return true;
        }
        PsiType[] superTypes = type.getSuperTypes();
        for (PsiType superType : superTypes) {
            if (isTargetType(superType, targetType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取枚举类
     */
    public static PsiClass getEnumClassIncludeArray(Project project, PsiType type) {
        PsiType enumType = null;
        boolean isEnum = isEnum(type);
        if (isEnum) {
            enumType = type;
        } else if (isArray(type)) {
            PsiArrayType arrayType = (PsiArrayType) type;
            enumType = arrayType.getComponentType();
        } else if (isCollection(type)) {
            PsiClassReferenceType type1 = (PsiClassReferenceType) type;
            enumType = type1.getParameters().length > 0 ? type1.getParameters()[0] : null;
        }
        if (enumType == null) {
            return null;
        }
        PsiClass enumClass = PsiUtils.findPsiClass(project, null, enumType.getCanonicalText());
        if (enumClass != null && enumClass.isEnum()) {
            return enumClass;
        }
        return null;
    }
}
