package io.yapix.parse.parser;

import static io.yapix.parse.util.PsiUtils.splitTypeAndGenericPair;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
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
import io.yapix.model.Property;
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

    private final Project project;
    private final Module module;
    private final YapixConfig settings;
    private final MockParser mockParser;

    public KernelParser(Project project, Module module, YapixConfig settings) {
        this.project = project;
        this.module = module;
        this.settings = settings;
        this.mockParser = new MockParser(settings);
    }

    public Property parseType(Project project, PsiType psiType, String canonicalType) {
        return doParseType(project, psiType, canonicalType, Sets.newHashSet());
    }

    private Property doParseType(Project project, PsiType psiType, String canonicalType, Set<PsiClass> chains) {
        // 泛型分割处理
        String[] types = splitTypeAndGenericPair(canonicalType);
        String type = types[0];
        String genericTypes = types[1];
        if (PsiTypeUtils.isVoid(type)) {
            return null;
        }

        Property item = new Property();
        item.setRequired(false);
        item.setType(DataTypes.OBJECT);
        PsiClass psiClass = PsiUtils.findPsiClass(this.project, this.module, type);
        if (psiClass != null) {
            psiType = PsiTypesUtil.getClassType(psiClass);
        }
        if (psiType == null) {
            return item;
        }

        item.setDescription(psiType.getCanonicalText());
        item.setType(DataTypeParser.parseType(psiType));
        // Map: 无需要解析
        if (PsiTypeUtils.isMap(psiType) || JavaConstants.Object.equals(type)) {
            item.setType(DataTypes.OBJECT);
            return item;
        }
        // 数组
        if (PsiTypeUtils.isArray(psiType)) {
            PsiArrayType arrayType = (PsiArrayType) psiType;
            PsiType componentType = arrayType.getComponentType();
            Property items = doParseType(project, componentType, componentType.getCanonicalText(), null);
            item.setItems(items);
        }
        // 集合
        if (PsiTypeUtils.isCollection(psiType)) {
            Property items = doParseType(project, null, genericTypes, null);
            item.setItems(items);
        }
        // 对象
        boolean isNeedParseObject = psiClass != null && item.isObjectType()
                && (chains == null || !chains.contains(psiClass));
        if (isNeedParseObject) {
            Map<String, Property> properties = doParseBean(project, psiType, type, genericTypes, psiClass, chains);
            item.setProperties(properties);
        }
        return item;
    }

    @NotNull
    private Map<String, Property> doParseBean(Project project, PsiType psiType, String type, String genericTypes,
            PsiClass psiClass, Set<PsiClass> chains) {
        // 防止循环引用
        HashSet<PsiClass> newChains = (chains != null) ? Sets.newHashSet(chains) : Sets.newHashSet();
        newChains.add(psiClass);
        BeanCustom beanCustom = getBeanCustom(type);

        Map<String, Property> properties = new LinkedHashMap<>();
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
                Property fieldProperty = doParseType(project, filedType, realType, newChains);
                if (fieldProperty == null) {
                    continue;
                }

                fieldProperty.setName(filedName);
                fieldProperty.setDescription(ParseHelper.getMethodDescription(method));
                fieldProperty.setDeprecated(ParseHelper.isDeprecated(method));
                fieldProperty.setMock(mockParser.parseMock(fieldProperty, filedType, null, filedName));
                if (beanCustom != null) {
                    handleWithBeanCustomField(fieldProperty, filedName, beanCustom);
                }
                properties.put(fieldProperty.getName(), fieldProperty);
            }
        } else {
            // 实体类
            PsiField[] fields = PsiUtils.getFields(psiClass);
            for (PsiField field : fields) {
                String filedName = field.getName();
                PsiType fieldType = field.getType();
                // 自定义配置决定是否处理该字段
                if (beanCustom != null && !beanCustom.isNeedHandleField(filedName)) {
                    continue;
                }
                String realType = PsiUtils.getRealTypeWithGeneric(psiClass, fieldType, genericTypes);
                Property fieldProperty = doParseType(project, fieldType, realType, newChains);
                if (fieldProperty == null) {
                    continue;
                }
                fieldProperty.setName(ParseHelper.getFiledName(field));
                fieldProperty.setDescription(ParseHelper.getFiledDescription(field));
                fieldProperty.setDeprecated(ParseHelper.getFiledDeprecated(field));
                fieldProperty.setRequired(ParseHelper.getFiledRequired(field));
                fieldProperty.setMock(mockParser.parseMock(fieldProperty, fieldType, field, filedName));

                if (beanCustom != null) {
                    handleWithBeanCustomField(fieldProperty, filedName, beanCustom);
                }
                properties.put(fieldProperty.getName(), fieldProperty);
            }
        }
        return properties;
    }

    /**
     * 处理自定义的bean配置
     */
    private void handleWithBeanCustomField(Property filedItem, String fieldName, BeanCustom beanCustom) {
        if (beanCustom.getFields() == null || !beanCustom.getFields().containsKey(fieldName)) {
            return;
        }
        Property customItem = beanCustom.getFields().get(fieldName);
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
