package io.apidocx.base.sdk.apifox.model;

import lombok.Data;

/**
 * 登录响应参数
 */
@Data
public class LoginResponse {

    private String accessToken;

    private String authority;

    private Long userId;


}
