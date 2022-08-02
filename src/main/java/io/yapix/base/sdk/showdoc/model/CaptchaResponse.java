package io.yapix.base.sdk.showdoc.model;


import io.yapix.base.sdk.showdoc.AbstractClient.HttpSession;
import lombok.Data;

@Data
public class CaptchaResponse {

    private byte[] bytes;
    private HttpSession session;

}
