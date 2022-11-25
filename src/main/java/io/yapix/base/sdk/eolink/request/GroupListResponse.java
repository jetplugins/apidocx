package io.yapix.base.sdk.eolink.request;

import io.yapix.base.sdk.eolink.model.ApiGroup;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GroupListResponse extends Response {

    /**
     * 分组列表
     */
    private List<ApiGroup> apiGroupData;

}
