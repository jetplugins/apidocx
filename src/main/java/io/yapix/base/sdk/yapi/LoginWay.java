package io.yapix.base.sdk.yapi;

/**
 * 登录方式
 */
public enum LoginWay {

    DEFAULT(YapiConstants.yapiLogin),
    LDAP(YapiConstants.yapiLoginLdap),
    ;

    /** 登录路径 */
    private final String path;

    LoginWay(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
