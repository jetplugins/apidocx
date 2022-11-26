package io.apidocx.base.sdk.apifox.model;

import lombok.Builder;
import lombok.Data;

/**
 * 登录请求参数
 */
@Data
@Builder
public class LoginRequest {

    private String account;

    private String password;

    private String loginType;


}
