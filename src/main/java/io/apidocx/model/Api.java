package io.apidocx.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * 接口信息
 */
@Data
public class Api {

    /**
     * 分类
     */
    private String category;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 路径
     */
    private String path;

    /**
     * 概述
     */
    private String summary;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 是否标记过期
     */
    private Boolean deprecated;

    /**
     * 参数
     */
    private List<Property> parameters;

    /**
     * 请求体类型
     */
    private RequestBodyType requestBodyType;

    /**
     * 请求体参数
     */
    private Property requestBody;

    /**
     * 请求体表单
     */
    private List<Property> requestBodyForm;

    /**
     * 响应体
     */
    private Property responses;


    /**
     * 获取指定类型的请求参数（query, path, header,etc）
     */
    public List<Property> getParametersByIn(ParameterIn in) {
        if (parameters == null) {
            return Collections.emptyList();
        }
        return parameters.stream().filter(p -> p.getIn() == in).collect(Collectors.toList());
    }

}
