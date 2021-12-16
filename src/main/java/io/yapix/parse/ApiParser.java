package io.yapix.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.model.Property;
import io.yapix.parse.constant.SpringConstants;
import io.yapix.parse.model.ControllerApiInfo;
import io.yapix.parse.model.PathParseInfo;
import io.yapix.parse.model.RequestParseInfo;
import io.yapix.parse.parser.ParseHelper;
import io.yapix.parse.parser.PathParser;
import io.yapix.parse.parser.RequestParser;
import io.yapix.parse.parser.ResponseParser;
import io.yapix.parse.util.PathUtils;
import io.yapix.parse.util.PsiAnnotationUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Api接口解析器
 *
 * @see #parse(PsiClass, PsiMethod)
 */
public class ApiParser {

    private static final Gson gson = new Gson();
    private final RequestParser requestParser;
    private final ResponseParser responseParser;
    private final ParseHelper parseHelper;

    public ApiParser(Project project, Module module, YapixConfig settings) {
        checkNotNull(project);
        checkNotNull(module);
        checkNotNull(settings);
        YapixConfig mergedSettings = settings.getMergedInternalConfig();
        this.requestParser = new RequestParser(project, module, mergedSettings);
        this.responseParser = new ResponseParser(project, module, mergedSettings);
        this.parseHelper = new ParseHelper(project, module);
    }

    /**
     * 解析接口
     */
    public List<Api> parse(PsiClass psiClass, PsiMethod selectMethod) {
        if (!isNeedParseController(psiClass)) {
            return Collections.emptyList();
        }
        // 获得待处理方法
        List<PsiMethod> methods = filterPsiMethods(psiClass, selectMethod);
        if (methods.isEmpty()) {
            return Collections.emptyList();
        }

        // 解析
        ControllerApiInfo controllerApiInfo = parseController(psiClass);
        List<Api> apis = Lists.newArrayListWithExpectedSize(methods.size());
        for (PsiMethod method : methods) {
            List<Api> methodApis = parseMethod(controllerApiInfo, method);
            apis.addAll(methodApis);
        }
        return apis;
    }

    /**
     * 判断是否是控制类或接口
     */
    private boolean isNeedParseController(PsiClass psiClass) {
        // 接口是为了满足接口继承的情况
        return psiClass.isInterface()
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.RestController) != null
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.Controller) != null;
    }

    /**
     * 获取待处理的方法列表
     */
    private List<PsiMethod> filterPsiMethods(PsiClass psiClass, PsiMethod selectMethod) {
        return Arrays.stream(psiClass.getAllMethods())
                .filter(m -> {
                    PsiModifierList modifier = m.getModifierList();
                    return !modifier.hasModifierProperty("private")
                            && !modifier.hasModifierProperty("static")
                            && (selectMethod == null || m == selectMethod);
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析类级别信息
     */
    private ControllerApiInfo parseController(PsiClass controller) {
        String path = null;
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotationIncludeExtends(controller,
                SpringConstants.RequestMapping);
        if (annotation != null) {
            PathParseInfo mapping = PathParser.parseRequestMappingAnnotation(annotation);
            path = mapping.getPath();
        }
        ControllerApiInfo info = new ControllerApiInfo();
        info.setPath(PathUtils.path(path));
        info.setCategory(parseHelper.getApiCategory(controller));
        return info;
    }

    /**
     * 解析某个方法的接口信息
     */
    private List<Api> parseMethod(ControllerApiInfo controllerInfo, PsiMethod method) {
        // 1.解析路径信息: @XxxMapping
        PathParseInfo mapping = PathParser.parse(method);
        if (mapping == null || mapping.getPaths() == null) {
            return Collections.emptyList();
        }

        // 2.解析方法上信息: 请求、响应等.
        Api methodApi = doParseMethod(method, mapping);

        // 3.合并信息
        return mapping.getPaths().stream().map(path -> {
            Api api = methodApi;
            if (mapping.getPaths().size() > 1) {
                api = gson.fromJson(gson.toJson(methodApi), Api.class);
            }
            api.setMethod(mapping.getMethod());
            api.setPath(PathUtils.path(controllerInfo.getPath(), path));
            api.setCategory(controllerInfo.getCategory());
            return api;
        }).collect(Collectors.toList());
    }

    /**
     * 解析方法的通用信息，除请求路径、请求方法外.
     */
    private Api doParseMethod(PsiMethod method, PathParseInfo mapping) {
        Api api = new Api();
        // 基本信息
        api.setMethod(mapping.getMethod());
        api.setSummary(parseHelper.getApiSummary(method));
        api.setDescription(parseHelper.getApiDescription(method));
        api.setDeprecated(parseHelper.getApiDeprecated(method));
        // 请求信息
        RequestParseInfo requestInfo = requestParser.parse(method, mapping.getMethod());
        api.setParameters(requestInfo.getParameters());
        api.setRequestBodyType(requestInfo.getRequestBodyType());
        api.setRequestBody(requestInfo.getRequestBody());
        api.setRequestBodyForm(requestInfo.getRequestBodyForm());
        // 响应信息
        Property response = responseParser.parse(method);
        api.setResponses(response);
        return api;
    }
}
