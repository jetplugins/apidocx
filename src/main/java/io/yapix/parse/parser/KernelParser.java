package io.yapix.parse.parser;

import static io.yapix.parse.util.PsiUtils.splitTypeAndGenericPair;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.yapix.config.BeanCustom;
import io.yapix.config.YapixConfig;
import io.yapix.model.DataTypes;
import io.yapix.model.Item;
import io.yapix.parse.constant.JavaConstants;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.PsiUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * 解析一个完整的类型
 */
public class KernelParser {

    private final YapixConfig settings;

    public KernelParser(YapixConfig settings) {
        this.settings = settings;
    }

    public Item parseType(Project project, PsiType psiType, String canonicalType) {
        return doParseType(project, psiType, canonicalType, Sets.newHashSet());
    }

    private Item doParseType(Project project, PsiType psiType, String canonicalType, Set<PsiClass> chains) {
        // 泛型分割处理
        String[] types = splitTypeAndGenericPair(canonicalType);
        String type = types[0];
        String genericTypes = types[1];
        if (PsiTypeUtils.isVoid(type)) {
            return null;
        }

        Item item = new Item();
        item.setRequired(false);
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
            Map<String, Item> properties = doParseBean(project, psiType, type, genericTypes, psiClass, chains);
            item.setProperties(properties);
        }
        return item;
    }

    @NotNull
    private Map<String, Item> doParseBean(Project project, PsiType psiType, String type, String genericTypes,
            PsiClass psiClass, Set<PsiClass> chains) {
        // 防止循环引用
        HashSet<PsiClass> newChains = (chains != null) ? Sets.newHashSet(chains) : Sets.newHashSet();
        newChains.add(psiClass);
        BeanCustom beanCustom = getBeanCustom(type);

        Map<String, Item> properties = new LinkedHashMap<>();
        if (psiClass.isInterface()) {
            // 接口类型
            PsiMethod[] methods = PsiUtils.getGetterMethods(psiClass);
            for (PsiMethod method : methods) {
                String methodName = method.getName();
                PsiType filedType = method.getReturnType();
                String filedName = uncapitalize(methodName.substring(methodName.startsWith("get") ? 3 : 2));
                // 自定义配置决定是否处理该字段
                if (beanCustom != null && !beanCustom.isNeedHandleField(filedName)) {
                    continue;
                }
                String realType = PsiUtils.getRealTypeWithGeneric(psiClass, filedType, genericTypes);
                Item filedItem = doParseType(project, filedType, realType, newChains);
                if (filedItem == null) {
                    continue;
                }

                filedItem.setName(filedName);
                filedItem.setDescription(ParseHelper.getMethodDescription(method));
                filedItem.setDeprecated(ParseHelper.isDeprecated(method));
                if (beanCustom != null) {
                    handleWithBeanCustomField(filedItem, filedName, beanCustom);
                }
                properties.put(filedItem.getName(), filedItem);
            }
        } else {
            // 实体类
            PsiField[] fields = PsiUtils.getFields(psiClass);
            for (PsiField field : fields) {
                String filedName = field.getName();
                // 自定义配置决定是否处理该字段
                if (beanCustom != null && !beanCustom.isNeedHandleField(filedName)) {
                    continue;
                }
                String realType = PsiUtils.getRealTypeWithGeneric(psiClass, field.getType(), genericTypes);
                Item filedItem = doParseType(project, psiType, realType, newChains);
                if (filedItem == null) {
                    continue;
                }
                filedItem.setName(ParseHelper.getFiledName(field));
                filedItem.setDescription(ParseHelper.getFiledDescription(field));
                filedItem.setDeprecated(ParseHelper.getFiledDeprecated(field));
                filedItem.setRequired(ParseHelper.getFiledRequired(field));
                if (beanCustom != null) {
                    handleWithBeanCustomField(filedItem, filedName, beanCustom);
                }
                properties.put(filedItem.getName(), filedItem);
            }
        }
        return properties;
    }

    /**
     * 处理自定义的bean配置
     */
    private void handleWithBeanCustomField(Item filedItem, String fieldName, BeanCustom beanCustom) {
        if (beanCustom.getFields() == null || !beanCustom.getFields().containsKey(fieldName)) {
            return;
        }
        Item customItem = beanCustom.getFields().get(fieldName);
        if (customItem == null) {
            return;
        }
        filedItem.mergeCustom(customItem);
    }

    private BeanCustom getBeanCustom(String type) {
        BeanCustom custom = null;
        Map<String, BeanCustom> beans = settings.getBeans();
        if (beans != null) {
            custom = beans.get(type);
        }
        if (custom != null) {
            if (custom.getIncludes() == null) {
                custom.setIncludes(Collections.emptyNavigableSet());
            }
            if (custom.getExcludes() == null) {
                custom.setExcludes(Collections.emptyNavigableSet());
            }
            if (custom.getFields() == null) {
                custom.setFields(Maps.newHashMapWithExpectedSize(0));
            }
        }
        return custom;
    }

}
