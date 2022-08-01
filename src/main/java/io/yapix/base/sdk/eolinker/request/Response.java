package io.yapix.base.sdk.eolinker.request;

public class Response {

    public final static String SUCCESS_CODE = "000000";

    protected String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
