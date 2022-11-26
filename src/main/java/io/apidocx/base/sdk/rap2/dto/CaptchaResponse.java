package io.apidocx.base.sdk.rap2.dto;

import lombok.Data;

@Data
public class CaptchaResponse {

    private byte[] bytes;
    private String session;

}
