package com.github.jetplugins.yapix.parse;

import com.github.jetplugins.yapix.constant.HttpMethodConstant;
import com.github.jetplugins.yapix.constant.SpringConstants;
import com.github.jetplugins.yapix.model.Api;
import com.github.jetplugins.yapix.parse.PathParser.PathInfo;
import com.github.jetplugins.yapix.util.PathUtils;
import com.github.jetplugins.yapix.util.PsiAnnotationSearchUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口参数解析器
 */
public class ApiParser {

    private static final Gson gson = new Gson();

    /**
     * 解析接口
     */
    public List<Api> parse(PsiClass controller) {
        // 过滤不处理得方法
        List<PsiMethod> methods = Arrays.stream(controller.getMethods())
                .filter(m -> {
                    PsiModifierList modifier = m.getModifierList();
                    return !modifier.hasModifierProperty("private") && !modifier.hasModifierProperty("static");
                })
                .collect(Collectors.toList());
        if (methods.isEmpty()) {
            return Collections.emptyList();
        }

        // 解析类级别数据
        ControllerApiInfo controllerApiInfo = parseController(controller);
        List<Api> apis = Lists.newArrayListWithExpectedSize(methods.size());
        for (PsiMethod method : methods) {
            // 解析某个方法
            List<Api> methodApis = parseMethod(controllerApiInfo, method);
            apis.addAll(methodApis);
        }
        return apis;
    }

    /**
     * 解析类级别信息
     */
    private ControllerApiInfo parseController(PsiClass controller) {
        // 路径
        String path = null;
        PsiAnnotation annotation = PsiAnnotationSearchUtil.findAnnotation(controller, SpringConstants.RequestMapping);
        if (annotation != null) {
            PathInfo mapping = PathParser.parseRequestMappingAnnotation(annotation);
            path = mapping.getPath();
        }
        ControllerApiInfo info = new ControllerApiInfo();
        info.setPath(PathUtils.path(path));
        info.setCategory(ParseHelper.apiCategory(controller));
        return info;
    }

    /**
     * 解析某个方法的接口信息
     */
    private List<Api> parseMethod(ControllerApiInfo controllerApiInfo, PsiMethod method) {
        List<Api> apis = Lists.newArrayList();
        PathInfo mapping = PathParser.parse(method);
        if (mapping == null || mapping.getPaths() == null) {
            return apis;
        }

        Api methodApi = doParseMethod(method, mapping);
        for (String path : mapping.getPaths()) {
            Api api = gson.fromJson(gson.toJson(methodApi), Api.class);
            api.setPath(path);
            api.setMethod(mapping.getMethod());
            mergeLevels(api, controllerApiInfo);
            apis.add(api);
        }
        return apis;
    }

    private void mergeLevels(Api api, ControllerApiInfo controllerInfo) {
        String path = PathUtils.path(controllerInfo.getPath(), api.getPath());
        api.setPath(path);
        api.setCategory(controllerInfo.getCategory());
    }

    /**
     * 解析方法的通用信息，出path/method外
     */
    private Api doParseMethod(PsiMethod method, PathInfo mapping) {
        Api api = new Api();
        api.setMethod(mapping.getMethod());
        api.setSummary(ParseHelper.apiSummary(method));
        api.setDescription(ParseHelper.apiDescription(method));
        api.setDeprecated(ParseHelper.isDeprecated(method));
        api.setParameters(RequestParser.parseParameters(method));
        List<String> bodyMethods = Lists
                .newArrayList(HttpMethodConstant.POST, HttpMethodConstant.PUT, HttpMethodConstant.PATCH);
        if (bodyMethods.contains(api.getMethod())) {
            api.setRequestBodyType(RequestParser.parseRequestBodyType(method));
            api.setRequestBody(RequestParser.parseRequestBody(method, api.getRequestBodyType()));
        }
        api.setResponses((new ResponseParser(null)).parse(method));
        return api;
    }


}
