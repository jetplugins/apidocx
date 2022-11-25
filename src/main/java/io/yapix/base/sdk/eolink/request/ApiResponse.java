package io.yapix.base.sdk.eolink.request;

import io.yapix.base.sdk.eolink.model.ApiInfo;
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
