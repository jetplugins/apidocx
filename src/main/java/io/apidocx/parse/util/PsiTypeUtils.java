package io.apidocx.parse.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTypesUtil;
import io.apidocx.model.DataTypes;
import io.apidocx.parse.parser.DataTypeParser;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * PsiType相关工具.
 */
public class PsiTypeUtils {

    private PsiTypeUtils() {
    }

    /**
     * 是否是原生类型
     */
    public static boolean isPrimitive(PsiType type) {
        return type instanceof PsiPrimitiveType;
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
    public static boolean isCollection(PsiType type, Project project, Module module) {
        PsiClass mapPsiClass = PsiUtils.findPsiClass(project, module, Collection.class.getName());
        PsiClassType mapPsiType = PsiTypesUtil.getClassType(mapPsiClass);
        return mapPsiType.isAssignableFrom(type);
    }

    /**
     * 是否是Map，以及其子类型
     */
    public static boolean isMap(PsiType type, Project project, Module module) {
        PsiClass mapPsiClass = PsiUtils.findPsiClass(project, module, Map.class.getName());
        PsiClassType mapPsiType = PsiTypesUtil.getClassType(mapPsiClass);
        return mapPsiType.isAssignableFrom(type);
    }

    /**
     * 是否是枚举类型
     */
    public static boolean isEnum(PsiType type) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        return psiClass != null && psiClass.isEnum();
    }

    /**
     * 获取枚举类
     */
    public static PsiClass getEnumClassIncludeArray(Project project, Module module, PsiType type) {
        PsiType enumType = null;
        boolean isEnum = isEnum(type);
        if (isEnum) {
            enumType = type;
        } else if (isArray(type)) {
            PsiArrayType arrayType = (PsiArrayType) type;
            enumType = arrayType.getComponentType();
        } else if (type instanceof PsiClassReferenceType && isCollection(type, project, module)) {
            PsiClassReferenceType type1 = (PsiClassReferenceType) type;
            enumType = type1.getParameters().length > 0 ? type1.getParameters()[0] : null;
        }
        if (enumType == null) {
            return null;
        }
        PsiClass enumClass = PsiUtils.findPsiClass(project, module, enumType.getCanonicalText());
        if (enumClass != null && enumClass.isEnum()) {
            return enumClass;
        }
        return null;
    }

    /**
     * 是否是文件上传
     */
    public static boolean isFileIncludeArray(PsiType type) {
        return DataTypes.FILE.equals(DataTypeParser.getTypeInProperties(type));
    }

}
