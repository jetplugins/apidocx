package io.yapix.base.sdk.yapi;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录方式
 */
@AllArgsConstructor
@Getter
public enum LoginWay {

    DEFAULT(YapiConstants.yapiLogin),
    LDAP(YapiConstants.yapiLoginLdap),
    ;

    /** 登录路径 */
    private final String path;

}
