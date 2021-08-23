package io.yapix.parse;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.yapix.config.DefaultConstants.FILE_NAME;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import io.yapix.config.YapixConfig;
import io.yapix.model.Api;
import io.yapix.parse.constant.SpringConstants;
import io.yapix.parse.model.ControllerApiInfo;
import io.yapix.parse.model.PathParseInfo;
import io.yapix.parse.model.RequestParseInfo;
import io.yapix.parse.parser.ParseHelper;
import io.yapix.parse.parser.PathParser;
import io.yapix.parse.parser.RequestParser;
import io.yapix.parse.parser.ResponseParser;
import io.yapix.parse.util.PathUtils;
import io.yapix.parse.util.PropertiesLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 接口参数解析器
 *
 * @see #parse(PsiClass, PsiMethod)
 */
public class ApiParser {

    private static final Gson gson = new Gson();
    private final RequestParser requestParser;
    private final ResponseParser responseParser;

    public ApiParser(YapixConfig settings) {
        checkNotNull(settings);
        settings = getMergeSettings(settings);
        this.requestParser = new RequestParser(settings);
        this.responseParser = new ResponseParser(settings);
    }

    /**
     * 解析接口
     */
    public List<Api> parse(PsiClass controller, PsiMethod selectMethod) {
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
            if (selectMethod != null && method != selectMethod) {
                continue;
            }
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

        RequestParseInfo requestInfo = requestParser.parse(method, mapping.getMethod());
        api.setParameters(requestInfo.getParameters());
        api.setRequestBodyType(requestInfo.getRequestBodyType());
        api.setRequestBody(requestInfo.getRequestBody());
        api.setRequestBodyForm(requestInfo.getRequestBodyForm());
        api.setResponses(responseParser.parse(method));
        return api;
    }

    /**
     * 合并内部配置
     */
    private YapixConfig getMergeSettings(YapixConfig settings) {
        Properties properties = PropertiesLoader.getProperties(FILE_NAME);
        YapixConfig internal = YapixConfig.fromProperties(properties);

        YapixConfig config = new YapixConfig();
        config.setYapiProjectId(settings.getYapiProjectId());
        config.setRap2ProjectId(settings.getRap2ProjectId());
        config.setEolinkerProjectId(settings.getEolinkerProjectId());
        config.setReturnWrapType(settings.getReturnWrapType());

        List<String> returnUnwrapTypes = Lists.newArrayList();
        returnUnwrapTypes.addAll(internal.getReturnUnwrapTypes());
        if (settings.getReturnUnwrapTypes() != null) {
            returnUnwrapTypes.addAll(settings.getReturnUnwrapTypes());
        }
        config.setReturnUnwrapTypes(returnUnwrapTypes);

        List<String> parameterIgnoreTypes = Lists.newArrayList();
        if (settings.getParameterIgnoreTypes() != null) {
            config.setReturnUnwrapTypes(returnUnwrapTypes);
            parameterIgnoreTypes.addAll(settings.getParameterIgnoreTypes());
        }
        parameterIgnoreTypes.addAll(internal.getParameterIgnoreTypes());
        config.setParameterIgnoreTypes(parameterIgnoreTypes);
        return config;
    }
}
