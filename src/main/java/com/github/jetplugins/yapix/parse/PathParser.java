package com.github.jetplugins.yapix.parse;

import com.github.jetplugins.yapix.constant.HttpMethodConstant;
import com.github.jetplugins.yapix.constant.SpringConstants;
import com.github.jetplugins.yapix.util.PsiAnnotationSearchUtil;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiMethod;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 路径请求相关工具解析类
 */
public class PathParser {

    private static final Map<String, String> simpleMappings = new LinkedHashMap<>();

    static {
        simpleMappings.put(HttpMethodConstant.GET, SpringConstants.GetMapping);
        simpleMappings.put(HttpMethodConstant.POST, SpringConstants.PostMapping);
        simpleMappings.put(HttpMethodConstant.PUT, SpringConstants.PutMapping);
        simpleMappings.put(HttpMethodConstant.DELETE, SpringConstants.DeleteMapping);
        simpleMappings.put(HttpMethodConstant.PATCH, SpringConstants.PatchMapping);
    }

    /**
     * 解析请求映射信息
     */
    public static PathInfo parse(PsiMethod method) {
        PsiAnnotation requestMapping = PsiAnnotationSearchUtil.findAnnotation(method, SpringConstants.RequestMapping);
        if (requestMapping != null) {
            return parseRequestMappingAnnotation(requestMapping);
        }

        for (Entry<String, String> item : simpleMappings.entrySet()) {
            PsiAnnotation annotation = PsiAnnotationSearchUtil.findAnnotation(method, item.getValue());
            if (annotation != null) {
                return parseSimpleMappingAnnotation(item.getKey(), annotation);
            }
        }
        return null;
    }

    /**
     * 解析@RequestMapping的信息
     */
    public static PathInfo parseRequestMappingAnnotation(PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        List<String> methods = getStringArrayAttribute(annotation, "method");
        if (methods.isEmpty()) {
            // 未指定方法，那么默认
            methods.add(HttpMethodConstant.GET);
        }
        PathInfo mapping = new PathInfo();
        mapping.setMethod(methods.get(0));
        mapping.setPaths(paths);
        return mapping;
    }

    /**
     * 解析其他注解信息，例如: @GetMapping, @PostMapping, ....
     */
    public static PathInfo parseSimpleMappingAnnotation(String method, PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        PathInfo info = new PathInfo();
        info.setPaths(paths);
        info.setMethod(method);
        return info;
    }

    private static List<String> getPaths(PsiAnnotation annotation) {
        List<String> paths = getStringArrayAttribute(annotation, "path");
        if (paths.isEmpty()) {
            paths = getStringArrayAttribute(annotation, "value");
        }
        return paths;
    }

    private static List<String> getStringArrayAttribute(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue memberValue = annotation.findAttributeValue(attribute);
        if (memberValue == null) {
            return Collections.emptyList();
        }

        List<String> paths = Lists.newArrayListWithExpectedSize(1);
        if (memberValue instanceof PsiArrayInitializerMemberValue) {
            PsiArrayInitializerMemberValue theMemberValue = (PsiArrayInitializerMemberValue) memberValue;
            PsiAnnotationMemberValue[] values = theMemberValue.getInitializers();
            for (PsiAnnotationMemberValue value : values) {
                String text = value.getText();
                text = text.substring(1, text.length() - 1);
                paths.add(text);
            }
        } else {
            String text = memberValue.getText();
            text = text.substring(1, text.length() - 1);
            paths.add(text);
        }
        return paths;
    }

    /**
     * 请求路径和方法信息
     */
    public static class PathInfo {

        private String method;

        private List<String> paths;

        public String getPath() {
            return paths != null && paths.size() > 0 ? paths.get(0) : null;
        }

        //-----------------generated---------------------//


        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }
    }
}
