package io.yapix.parse.model;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import io.yapix.model.Api;
import java.util.Collections;
import java.util.List;

/**
 * 接口解析结果
 */
public class ClassParseData {

    /**
     * 有效的类
     */
    public boolean valid;

    /**
     * 目标类
     */
    public PsiClass psiClass;

    /**
     * 声明的分类名称
     */
    public String declaredCategory;

    public List<MethodParseData> methodDataList;

    public ClassParseData(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public static ClassParseData valid(PsiClass psiClass) {
        ClassParseData data = new ClassParseData(psiClass);
        data.valid = true;
        return data;
    }

    public static ClassParseData invalid(PsiClass psiClass) {
        ClassParseData data = new ClassParseData(psiClass);
        data.valid = false;
        return data;
    }

    public List<Api> getApis() {
        if (methodDataList == null || methodDataList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Api> apis = Lists.newArrayList();
        for (MethodParseData methodApiInfo : methodDataList) {
            apis.addAll(methodApiInfo.getApis());
        }
        return apis;
    }
}
