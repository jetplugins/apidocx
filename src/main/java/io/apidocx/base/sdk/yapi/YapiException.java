package io.apidocx.base.sdk.yapi;

import lombok.Getter;

/**
 * Yapi客户端基异常
 */
@Getter
public class YapiException extends RuntimeException {

    private String path;
    private Integer code;
    private String msg;

    public YapiException(String path, Integer code, String msg) {
        super(msg);
        this.path = path;
        this.code = code;
        this.msg = msg;
    }

    public YapiException(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    /**
     * 是需要认证
     */
    public boolean isNeedAuth() {
        return Integer.valueOf(40011).equals(this.code);
    }

    /**
     * 认证失败，账户或密码错误
     */
    public boolean isAuthFailed() {
        boolean isLoginPath = YapiConstants.isLoginPath(path);
        return isLoginPath && (Integer.valueOf(400).equals(code)
                || Integer.valueOf(404).equals(code)
                || Integer.valueOf(401).equals(code)
        );
    }

}
