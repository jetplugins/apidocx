package io.yapix.base.sdk.eolinker;

/**
 * Eolinker客户端基异常
 */
public class EolinkerException extends RuntimeException {

    private final String path;
    private final String statusCode;
    private final String msg;

    public EolinkerException(String path, String statusCode) {
        super(statusCode);
        this.path = path;
        this.statusCode = statusCode;
        this.msg = statusCode;
    }

    public EolinkerException(String path, String statusCode, String message) {
        super(message);
        this.path = path;
        this.statusCode = statusCode;
        this.msg = message;
    }

    public EolinkerException(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.statusCode = null;
        this.msg = message;
    }

    public boolean isNeedAuth() {
        return "200001".equals(statusCode);
    }

    public boolean isAccountPasswordError() {
        return "300110".equals(statusCode) || "300106".equals(statusCode) || "132000001".equals(statusCode);
    }

    //----------------------generated----------------------------//

    public String getPath() {
        return path;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
