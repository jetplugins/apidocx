package io.yapix.parse.parser;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import io.yapix.model.HttpMethod;
import io.yapix.parse.constant.SpringConstants;
import io.yapix.parse.constant.WxbConstants;
import io.yapix.parse.model.PathParseInfo;
import io.yapix.parse.util.PsiAnnotationUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

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
        PathParseInfo pathInfo = null;
        PsiAnnotation requestMapping = PsiAnnotationUtils.getAnnotation(method, SpringConstants.RequestMapping);
        if (requestMapping != null) {
            pathInfo = parseRequestMappingAnnotation(requestMapping);
        } else {
            for (Entry<HttpMethod, String> item : simpleMappings.entrySet()) {
                PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(method, item.getValue());
                if (annotation != null) {
                    pathInfo = parseSimpleMappingAnnotation(item.getKey(), annotation);
                    break;
                }
            }
        }

        // 公司内部定制@ApiVersion注解
        if (pathInfo != null && CollectionUtils.isNotEmpty(pathInfo.getPaths())) {
            wxbPathHandle(method, pathInfo);
        }
        return pathInfo;
    }

    /**
     * 小宝定制
     */
    private static void wxbPathHandle(PsiMethod method, PathParseInfo pathInfo) {
        PsiAnnotation apiVersion = PsiAnnotationUtils.getAnnotation(method, WxbConstants.ApiVersion);
        if (apiVersion == null && method.getContainingClass() != null) {
            apiVersion = PsiAnnotationUtils.getAnnotation(method.getContainingClass(), WxbConstants.ApiVersion);
        }
        if (apiVersion == null) {
            return;
        }
        Long version = AnnotationUtil.getLongAttributeValue(apiVersion, PsiAnnotation.DEFAULT_REFERENCED_METHOD_NAME);
        if (version == null) {
            return;
        }

        List<String> paths = Lists.newArrayListWithCapacity(pathInfo.getPaths().size());
        for (String p : pathInfo.getPaths()) {
            String path = p.replaceAll("\\{\\s*version\\s*}", "v" + version);
            paths.add(path);
        }
        pathInfo.setPaths(paths);
    }

    /**
     * 解析@RequestMapping的信息
     */
    public static PathParseInfo parseRequestMappingAnnotation(PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        List<HttpMethod> methods = PsiAnnotationUtils.getStringArrayAttribute(annotation, "method").stream()
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
        List<String> paths = PsiAnnotationUtils.getStringArrayAttribute(annotation, "path");
        if (paths.isEmpty()) {
            paths = PsiAnnotationUtils.getStringArrayAttribute(annotation, "value");
        }
        return paths;
    }


}
