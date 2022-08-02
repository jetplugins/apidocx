package io.yapix.base.sdk.eolinker.request;

import lombok.Data;

/**
 * @author chengliang
 * @date 2022/7/30 14:45
 */
@Data
public class LoginRequest {

    private Integer client;

    private String password;

    private String username;

    private String verifyCode;

}
