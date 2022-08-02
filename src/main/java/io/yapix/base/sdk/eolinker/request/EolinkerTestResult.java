package io.yapix.base.sdk.eolinker.request;

import io.yapix.base.sdk.eolinker.AbstractClient.HttpSession;
import lombok.Data;

@Data
public class EolinkerTestResult {

    private Code code;
    private String message;
    private HttpSession authSession;

    public enum Code {
        OK,
        AUTH_ERROR,
        NETWORK_ERROR
    }
}
