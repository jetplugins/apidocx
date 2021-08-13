package com.github.jetplugins.yapix.parse.parser;

import static com.github.jetplugins.yapix.parse.util.PsiUtils.splitTypeAndGenericPair;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.github.jetplugins.yapix.model.DataTypes;
import com.github.jetplugins.yapix.model.Item;
import com.github.jetplugins.yapix.parse.constant.JavaConstants;
import com.github.jetplugins.yapix.parse.util.PsiTypeUtils;
import com.github.jetplugins.yapix.parse.util.PsiUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * 解析一个完整的类型
 */
public class CoreParser {

    public static Item parseType(Project project, PsiType psiType, String canonicalType) {
        return doParseType(project, psiType, canonicalType, Sets.newHashSet());
    }

    private static Item doParseType(Project project, PsiType psiType, String canonicalType, Set<PsiClass> chains) {
        // 泛型分割处理
        String[] types = splitTypeAndGenericPair(canonicalType);
        String type = types[0];
        String genericTypes = types[1];
        if (PsiTypeUtils.isVoid(type)) {
            return null;
        }

        Item item = new Item();
        item.setType(DataTypes.OBJECT);
        PsiClass psiClass = PsiUtils.findPsiClass(project, type);
        if (psiClass != null) {
            psiType = PsiTypesUtil.getClassType(psiClass);
        }
        if (psiType == null) {
            return item;
        }

        item.setDescription(psiType.getCanonicalText());
        item.setType(DataTypeParser.parseType(psiType));
        item.setMock(MockParser.parseMock(psiType));
        // Map: 无需要解析
        if (PsiTypeUtils.isMap(psiType) || JavaConstants.Object.equals(type)) {
            item.setType(DataTypes.OBJECT);
            return item;
        }
        // 数组
        if (PsiTypeUtils.isArray(psiType)) {
            PsiArrayType arrayType = (PsiArrayType) psiType;
            PsiType componentType = arrayType.getComponentType();
            Item items = doParseType(project, componentType, componentType.getCanonicalText(), null);
            item.setItems(items);
        }
        // 集合
        if (PsiTypeUtils.isCollection(psiType)) {
            Item items = doParseType(project, null, genericTypes, null);
            item.setItems(items);
        }
        // 对象
        boolean isNeedParseObject = psiClass != null && item.isObjectType()
                && (chains == null || !chains.contains(psiClass));
        if (isNeedParseObject) {
            Map<String, Item> properties = doParseBean(project, psiType, genericTypes, psiClass, chains);
            item.setProperties(properties);
        }
        return item;
    }

    @NotNull
    private static Map<String, Item> doParseBean(Project project, PsiType psiType, String genericTypes,
            PsiClass psiClass, Set<PsiClass> chains) {
        // 防止循环引用
        HashSet<PsiClass> newChains = (chains != null) ? Sets.newHashSet(chains) : Sets.newHashSet();
        newChains.add(psiClass);

        Map<String, Item> properties = new LinkedHashMap<>();
        if (psiClass.isInterface()) {
            // 接口类型
            PsiMethod[] methods = PsiUtils.getGetterMethods(psiClass);
            for (PsiMethod method : methods) {
                String methodName = method.getName();
                PsiType filedType = method.getReturnType();
                String filedName = uncapitalize(methodName.substring(methodName.startsWith("get") ? 3 : 2));
                String realType = PsiUtils.getRealTypeWithGeneric(psiClass, filedType, genericTypes);
                Item filedItem = doParseType(project, filedType, realType, newChains);
                if (filedItem == null) {
                    continue;
                }

                filedItem.setName(filedName);
                filedItem.setDescription(ParseHelper.getMethodDescription(method));
                filedItem.setDeprecated(ParseHelper.isDeprecated(method));

                properties.put(filedItem.getName(), filedItem);
            }
        } else {
            // 实体类
            PsiField[] fields = PsiUtils.getFields(psiClass);
            for (PsiField field : fields) {
                String realType = PsiUtils.getRealTypeWithGeneric(psiClass, field.getType(), genericTypes);
                Item filedItem = doParseType(project, psiType, realType, newChains);
                if (filedItem == null) {
                    continue;
                }
                filedItem.setName(ParseHelper.getFiledName(field));
                filedItem.setDescription(ParseHelper.getFiledDescription(field));
                filedItem.setDeprecated(ParseHelper.getFiledDeprecated(field));
                filedItem.setRequired(ParseHelper.getFiledRequired(field));

                properties.put(filedItem.getName(), filedItem);
            }
        }
        return properties;
    }

}
