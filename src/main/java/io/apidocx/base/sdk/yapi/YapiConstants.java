package io.apidocx.base.sdk.yapi;

/**
 * Yapi接口常量
 */
public interface YapiConstants {

    /**
     * 登录地址
     */
    String yapiLogin = "/api/user/login";

    /**
     * 登录地址LDAP
     */
    String yapiLoginLdap = "/api/user/login_by_ldap";

    static boolean isLoginPath(String path) {
        return yapiLogin.equals(path) || yapiLoginLdap.equals(path);
    }
}
