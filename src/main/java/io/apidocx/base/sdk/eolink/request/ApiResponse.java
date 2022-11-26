package io.apidocx.base.sdk.eolink.request;

import io.apidocx.base.sdk.eolink.model.ApiInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiResponse extends Response {

    private ApiInfo apiInfo;
    private int hasUnreadComment;

}
