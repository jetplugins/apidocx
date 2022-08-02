package io.yapix.base.sdk.yapi.model;


import lombok.Data;

@Data
public class AuthCookies {

    private String cookies;
    private long ttl;

    public AuthCookies(String cookies, long ttl) {
        this.cookies = cookies;
        this.ttl = ttl;
    }

}
