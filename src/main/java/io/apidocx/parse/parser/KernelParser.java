package io.apidocx.parse.parser;

import static io.apidocx.base.util.NotificationUtils.notifyWarning;
import static java.lang.String.format;
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
import io.apidocx.config.ApidocxConfig;
import io.apidocx.config.BeanCustom;
import io.apidocx.model.DataTypes;
import io.apidocx.model.Property;
import io.apidocx.parse.constant.DocumentTags;
import io.apidocx.parse.model.Jsr303Info;
import io.apidocx.parse.model.TypeParseContext;
import io.apidocx.parse.util.PsiDocCommentUtils;
import io.apidocx.parse.util.PsiFieldUtils;
import io.apidocx.parse.util.PsiGenericUtils;
import io.apidocx.parse.util.PsiTypeUtils;
import io.apidocx.parse.util.PsiUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 解析一个完整的类型
 */
public class KernelParser {

    private final Project project;
    private final Module module;
    private final ApidocxConfig settings;
    private final MockParser mockParser;
    private final DataTypeParser dataTypeParser;
    private final DateParser dateParser;
    private final ParseHelper parseHelper;
    private final boolean isResponse;

    public KernelParser(Project project, Module module, ApidocxConfig settings, boolean isResponse) {
        this.project = project;
        this.module = module;
        this.settings = settings;
        this.mockParser = new MockParser(project, module, settings);
        this.dataTypeParser = new DataTypeParser(project, module, settings);
        this.dateParser = new DateParser(settings);
        this.parseHelper = new ParseHelper(project, module);
        this.isResponse = isResponse;
    }

    public Property parse(PsiType psiType) {
        return parse(psiType, psiType.getCanonicalText());
    }

    public Property parse(PsiType psiType, String canonicalType) {
        TypeParseContext context = new TypeParseContext();
        return parse(context, psiType, canonicalType);
    }

    /**
     * 解析指定类型
     */
    public Property parse(TypeParseContext context, PsiType psiType, String canonicalType) {
        return doParse(context, psiType, canonicalType, Sets.newHashSet());
    }

    private Property doParse(TypeParseContext context, PsiType psiType, String canonicalType, Set<PsiClass> chains) {
        Property property = new Property();
        property.setRequired(false);
        property.setType(DataTypes.OBJECT);
        if (StringUtils.isEmpty(canonicalType)) {
            return property;
        }

        // 泛型分割处理
        String[] types = PsiGenericUtils.splitTypeAndGenericPair(canonicalType);
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
            return property;
        }
        property.setType(dataTypeParser.parse(psiType));
        property.setValues(parseHelper.getTypeValues(psiType));

        // 文件类型
        if (property.isFileType()) {
            return property;
        }

        // Map类型
        if (PsiTypeUtils.isMap(psiType, this.project, this.module) || Object.class.getName().equals(type)) {
            property.setType(DataTypes.OBJECT);
            doHandleMap(context, property, genericTypes, chains);
            return property;
        }

        // 数组
        if (PsiTypeUtils.isArray(psiType)) {
            PsiArrayType arrayType = (PsiArrayType) psiType;
            PsiType componentType = arrayType.getComponentType();
            Property items = doParse(context, componentType, componentType.getCanonicalText(), chains);
            property.setItems(items);
        }

        // 集合
        if (PsiTypeUtils.isCollection(psiType, this.project, this.module)) {
            Property items = doParse(context, null, genericTypes, chains);
            property.setItems(items);
        }

        // 对象
        boolean isNeedParseObject = psiClass != null && property.isObjectType()
                && (chains == null || !chains.contains(psiClass));
        if (isNeedParseObject) {
            Map<String, Property> properties = doParseBean(context, type, genericTypes, psiClass, chains);
            property.setProperties(properties);
        }

        // Mock数据: 放最后面解析会用到上面的数据
        String mock = mockParser.parse(property, psiType, null, null);
        property.setMock(mock);
        return property;
    }

    /**
     * 处理Map类型
     */
    private void doHandleMap(TypeParseContext context, Property property, String genericTypes, Set<PsiClass> chains) {
        // 尝试解析map中泛型
        if (StringUtils.isEmpty(genericTypes)) {
            return;
        }

        String[] kvGenericTypes = PsiGenericUtils.splitGenericParameters(genericTypes);
        if (kvGenericTypes.length >= 2) {
            Property mapValueProperty = doParse(context, null, kvGenericTypes[1], chains);
            if (mapValueProperty != null) {
                mapValueProperty.setName("KEY");
                Map<String, Property> properties = Maps.newHashMap();
                properties.put(mapValueProperty.getName(), mapValueProperty);
                property.setProperties(properties);
            }
        }
    }

    @NotNull
    private Map<String, Property> doParseBean(TypeParseContext context, String type, String genericTypes, PsiClass psiClass,
                                              Set<PsiClass> chains) {
        Set<PsiClass> newChains = createNewChains(chains, psiClass);
        Map<String, Property> properties = new LinkedHashMap<>();
        BeanCustom beanCustom = this.settings.getBeanCustomSettings(type);

        // 针对接口/实体类, 检查是否存在@see引用
        for (String typeName : PsiDocCommentUtils.getTagTextSet(psiClass, DocumentTags.See)) {
            // 优先根据全限定名获取引用类, 其次根据非限定名(短名)获取.
            // [note] 建议用户尽量使用全限定名, 短名容易出现重名冲突.
            // 其实可以通过获取当前类的import作filter.
            // 但既然用户使用javadoc, 就应该对输入作严格规范, 而非对插件输出作强要求.
            PsiClass refPsiClass = Optional.ofNullable(PsiUtils.findPsiClass(project, module, typeName))
                    .orElse(PsiUtils.findPsiClassByShortName(project, module, typeName));

            // 引用类必须跟当前类存在派生关系(适用于接口和实体类)
            if (refPsiClass == null || !refPsiClass.isInheritor(psiClass, true)) {
                notifyWarning("Parse skipped", format("%s @see %s", type, typeName));
                continue;
            }

            Optional.of(PsiTypesUtil.getClassType(refPsiClass))
                    .map(it -> doParse(context, it, it.getCanonicalText(), chains))
                    .map(Property::getProperties)
                    .ifPresent(properties::putAll);
        }

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
                Property fieldProperty = doParse(context, filedType, realType, newChains);
                if (fieldProperty == null) {
                    continue;
                }

                fieldProperty.setName(filedName);
                fieldProperty.setDeprecated(parseHelper.getApiDeprecated(method));
                fieldProperty.setMock(mockParser.parse(fieldProperty, filedType, null, filedName));
                if (beanCustom != null) {
                    handleWithBeanCustomField(fieldProperty, filedName, beanCustom);
                }
                properties.put(fieldProperty.getName(), fieldProperty);
            }
        } else {
            // 实体类
            List<PsiField> fields = parseHelper.getFields(psiClass);
            for (PsiField field : fields) {
                String filedName = field.getName();
                PsiType fieldType = field.getType();
                // 自定义配置决定是否处理
                if (beanCustom != null && !beanCustom.isNeedHandleField(filedName)) {
                    continue;
                }
                String realType = PsiGenericUtils.getRealTypeWithGeneric(psiClass, fieldType, genericTypes);
                Property fieldProperty = doParse(context, fieldType, realType, newChains);
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
                // JSR303注解
                Jsr303Info jsr303Info = parseHelper.getJsr303Info(field);
                if (jsr303Info.getMinLength() != null) {
                    fieldProperty.setMinLength(jsr303Info.getMinLength());
                }
                if (jsr303Info.getMaxLength() != null) {
                    fieldProperty.setMaxLength(jsr303Info.getMaxLength());
                }
                if (jsr303Info.getMinimum() != null) {
                    fieldProperty.setMinimum(jsr303Info.getMinimum());
                }
                if (jsr303Info.getMaximum() != null) {
                    fieldProperty.setMaximum(jsr303Info.getMaximum());
                }

                fieldProperty.setValues(parseHelper.getFieldValues(field));
                fieldProperty.setName(parseHelper.getFieldName(field));
                fieldProperty.setDescription(parseHelper.getFieldDescription(field, fieldProperty.getPropertyValues()));
                fieldProperty.setDeprecated(parseHelper.getFieldDeprecated(field));
                fieldProperty.setRequired(parseHelper.getFieldRequired(context, field));
                fieldProperty.setMock(mockParser.parse(fieldProperty, fieldType, field, filedName));

                if (beanCustom != null) {
                    handleWithBeanCustomField(fieldProperty, filedName, beanCustom);
                }
                properties.put(fieldProperty.getName(), fieldProperty);
            }
        }
        return properties;
    }

    private Set<PsiClass> createNewChains(Set<PsiClass> chains, PsiClass psiClass) {
        Set<PsiClass> newer = (chains != null) ? Sets.newHashSet(chains) : Sets.newHashSet();
        newer.add(psiClass);
        return newer;
    }

    /**
     * 处理自定义的bean配置
     */
    private void handleWithBeanCustomField(Property property, String fieldName, BeanCustom beanCustom) {
        Property fieldProperty = beanCustom.getFieldProperty(fieldName);
        if (fieldProperty != null) {
            property.mergeCustom(fieldProperty);
        }
    }

}
