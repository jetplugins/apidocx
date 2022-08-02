package io.yapix.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请求体类型
 */
@AllArgsConstructor
@Getter
public enum RequestBodyType {
    form("application/x-www-form-urlencoded"),
    form_data("multipart/form-data"),
    json("application/json;charset=utf-8"),
    raw(""),
    ;

    private final String contentType;

}
