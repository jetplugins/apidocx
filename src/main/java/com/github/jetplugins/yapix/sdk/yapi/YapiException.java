package com.github.jetplugins.yapix.sdk.yapi;

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
