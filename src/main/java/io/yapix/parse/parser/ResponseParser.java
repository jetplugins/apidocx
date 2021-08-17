package io.yapix.parse.parser;

import static io.yapix.parse.util.PsiUtils.splitTypeAndGenericPair;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.yapix.model.Item;
import io.yapix.parse.ApiParseSettings;
import io.yapix.parse.util.PsiTypeUtils;
import io.yapix.parse.util.PsiUtils;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * 方法返回值解析
 *
 * @see #parse(PsiMethod)
 */
public class ResponseParser {

    private final ApiParseSettings settings;

    public ResponseParser(ApiParseSettings settings) {
        this.settings = settings;
    }

    public Item parse(PsiMethod method) {
        PsiType returnType = method.getReturnType();
        if (returnType == null) {
            return null;
        }
        PsiType type = returnType;
        String typeText = returnType.getCanonicalText();

        // 包装类处理
        PsiClass returnClass = getWrapperPsiClass(method);
        if (returnClass != null) {
            type = PsiTypesUtil.getClassType(returnClass);
            typeText = type.getCanonicalText() + "<" + returnType.getCanonicalText() + ">";
        }

        // 解析
        Item item = KernelParser.parseType(method.getProject(), type, typeText);
        if (item != null) {
            item.setDescription(returnType.getCanonicalText());
        }
        return item;
    }

    /**
     * 返回需要需要的包装类
     */
    private PsiClass getWrapperPsiClass(PsiMethod method) {
        if (StringUtils.isEmpty(settings.getReturnClass())) {
            return null;
        }
        PsiClass returnClass = PsiUtils.findPsiClass(method.getProject(), settings.getReturnClass());
        if (returnClass == null) {
            return null;
        }

        // 是否是byte[]
        PsiType returnType = method.getReturnType();
        if (PsiTypeUtils.isBytes(returnType)) {
            return null;
        }

        // 是否是相同类型
        String[] types = splitTypeAndGenericPair(returnType.getCanonicalText());
        String theReturnType = types[0];
        if (Objects.equals(theReturnType, returnClass.getQualifiedName())) {
            return null;
        }
        return returnClass;
    }

}
