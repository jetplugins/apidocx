package io.yapix.parse.util;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.Objects;

public class PsiLinkUtils {

    /**
     * @description: 获得link 备注
     * @param: [remark, project, field]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/18
     */
    public static String getLinkRemark(String remark, PsiField field) {
        Project project = field.getProject();
        // 尝试获得@link 的常量定义
        if (Objects.isNull(field.getDocComment())) {
            return remark;
        }
        String[] linkString = field.getDocComment().getText().split("@link");
        if (linkString.length > 1) {
            //说明有link
            String linkAddress = linkString[1].split("}")[0].trim();
            PsiClass psiClassLink = JavaPsiFacade.getInstance(project)
                    .findClass(linkAddress, GlobalSearchScope.allScope(project));
            if (Objects.isNull(psiClassLink)) {
                //可能没有获得全路径，尝试获得全路径
                String[] importPaths = field.getParent().getContext().getText().split("import");
                if (importPaths.length > 1) {
                    for (String importPath : importPaths) {
                        importPath = importPath.split(";")[0];
                        if (importPath.contains(linkAddress.split("\\.")[0])) {
                            linkAddress = importPath.split(linkAddress.split("\\.")[0])[0] + linkAddress;
                            psiClassLink = JavaPsiFacade.getInstance(project)
                                    .findClass(linkAddress.trim(), GlobalSearchScope.allScope(project));
                            break;
                        }
                    }
                }
                if (Objects.isNull(psiClassLink)) {
                    //如果是同包情况
                    linkAddress =
                            ((PsiJavaFileImpl) ((PsiClassImpl) field.getParent()).getContext()).getPackageName() + "."
                                    + linkAddress;
                    psiClassLink = JavaPsiFacade.getInstance(project)
                            .findClass(linkAddress, GlobalSearchScope.allScope(project));
                }
                //如果小于等于一为不存在import，不做处理
            }
            if (Objects.nonNull(psiClassLink)) {
                //说明获得了link 的class
                PsiField[] linkFields = psiClassLink.getFields();
                if (linkFields.length > 0) {
                    remark += "," + psiClassLink.getName() + "[";
                    for (int i = 0; i < linkFields.length; i++) {
                        PsiField psiField = linkFields[i];
                        if (i > 0) {
                            remark += ",";
                        }
                        // 先获得名称
                        remark += psiField.getName();
                        // 后获得value,通过= 来截取获得，第二个值，再截取;
                        String[] splitValue = psiField.getText().split("=");
                        if (splitValue.length > 1) {
                            String value = splitValue[1].split(";")[0];
                            remark += ":" + value;
                        }
                        String filedValue = PsiDocCommentUtils.getDocCommentTitle(psiField);
                        if (!Strings.isNullOrEmpty(filedValue)) {
                            remark += "(" + filedValue + ")";
                        }
                    }
                    remark += "]";
                }
            }
        }
        return remark;
    }

}
