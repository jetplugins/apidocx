package io.yapix.base.sdk.yapi;

import java.util.Objects;

/**
 * Yapi客户端基异常
 */
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
        return Objects.equals(path, YapiConstants.yapiLogin) && Integer.valueOf(400).equals(code);
    }

    public String getPath() {
        return path;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
