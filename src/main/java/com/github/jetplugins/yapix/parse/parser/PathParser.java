package com.github.jetplugins.yapix.parse.parser;

import com.github.jetplugins.yapix.model.HttpMethod;
import com.github.jetplugins.yapix.parse.constant.SpringConstants;
import com.github.jetplugins.yapix.parse.model.PathParseInfo;
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
import java.util.stream.Collectors;

/**
 * 路径请求相关工具解析类
 */
public class PathParser {

    private static final Map<HttpMethod, String> simpleMappings = new LinkedHashMap<>();

    static {
        simpleMappings.put(HttpMethod.GET, SpringConstants.GetMapping);
        simpleMappings.put(HttpMethod.POST, SpringConstants.PostMapping);
        simpleMappings.put(HttpMethod.PUT, SpringConstants.PutMapping);
        simpleMappings.put(HttpMethod.DELETE, SpringConstants.DeleteMapping);
        simpleMappings.put(HttpMethod.PATCH, SpringConstants.PatchMapping);
    }

    /**
     * 解析请求映射信息
     */
    public static PathParseInfo parse(PsiMethod method) {
        PsiAnnotation requestMapping = method.getAnnotation(SpringConstants.RequestMapping);
        if (requestMapping != null) {
            return parseRequestMappingAnnotation(requestMapping);
        }

        for (Entry<HttpMethod, String> item : simpleMappings.entrySet()) {
            PsiAnnotation annotation = method.getAnnotation(item.getValue());
            if (annotation != null) {
                return parseSimpleMappingAnnotation(item.getKey(), annotation);
            }
        }
        return null;
    }

    /**
     * 解析@RequestMapping的信息
     */
    public static PathParseInfo parseRequestMappingAnnotation(PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        List<HttpMethod> methods = getStringArrayAttribute(annotation, "method").stream()
                .map(HttpMethod::of).collect(Collectors.toList());
        if (methods.isEmpty()) {
            // 未指定方法，那么默认
            methods.add(HttpMethod.GET);
        }
        PathParseInfo mapping = new PathParseInfo();
        mapping.setMethod(methods.get(0));
        mapping.setPaths(paths);
        return mapping;
    }

    /**
     * 解析其他注解信息，例如: @GetMapping, @PostMapping, ....
     */
    public static PathParseInfo parseSimpleMappingAnnotation(HttpMethod method, PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        PathParseInfo info = new PathParseInfo();
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

}
