package io.yapix.parse.parser;

import static io.yapix.parse.constant.JavaConstants.HttpServletRequest;
import static io.yapix.parse.constant.JavaConstants.HttpServletResponse;
import static io.yapix.parse.constant.SpringConstants.BindingResult;
import static io.yapix.parse.constant.SpringConstants.MultipartFile;
import static io.yapix.parse.constant.SpringConstants.Pageable;
import static io.yapix.parse.constant.SpringConstants.PathVariable;
import static io.yapix.parse.constant.SpringConstants.RequestAttribute;
import static io.yapix.parse.constant.SpringConstants.RequestBody;
import static io.yapix.parse.constant.SpringConstants.RequestHeader;
import static io.yapix.parse.constant.SpringConstants.RequestParam;
import static io.yapix.parse.util.PsiDocCommentUtils.getTagParamTextMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import io.yapix.model.HttpMethod;
import io.yapix.model.Item;
import io.yapix.model.ParameterIn;
import io.yapix.model.RequestBodyType;
import io.yapix.parse.constant.SwaggerConstants;
import io.yapix.parse.model.RequestParseInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 参数解析工具类
 */
public class RequestParser {

    private RequestParser() {
    }

    public static RequestParseInfo parse(PsiMethod method, HttpMethod httpMethod) {
        List<PsiParameter> parameters = filterIgnoreParameter(method.getParameterList().getParameters());
        List<Item> requestParameters = doParseParameters(method, parameters);
        RequestBodyType requestBodyType = doParseRequestBodyType(parameters, httpMethod);
        List<Item> requestBody = doParseRequestBody(parameters, httpMethod, requestParameters, requestBodyType);

        RequestParseInfo info = new RequestParseInfo();
        info.setParameters(requestParameters);
        info.setRequestBodyType(requestBodyType);
        if (requestBodyType == RequestBodyType.form || requestBodyType == RequestBodyType.form_data) {
            info.setRequestBodyForm(requestBody);
        } else if (!requestBody.isEmpty()) {
            info.setRequestBody(requestBody.get(0));
        }
        return info;
    }

    /**
     * 解析请求方式
     */
    private static RequestBodyType doParseRequestBodyType(List<PsiParameter> parameters, HttpMethod method) {
        if (!method.isAllowBody()) {
            return null;
        }
        boolean requestBody = parameters.stream().anyMatch(p -> p.getAnnotation(RequestBody) != null);
        if (requestBody) {
            return RequestBodyType.json;
        }
        boolean multipartFile = parameters.stream().anyMatch(p -> MultipartFile.equals(p.getType().getCanonicalText()));
        if (multipartFile) {
            return RequestBodyType.form_data;
        }
        return RequestBodyType.form;
    }

    /**
     * 解析请求体内容
     */
    private static List<Item> doParseRequestBody(List<PsiParameter> parameters, HttpMethod method,
            List<Item> requestParameters, RequestBodyType requestBodyType) {
        if (!method.isAllowBody()) {
            return Lists.newArrayList();
        }

        // Json请求
        PsiParameter bp = parameters.stream()
                .filter(p -> p.getAnnotation(RequestBody) != null).findFirst().orElse(null);
        if (bp != null) {
            Item item = KernelParser.parseType(bp.getProject(), bp.getType(), bp.getType().getCanonicalText());
            return Lists.newArrayList(item);
        }

        // 文件上传
        List<Item> items = Lists.newArrayList();
        List<PsiParameter> fileParameters = parameters.stream()
                .filter(p -> MultipartFile.equals(p.getType().getCanonicalText())).collect(Collectors.toList());
        for (PsiParameter p : fileParameters) {
            Item item = KernelParser.parseType(p.getProject(), p.getType(), p.getType().getCanonicalText());
            item.setName(p.getName());
            item.setRequired(true);
            items.add(item);
        }
        // 合并查询参数到表单
        if (!fileParameters.isEmpty() && requestParameters != null) {
            List<Item> queries = requestParameters.stream()
                    .filter(p -> p.getIn() == ParameterIn.query).collect(Collectors.toList());
            for (Item query : queries) {
                query.setIn(null);
                items.add(query);
            }
            requestParameters.removeAll(queries);
        }
        return items;
    }

    /**
     * 解析普通参数
     */
    public static List<Item> doParseParameters(PsiMethod method, List<PsiParameter> allParameters) {
        List<PsiParameter> parameters = allParameters.stream()
                .filter(p -> p.getAnnotation(RequestBody) == null)
                .filter(p -> !MultipartFile.equals(p.getType().getCanonicalText()))
                .collect(Collectors.toList());

        Map<String, String> paramTagMap = getTagParamTextMap(method);
        List<Item> items = Lists.newArrayListWithExpectedSize(parameters.size());
        for (PsiParameter parameter : parameters) {
            Item item = doParseParameter(parameter);
            item.setDescription(ParseHelper.getParameterDescription(parameter, paramTagMap));

            List<Item> parameterItems = resolveItemToParameters(item);
            items.addAll(parameterItems);
        }
        return items;
    }

    /**
     * 解析单个参数
     */
    private static Item doParseParameter(PsiParameter parameter) {
        Item item = KernelParser
                .parseType(parameter.getProject(), parameter.getType(), parameter.getType().getCanonicalText());
        // 参数类型
        PsiAnnotation annotation = null;
        ParameterIn in = ParameterIn.query;
        Map<String, ParameterIn> targets = new LinkedHashMap<>();
        targets.put(RequestParam, ParameterIn.query);
        targets.put(RequestAttribute, ParameterIn.query);
        targets.put(RequestHeader, ParameterIn.header);
        targets.put(PathVariable, ParameterIn.path);
        for (Entry<String, ParameterIn> target : targets.entrySet()) {
            annotation = parameter.getAnnotation(target.getKey());
            if (annotation != null) {
                in = target.getValue();
                break;
            }
        }

        // 字段名称
        Boolean required = null;
        String name = null;
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
        if (required == null) {
            required = ParseHelper.getAnnotationRequired(parameter);
        }

        item.setIn(in);
        item.setName(name);
        item.setRequired(required != null ? required : false);
        return item;
    }

    /**
     * 解析Item为扁平结构的parameter
     */
    private static List<Item> resolveItemToParameters(Item item) {
        if (item == null) {
            return Collections.emptyList();
        }
        boolean needFlat = item.isObjectType() && item.getProperties() != null && ParameterIn.query == item.getIn();
        if (needFlat) {
            Collection<Item> flatItems = item.getProperties().values();
            flatItems.forEach(one -> one.setIn(item.getIn()));
            return Lists.newArrayList(flatItems);
        }
        return Lists.newArrayList(item);
    }

    /**
     * 过滤无需处理的参数
     */
    private static List<PsiParameter> filterIgnoreParameter(PsiParameter[] parameters) {
        Set<String> ignoreTypes = Sets.newHashSet(HttpServletRequest, HttpServletResponse, BindingResult, Pageable);
        return Arrays.stream(parameters)
                .filter(p -> {
                    String type = p.getType().getCanonicalText();
                    if (ignoreTypes.contains(type)) {
                        return false;
                    }
                    PsiAnnotation ignoreAnnotation = p.getAnnotation(SwaggerConstants.ApiIgnore);
                    if (ignoreAnnotation != null) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
    }


}
