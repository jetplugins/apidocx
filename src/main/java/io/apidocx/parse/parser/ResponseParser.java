package io.apidocx.parse.parser;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Property;
import io.apidocx.parse.util.PsiGenericUtils;
import io.apidocx.parse.util.PsiTypeUtils;
import io.apidocx.parse.util.PsiUtils;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * 方法返回值解析
 *
 * @see #parse(PsiMethod)
 */
public class ResponseParser {

    private final Project project;
    private final Module module;
    private final ApidocxConfig settings;
    private final KernelParser kernelParser;
    private final ParseHelper parseHelper;

    public ResponseParser(Project project, Module module, ApidocxConfig settings) {
        this.project = project;
        this.module = module;
        this.settings = settings;
        this.kernelParser = new KernelParser(project, module, settings, true);
        this.parseHelper = new ParseHelper(project, module);
    }

    /**
     * 解析方法响应数据
     *
     * @param method 待解析的方法
     */
    public Property parse(PsiMethod method) {
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            return null;
        }
        PsiType type = returnType;
        String typeText = returnType.getCanonicalText();

        String unwrappedType = getUnwrapType(returnType);
        if (unwrappedType != null) {
            // 需要解开包赚类处理
            String[] types = PsiGenericUtils.splitTypeAndGenericPair(unwrappedType);
            PsiClass psiClass = PsiUtils.findPsiClass(this.project, this.module, types[0]);
            type = psiClass != null ? PsiTypesUtil.getClassType(psiClass) : null;
            typeText = unwrappedType;
        } else {
            // 包装类处理
            PsiClass returnClass = getWrapperPsiClass(method);
            if (returnClass != null) {
                type = PsiTypesUtil.getClassType(returnClass);
                typeText = type.getCanonicalText() + "<" + returnType.getCanonicalText() + ">";
            }
        }

        // 解析
        Property property = kernelParser.parse(type, typeText);
        if (property != null) {
            property.setDescription(parseHelper.getTypeDescription(type, property.getPropertyValues()));
        }
        return property;
    }

    /**
     * 解开类型, 例如输入: ResponseEntity&lt;User>, 那么应当处理类型: User
     */
    private String getUnwrapType(PsiType type) {
        // 获取类型：types[0]=原始类型, types[1]=泛型参数
        String[] types = PsiGenericUtils.splitTypeAndGenericPair(type.getCanonicalText());

        // 是解开包装类， 例如： ResponseEntity<User>,
        Optional<String> unwrapOpt = settings.getReturnUnwrapTypes().stream()
                .filter(t -> t.equals(types[0])).findAny();
        if (unwrapOpt.isPresent()) {
            return types[1];
        }
        return null;
    }

    /**
     * 返回需要需要的包装类
     */
    private PsiClass getWrapperPsiClass(PsiMethod method) {
        if (StringUtils.isEmpty(settings.getReturnWrapType())) {
            return null;
        }
        PsiClass returnClass = PsiUtils.findPsiClass(this.project, this.module, settings.getReturnWrapType());
        if (returnClass == null) {
            return null;
        }

        // 是否是byte[]
        PsiType returnType = method.getReturnType();
        if (PsiTypeUtils.isBytes(returnType)) {
            return null;
        }

        // 是否是相同类型
        String[] types = PsiGenericUtils.splitTypeAndGenericPair(returnType.getCanonicalText());
        String theReturnType = types[0];
        if (Objects.equals(theReturnType, returnClass.getQualifiedName())) {
            return null;
        }
        return returnClass;
    }

}
