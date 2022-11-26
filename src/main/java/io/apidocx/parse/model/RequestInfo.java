package io.apidocx.parse.model;

import io.apidocx.model.Property;
import io.apidocx.model.RequestBodyType;
import java.util.List;
import lombok.Data;

/**
 * 请求参数信息
 */
@Data
public class RequestInfo {

    private List<Property> parameters;
    private RequestBodyType requestBodyType;
    private Property requestBody;
    private List<Property> requestBodyForm;

}
