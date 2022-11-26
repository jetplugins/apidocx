package io.apidocx.base.sdk.showdoc.model;


import lombok.Data;

@Data
public class CaptchaResponse {

    private byte[] bytes;
    private String session;

}
