package io.apidocx.parse.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 泛型处理
 */
public class PsiGenericUtils {

    /**
     * 获取字段的真实类型，替换泛型
     * <p>
     * 输入: User<T>, List<List<T>> data, 泛型参数: User<Item>
     * <p>
     * 输出: List, List<Item>
     */
    public static String getRealTypeWithGeneric(PsiClass clazz, PsiType type, String genericTypes) {
        String[] generics = genericTypes != null ? splitGenericParameters(genericTypes) : new String[0];

        Map<String, String> genericMap = new HashMap<>();
        PsiTypeParameter[] parameters = clazz.getTypeParameters();
        for (int i = 0; i < parameters.length; i++) {
            String key = parameters[i].getText().split("\\s")[0];
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
     *
     * @param psiType 指定类型， 例如：List;&lt;User&lt;A,B>>
     * @return 分割后的嵌套泛型, 例如：["List", "User", "A,B"]
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

    /**
     * 分割多个泛型参数
     *
     * @param genericParameters 泛型参数例如: User&lt;A, B>, Student&lt;C, D>
     * @return 返回同级别被分割的泛型 [User&lt;A,B>, Student&lt;C, D>
     */
    public static String[] splitGenericParameters(String genericParameters) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean inGeneric = false;  // 是否在泛型范围，钻石符号之间
        for (char c : genericParameters.toCharArray()) {
            if (c == ' ') {
                continue;
            }
            // 非泛型内的,号进行分割
            if (c == ',' && !inGeneric) {
                list.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            sb.append(c);
            if (c == '<') {
                inGeneric = true;
            } else if (c == '>') {
                inGeneric = false;
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }
}
