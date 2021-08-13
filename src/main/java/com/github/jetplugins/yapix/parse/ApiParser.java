package com.github.jetplugins.yapix.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.jetplugins.yapix.model.Api;
import com.github.jetplugins.yapix.parse.constant.SpringConstants;
import com.github.jetplugins.yapix.parse.model.ControllerApiInfo;
import com.github.jetplugins.yapix.parse.model.PathParseInfo;
import com.github.jetplugins.yapix.parse.model.RequestParseInfo;
import com.github.jetplugins.yapix.parse.parser.ParseHelper;
import com.github.jetplugins.yapix.parse.parser.PathParser;
import com.github.jetplugins.yapix.parse.parser.RequestParser;
import com.github.jetplugins.yapix.parse.parser.ResponseParser;
import com.github.jetplugins.yapix.parse.util.PathUtils;
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
 *
 * @see #parse(PsiClass)
 */
public class ApiParser {

    private final ApiParseSettings settings;
    private static final Gson gson = new Gson();

    public ApiParser(ApiParseSettings settings) {
        checkNotNull(settings);
        this.settings = settings;
    }

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
        PsiAnnotation annotation = controller.getAnnotation(SpringConstants.RequestMapping);
        if (annotation != null) {
            PathParseInfo mapping = PathParser.parseRequestMappingAnnotation(annotation);
            path = mapping.getPath();
        }
        ControllerApiInfo info = new ControllerApiInfo();
        info.setPath(PathUtils.path(path));
        info.setCategory(ParseHelper.getApiCategory(controller));
        return info;
    }

    /**
     * 解析某个方法的接口信息
     */
    private List<Api> parseMethod(ControllerApiInfo controllerApiInfo, PsiMethod method) {
        List<Api> apis = Lists.newArrayList();
        PathParseInfo mapping = PathParser.parse(method);
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
    private Api doParseMethod(PsiMethod method, PathParseInfo mapping) {
        Api api = new Api();
        api.setMethod(mapping.getMethod());
        api.setSummary(ParseHelper.getApiSummary(method));
        api.setDescription(ParseHelper.getApiDescription(method));
        api.setDeprecated(ParseHelper.isDeprecated(method));

        RequestParseInfo requestInfo = RequestParser.parse(method, mapping.getMethod());
        api.setParameters(requestInfo.getParameters());
        api.setRequestBodyType(requestInfo.getRequestBodyType());
        api.setRequestBody(requestInfo.getRequestBody());
        api.setRequestBodyForm(requestInfo.getRequestBodyForm());
        api.setResponses((new ResponseParser(settings)).parse(method));
        return api;
    }

}
