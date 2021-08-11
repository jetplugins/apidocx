package com.github.jetplugins.yapix.parse;

import static com.github.jetplugins.yapix.constant.JavaConstant.HttpServletRequest;
import static com.github.jetplugins.yapix.constant.JavaConstant.HttpServletResponse;
import static com.github.jetplugins.yapix.constant.SpringConstants.BindingResult;
import static com.github.jetplugins.yapix.constant.SpringConstants.MultipartFile;
import static com.github.jetplugins.yapix.constant.SpringConstants.PathVariable;
import static com.github.jetplugins.yapix.constant.SpringConstants.RequestAttribute;
import static com.github.jetplugins.yapix.constant.SpringConstants.RequestBody;
import static com.github.jetplugins.yapix.constant.SpringConstants.RequestHeader;
import static com.github.jetplugins.yapix.constant.SpringConstants.RequestParam;

import com.github.jetplugins.yapix.constant.SpringConstants;
import com.github.jetplugins.yapix.constant.SwaggerConstants;
import com.github.jetplugins.yapix.model.Item;
import com.github.jetplugins.yapix.model.ParameterInConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 参数解析工具类
 */
public class RequestParser {

    private RequestParser() {
    }

    /**
     * 解析普通参数
     */
    public static List<Item> parseParameters(PsiMethod method) {
        List<PsiParameter> parameters = filterIgnoreParameter(method.getParameterList().getParameters());
        parameters = parameters.stream()
                .filter(p -> p.getAnnotation(RequestBody) == null)
                .filter(p -> !MultipartFile.equals(p.getType().getCanonicalText()))
                .collect(Collectors.toList());

        List<Item> items = Lists.newArrayListWithExpectedSize(parameters.size());
        for (PsiParameter parameter : parameters) {
            Item item = doParseParameter(parameter);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * 过滤无需处理的参数
     */
    private static List<PsiParameter> filterIgnoreParameter(PsiParameter[] parameters) {
        Set<String> ignoreTypes = Sets.newHashSet(HttpServletRequest, HttpServletResponse, BindingResult);
        return Arrays.stream(parameters)
                .filter(p -> {
                    String type = p.getType().getCanonicalText();
                    if (ignoreTypes.contains(type)) {
                        return false;
                    }
                    PsiAnnotation ignoreAnnotation = p.getAnnotation(SwaggerConstants.API_IGNORE);
                    if (ignoreAnnotation != null) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    /**
     * 解析单个参数
     */
    private static Item doParseParameter(PsiParameter parameter) {
        PsiAnnotation requestBodyAnnotation = parameter.getAnnotation(SpringConstants.RequestBody);
        if (requestBodyAnnotation != null) {
            return null;
        }

        Item item = new Item();
        // 字段名
        boolean required = false;
        String name = null;
        String in = ParameterInConstants.QUERY;
        Map<String, String> targets = new LinkedHashMap<>();

        targets.put(RequestParam, ParameterInConstants.QUERY);
        targets.put(RequestAttribute, ParameterInConstants.QUERY);
        targets.put(RequestHeader, ParameterInConstants.HEADER);
        targets.put(PathVariable, ParameterInConstants.PATH);
        PsiAnnotation annotation = null;

        for (Entry<String, String> target : targets.entrySet()) {
            annotation = parameter.getAnnotation(target.getKey());
            if (annotation != null) {
                in = target.getKey();
                break;
            }
        }
        if (annotation != null) {
            name = AnnotationUtil.getStringAttributeValue(annotation, "name");
            if (StringUtils.isEmpty(name)) {
                name = AnnotationUtil.getStringAttributeValue(annotation, "value");
            }
            required = AnnotationUtil.getBooleanAttributeValue(annotation, "required");
        }
        if (StringUtils.isEmpty(name)) {
            name = parameter.getName();
        }
        item.setName(name);
        item.setType(ParseHelper.getDataType(parameter.getType()));
        item.setIn(in);
        item.setRequired(required);
        return item;
    }


    /**
     * 解析请求体类型
     */
    public static String parseRequestBodyType(PsiMethod method) {
        List<PsiParameter> parameters = filterIgnoreParameter(method.getParameterList().getParameters());
        Optional<PsiParameter> bodyParamOpt = parameters.stream().filter(p -> p.getAnnotation(RequestBody) != null)
                .findFirst();
        if (bodyParamOpt.isPresent()) {
            return "json";
        }
        Optional<PsiParameter> fileParamOpt = parameters.stream()
                .filter(p -> MultipartFile.equals(p.getType().getCanonicalText()))
                .findFirst();
        if (fileParamOpt.isPresent()) {
            return "form-data";
        }
        return "x-www-form-urlencoded";
    }

    public static Item parseRequestBody(PsiMethod method, String bodyType) {
        if ("json".equals(bodyType)) {

        }
        return null;
    }


}
