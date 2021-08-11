package com.github.jetplugins.yapix.parse;

import com.github.jetplugins.yapix.model.Item;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * 方法返回值解析
 */
public class ResponseParser {

    private String wrapDataType;

    public ResponseParser(String wrapDataType) {
        this.wrapDataType = wrapDataType;
    }

    public Item parse(PsiMethod psiMethod) {
        PsiType returnType = psiMethod.getReturnType();
        if (returnType == null) {
            return null;
        }
        return parseType(psiMethod.getProject(), returnType, 0);
    }

    private Item parseType(Project project, PsiType type, int idx) {
        String[] types = PsiUtils.getPsiTypeClasses(type);
        return parseTypes(project, types, idx);
    }

    private Item parseTypes(Project project, String[] types, int idx) {
        String type = types[idx];
        PsiClass typeClass = JavaPsiFacade.getInstance(project).findClass(type, GlobalSearchScope.allScope(project));
        if (typeClass == null) {
            return null;
        }
        Item item = new Item();
        item.setType(TypeUtils.getType(null));
        if (item.isArrayType()) {
            // 数组
        }
        if (item.isObjectType()) {
            PsiTypeParameter[] typeParameters = typeClass.getTypeParameters();
            // 对象
            PsiField[] fields = typeClass.getAllFields();
            for (PsiField field : fields) {
                if (PsiUtils.isFieldSkip(field)) {
                    continue;
                }
            }
        }
        return item;
    }

}
