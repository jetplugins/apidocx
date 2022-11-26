package io.apidocx.parse.model;

import com.google.common.collect.Lists;
import io.apidocx.model.Api;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 接口解析结果
 */
@Data
public class ClassApiData {

    /**
     * 有效的类
     */
    private boolean valid = true;

    /**
     * 声明的分类名称
     */
    private String declaredCategory;

    private List<MethodApiData> methodDataList;

    public List<Api> getApis() {
        if (methodDataList == null || methodDataList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Api> apis = Lists.newArrayList();
        for (MethodApiData methodApiInfo : methodDataList) {
            apis.addAll(methodApiInfo.getApis());
        }
        return apis;
    }
}
