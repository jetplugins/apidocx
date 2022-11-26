package io.apidocx.base.sdk.rap2.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private Long id;

    private String fullname;

    private String email;

    private String errMsg;

    public boolean isSuccess() {
        return errMsg == null || errMsg.isEmpty();
    }
}
