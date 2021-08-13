package com.github.jetplugins.yapix.parse.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class PsiUtils {

    private PsiUtils() {
    }

    public static PsiField[] getFields(PsiClass t) {
        PsiField[] fields = t.getAllFields();
        return Arrays.stream(fields).filter(PsiUtils::isNeedField).toArray(PsiField[]::new);
    }

    public static boolean isNeedField(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList == null || !modifierList.hasExplicitModifier(PsiModifier.STATIC)) {
            return true;
        }
        return false;
    }


    public static PsiClass findPsiClass(Project project, String type) {
        return JavaPsiFacade.getInstance(project).findClass(type, GlobalSearchScope.allScope(project));
    }

    public static PsiMethod[] getGetterMethods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods()).filter(method -> {
            String methodName = method.getName();
            PsiType returnType = method.getReturnType();
            PsiModifierList modifierList = method.getModifierList();
            boolean isAccessMethod = !modifierList.hasModifierProperty("static")
                    && !methodName.equals("getClass")
                    && ((methodName.startsWith("get") && methodName.length() > 3 && returnType != null)
                    || (methodName.startsWith("is") && methodName.length() > 2 && returnType != null && returnType
                    .getCanonicalText().equals("boolean"))
            );
            return isAccessMethod;
        }).toArray(len -> new PsiMethod[len]);
    }

    /**
     * 获取字段的真实类型，替换泛型
     * <p>
     * 输入: User<T>, List<List<T>> data, 泛型参数: User<Item>
     * <p>
     * 输出: List, List<Item>
     */
    public static String getRealTypeWithGeneric(PsiClass clazz, PsiType type, String genericTypes) {
        String[] generics = genericTypes != null ? genericTypes.split(",") : new String[0];

        Map<String, String> genericMap = new HashMap<>();
        PsiTypeParameter[] parameters = clazz.getTypeParameters();
        for (int i = 0; i < parameters.length; i++) {
            String key = parameters[i].getText();
            String value = (i < generics.length) ? generics[i].trim() : "java.lang.Object";
            genericMap.put(key, value);
        }

        // 泛型替换: List<T> -> List<User>
        String[] filedTypes = splitGenericTypes(type);
        for (int i = 0; i < filedTypes.length; i++) {
            String filedType = filedTypes[i];
            String[] splits = filedType.split(",");
            for (int j = 0; j < splits.length; j++) {
                String gt = splits[j].trim();
                if (genericMap.containsKey(gt)) {
                    String realGt = genericMap.get(gt);
                    splits[j] = realGt;
                }
            }
            filedTypes[i] = StringUtils.join(splits, ",");
        }
        return concatGenericTypes(filedTypes);
    }

    /**
     * 分割类型和泛型参数对
     * <p>
     * 输入: List<List<Integer>> 输出: List List<Integer>
     */
    public static String[] splitTypeAndGenericPair(String typeText) {
        String[] types = new String[2];
        int idx = typeText.indexOf("<");
        if (idx == -1) {
            types[0] = typeText;
        } else {
            types[0] = typeText.substring(0, idx);
            types[1] = typeText.substring(idx + 1, typeText.length() - 1);
        }
        return types;
    }

    /**
     * 分割泛型,
     * <p>
     * 输入: List<User<A,B>> 返回: List User A,B
     */
    private static String[] splitGenericTypes(PsiType psiType) {
        String[] types = psiType.getCanonicalText().split("<");
        types[types.length - 1] = types[types.length - 1].replaceAll(">", "");
        return types;
    }

    /**
     * 泛型合并
     * <p>
     * 输入: List User A,B 输出: List<User<A,B>>
     */
    private static String concatGenericTypes(String[] types) {
        StringBuilder element = new StringBuilder();
        for (int i = types.length - 1; i >= 0; i--) {
            if (element.length() == 0) {
                element.append(types[i]);
                continue;
            }
            element = new StringBuilder(types[i] + "<" + element + ">");
        }
        return element.toString();
    }
}
