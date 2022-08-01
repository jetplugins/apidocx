package io.yapix.base.sdk.eolinker.request;

/**
 * @author chengliang
 * @date 2022/7/30 14:45
 */
public class LoginRequest {

    private Integer client;

    private String password;

    private String username;

    private String verifyCode;

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
