package io.yapix.base.sdk.eolink.request;

import io.yapix.base.sdk.eolink.model.UserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetUserInfoResponse extends Response {

    private UserInfo userInfo;

}
