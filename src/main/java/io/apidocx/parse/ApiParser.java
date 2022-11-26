package io.apidocx.parse;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.apidocx.parse.util.PsiDocCommentUtils.findTagByName;
import static java.util.Objects.isNull;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import io.apidocx.config.ApidocxConfig;
import io.apidocx.model.Api;
import io.apidocx.model.Property;
import io.apidocx.parse.constant.DocumentTags;
import io.apidocx.parse.constant.SpringConstants;
import io.apidocx.parse.model.ClassParseData;
import io.apidocx.parse.model.ControllerApiInfo;
import io.apidocx.parse.model.MethodParseData;
import io.apidocx.parse.model.PathParseInfo;
import io.apidocx.parse.model.RequestParseInfo;
import io.apidocx.parse.parser.ParseHelper;
import io.apidocx.parse.parser.PathParser;
import io.apidocx.parse.parser.RequestParser;
import io.apidocx.parse.parser.ResponseParser;
import io.apidocx.parse.util.PathUtils;
import io.apidocx.parse.util.PsiAnnotationUtils;
import io.apidocx.parse.util.PsiUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Api接口解析器
 */
public class ApiParser {

    private static final Gson gson = new Gson();
    private final RequestParser requestParser;
    private final ResponseParser responseParser;
    private final ParseHelper parseHelper;
    private final Project project;
    private final Module module;

    public ApiParser(Project project, Module module, ApidocxConfig settings) {
        checkNotNull(project);
        checkNotNull(module);
        checkNotNull(settings);
        this.project = project;
        this.module = module;
        this.requestParser = new RequestParser(project, module, settings);
        this.responseParser = new ResponseParser(project, module, settings);
        this.parseHelper = new ParseHelper(project, module);
    }

    /**
     * 解析接口
     */
    public MethodParseData parse(PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        ControllerApiInfo controllerApiInfo = parseController(psiClass);
        return parseMethod(controllerApiInfo, method);
    }

    /**
     * 解析接口
     */
    public ClassParseData parse(PsiClass psiClass) {
        if (!isNeedParseController(psiClass) || findTagByName(psiClass, DocumentTags.Ignore) != null) {
            return ClassParseData.invalid(psiClass);
        }

        // 获得待处理方法
        List<PsiMethod> methods = filterPsiMethods(psiClass);
        if (methods.isEmpty()) {
            return ClassParseData.valid(psiClass);
        }

        // 解析
        ControllerApiInfo controllerApiInfo = parseController(psiClass);
        List<MethodParseData> methodDataList = Lists.newArrayListWithExpectedSize(methods.size());
        for (PsiMethod method : methods) {
            MethodParseData methodData = parseMethod(controllerApiInfo, method);
            methodDataList.add(methodData);
        }

        ClassParseData data = ClassParseData.valid(psiClass);
        data.declaredCategory = controllerApiInfo.getDeclareCategory();
        data.methodDataList = methodDataList;
        return data;
    }

    /**
     * 判断是否是控制类或接口
     */
    private boolean isNeedParseController(PsiClass psiClass) {
        // 接口是为了满足接口继承的情况
        boolean isController = psiClass.isInterface()
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.RestController) != null
                || PsiAnnotationUtils.getAnnotation(psiClass, SpringConstants.Controller) != null;
        if (isController) {
            return true;
        }

        // 支持一级组合继承@RestController、@Controller的情况
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            PsiClass thePsiClass = PsiUtils.findPsiClass(project, module, annotation.getQualifiedName());
            if (isNull(thePsiClass)) {
                continue;
            }
            isController = PsiAnnotationUtils.getAnnotation(thePsiClass, SpringConstants.RestController) != null
                    || PsiAnnotationUtils.getAnnotation(thePsiClass, SpringConstants.Controller) != null;
            if (isController) {
                break;
            }
        }
        return isController;
    }

    /**
     * 获取待处理的方法列表
     */
    private List<PsiMethod> filterPsiMethods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getAllMethods())
                .filter(m -> {
                    PsiModifierList modifier = m.getModifierList();
                    return !modifier.hasModifierProperty(PsiModifier.PRIVATE)
                            && !modifier.hasModifierProperty(PsiModifier.STATIC);
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
        info.setDeclareCategory(parseHelper.getDeclareApiCategory(controller));
        info.setCategory(info.getDeclareCategory());
        if (StringUtils.isEmpty(info.getCategory())) {
            info.setCategory(parseHelper.getDefaultApiCategory(controller));
        }
        return info;
    }

    /**
     * 解析某个方法的接口信息
     */
    private MethodParseData parseMethod(ControllerApiInfo controllerInfo, PsiMethod method) {
        if (findTagByName(method, DocumentTags.Ignore) != null) {
            return MethodParseData.invalid(method);
        }
        // 1.解析路径信息: @XxxMapping
        PathParseInfo mapping = PathParser.parse(method);
        if (mapping == null || mapping.getPaths() == null) {
            return MethodParseData.invalid(method);
        }

        // 2.解析方法上信息: 请求、响应等.
        Api methodApi = doParseMethod(method, mapping);

        // 3.合并信息
        List<Api> apis = mapping.getPaths().stream().map(path -> {
            Api api = methodApi;
            if (mapping.getPaths().size() > 1) {
                api = gson.fromJson(gson.toJson(methodApi), Api.class);
            }
            api.setMethod(mapping.getMethod());
            api.setPath(PathUtils.path(controllerInfo.getPath(), path));
            api.setCategory(controllerInfo.getCategory());
            return api;
        }).collect(Collectors.toList());

        MethodParseData data = MethodParseData.valid(method);
        data.declaredApiSummary = methodApi.getSummary();
        data.apis = apis;
        return data;
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
        api.setTags(parseHelper.getApiTags(method));
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
