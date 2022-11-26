package io.apidocx.base.sdk.eolink.request;

import lombok.Data;

/**
 * @author chengliang
 * @date 2022/7/30 14:45
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 客户端类型: 0:网页, 1:桌面端
     */
    private Integer client = 1;

    /**
     * 登录类型: 0:手机号登录, 1账号密码登录
     */
    private Integer type = 1;

    private Integer appType = 0;

    private String verifyCode;

}
