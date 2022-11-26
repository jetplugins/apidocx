package io.apidocx.base.sdk.rap2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

    private String email;
    private String password;
    private String captcha;

}
