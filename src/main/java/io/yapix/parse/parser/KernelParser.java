package io.yapix.parse.parser;

import static io.yapix.parse.util.PsiGenericUtils.splitTypeAndGenericPair;
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
import io.yapix.parse.util.PsiFieldUtils;
import io.yapix.parse.util.PsiGenericUtils;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.PsiUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 解析一个完整的类型
 */
public class KernelParser {

    private final Project project;
    private final Module module;
    private final YapixConfig settings;
    private final MockParser mockParser;
    private final DataTypeParser dataTypeParser;
    private final DateParser dateParser;
    private final ParseHelper parseHelper;
    private final boolean isResponse;

    public KernelParser(Project project, Module module, YapixConfig settings, boolean isResponse) {
        this.project = project;
        this.module = module;
        this.settings = settings;
        this.mockParser = new MockParser(project, module, settings);
        this.dataTypeParser = new DataTypeParser(project, module, settings);
        this.dateParser = new DateParser(settings);
        this.parseHelper = new ParseHelper(project, module);
        this.isResponse = isResponse;
    }

    /**
     * 解析指定类型
     */
    public Property parseType(PsiType psiType, String canonicalType) {
        return doParseType(psiType, canonicalType, Sets.newHashSet());
    }

    private Property doParseType(PsiType psiType, String canonicalType, Set<PsiClass> chains) {
        Property item = new Property();
        item.setRequired(false);
        item.setType(DataTypes.OBJECT);
        if (StringUtils.isEmpty(canonicalType)) {
            return item;
        }

        // 泛型分割处理
        String[] types = splitTypeAndGenericPair(canonicalType);
        String type = types[0];
        String genericTypes = types[1];
        if (PsiTypeUtils.isVoid(type)) {
            return null;
        }

        PsiClass psiClass = PsiUtils.findPsiClass(this.project, this.module, type);
        if (psiClass != null) {
            psiType = PsiTypesUtil.getClassType(psiClass);
        }
        if (psiType == null) {
            return item;
        }

        item.setType(dataTypeParser.parseType(psiType));
        item.setValues(parseHelper.getTypeValues(psiType));
        // 文件： 无需继续解析
        if (DataTypes.FILE.equals(item.getType())) {
            return item;
        }
        // Map类型
        if (PsiTypeUtils.isMap(psiType, this.project, this.module) || JavaConstants.Object.equals(type)) {
            item.setType(DataTypes.OBJECT);
            doHandleMap(item, genericTypes, chains);
            return item;
        }
        // 数组
        if (PsiTypeUtils.isArray(psiType)) {
            PsiArrayType arrayType = (PsiArrayType) psiType;
            PsiType componentType = arrayType.getComponentType();
            Property items = doParseType(componentType, componentType.getCanonicalText(), chains);
            item.setItems(items);
        }
        // 集合
        if (PsiTypeUtils.isCollection(psiType, this.project, this.module)) {
            Property items = doParseType(null, genericTypes, chains);
            item.setItems(items);
        }
        // 对象
        boolean isNeedParseObject = psiClass != null && item.isObjectType()
                && (chains == null || !chains.contains(psiClass));
        if (isNeedParseObject) {
            Map<String, Property> properties = doParseBean(type, genericTypes, psiClass, chains);
            item.setProperties(properties);
        }
        return item;
    }

    /**
     * 处理Map类型
     */
    private void doHandleMap(Property item, String genericTypes, Set<PsiClass> chains) {
        // 尝试解析map值得类型
        if (StringUtils.isEmpty(genericTypes)) {
            return;
        }

        String[] kvGenericTypes = PsiGenericUtils.splitGenericParameters(genericTypes);
        if (kvGenericTypes.length >= 2) {
            Property mapValueProperty = doParseType(null, kvGenericTypes[1], chains);
            if (mapValueProperty != null) {
                Map<String, Property> properties = Maps.newHashMap();
                properties.put("KEY", mapValueProperty);
                item.setProperties(properties);
            }
        }
    }

    @NotNull
    private Map<String, Property> doParseBean(String type, String genericTypes, PsiClass psiClass,
            Set<PsiClass> chains) {
        // 防止循环引用
        HashSet<PsiClass> newChains = (chains != null) ? Sets.newHashSet(chains) : Sets.newHashSet();
        newChains.add(psiClass);
        BeanCustom beanCustom = getBeanCustomSettings(type);

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
                String realType = PsiGenericUtils.getRealTypeWithGeneric(psiClass, filedType, genericTypes);
                Property fieldProperty = doParseType(filedType, realType, newChains);
                if (fieldProperty == null) {
                    continue;
                }

                fieldProperty.setName(filedName);
                fieldProperty.setDeprecated(parseHelper.getApiDeprecated(method));
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
                String realType = PsiGenericUtils.getRealTypeWithGeneric(psiClass, fieldType, genericTypes);
                Property fieldProperty = doParseType(fieldType, realType, newChains);
                if (fieldProperty == null) {
                    continue;
                }
                dateParser.handle(fieldProperty, field);
                // 响应参数不要默认值
                if (!isResponse) {
                    String defaultValue = PsiFieldUtils.getFieldDefaultValue(field);
                    if (defaultValue != null) {
                        fieldProperty.setDefaultValue(defaultValue);
                    }
                }
                fieldProperty.setValues(parseHelper.getFieldValues(field));
                fieldProperty.setName(parseHelper.getFieldName(field));
                fieldProperty.setDescription(parseHelper.getFieldDescription(field, fieldProperty.getValues()));
                fieldProperty.setDeprecated(parseHelper.getFieldDeprecated(field));
                fieldProperty.setRequired(parseHelper.getFieldRequired(field));
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

    /**
     * 获取指定类型自定义的bean配置
     */
    private BeanCustom getBeanCustomSettings(String type) {
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
