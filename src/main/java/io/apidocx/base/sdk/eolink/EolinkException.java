package io.apidocx.base.sdk.eolink;

import lombok.Getter;

/**
 * Eolink客户端基异常
 */
@Getter
public class EolinkException extends RuntimeException {

    private final String path;
    private final String code;
    private final String msg;

    public EolinkException(String path, String code) {
        super(code);
        this.path = path;
        this.code = code;
        this.msg = code;
    }

    public EolinkException(String path, String code, String message) {
        super(message);
        this.path = path;
        this.code = code;
        this.msg = message;
    }

    public EolinkException(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
        this.code = null;
        this.msg = message;
    }

    public boolean isNeedAuth() {
        return "200001".equals(code);
    }

    public boolean isAccountPasswordError() {
        return "300110".equals(code) || "300106".equals(code) || "132000001".equals(code);
    }

}
