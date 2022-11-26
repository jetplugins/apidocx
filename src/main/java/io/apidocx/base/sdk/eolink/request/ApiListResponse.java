package io.apidocx.base.sdk.eolink.request;

import io.apidocx.base.sdk.eolink.model.ApiBase;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiListResponse extends Response {

    private Integer itemNum;

    private List<ApiBase> apiList;

}
