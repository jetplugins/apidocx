package io.yapix.base.sdk.rap2.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
    private String captcha;

}
