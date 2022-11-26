package io.apidocx.base.sdk.apifox;

import lombok.Getter;

/**
 * 客户端基异常
 */
@Getter
public class ApifoxException extends RuntimeException {

    private final String path;
    private final String code;
    private final String msg;

    public ApifoxException(String path, String code, String message) {
        super("code: " + code + ", message: " + message);
        this.path = path;
        this.code = code;
        this.msg = message;
    }

    public ApifoxException(String path, String code, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.code = code;
        this.msg = message;
    }

    public boolean isNeedAuth() {
        return "401000".equals(code);
    }

    public boolean isAccountPasswordError() {
        return ("422001".equals(code) || "401009".equals(code));
    }

}
