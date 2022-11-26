package io.apidocx.base.sdk.showdoc.model;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
    private String v_code;

}
