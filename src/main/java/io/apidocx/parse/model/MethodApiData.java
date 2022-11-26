package io.apidocx.parse.model;

import com.intellij.psi.PsiMethod;
import io.apidocx.model.Api;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 方法解析数据
 */
@Data
public class MethodApiData {

    /**
     * 是否是有效的接口方法
     */
    private boolean valid = true;

    /**
     * 目标方法
     */
    private PsiMethod method;


    /**
     * 指定的接口名称
     */
    private String declaredApiSummary;

    /**
     * 接口列表
     */
    private List<Api> apis;

    public List<Api> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }
}
