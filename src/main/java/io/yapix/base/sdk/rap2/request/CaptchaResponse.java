package io.yapix.base.sdk.rap2.request;

import io.yapix.base.sdk.rap2.AbstractClient.HttpSession;
import lombok.Data;

@Data
public class CaptchaResponse {

    private byte[] bytes;
    private HttpSession session;

}
