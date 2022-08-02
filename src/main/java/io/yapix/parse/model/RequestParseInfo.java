package io.yapix.parse.model;

import io.yapix.model.Property;
import io.yapix.model.RequestBodyType;
import java.util.List;
import lombok.Data;

/**
 * 请求参数信息
 */
@Data
public class RequestParseInfo {

    private List<Property> parameters;
    private RequestBodyType requestBodyType;
    private Property requestBody;
    private List<Property> requestBodyForm;

}
