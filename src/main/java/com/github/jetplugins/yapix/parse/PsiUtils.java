package com.github.jetplugins.yapix.parse;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;

public class PsiUtils {

    private PsiUtils() {
    }

    public static String[] getPsiTypeClasses(PsiType psiType) {
        String[] types = psiType.getCanonicalText().split("<");
        types[types.length - 1] = types[types.length - 1].replaceAll(">", "");
        return types;
    }


    public static boolean isFieldSkip(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null && modifierList.hasExplicitModifier(PsiModifier.STATIC)) {
            return true;
        }
        return false;
    }
}
