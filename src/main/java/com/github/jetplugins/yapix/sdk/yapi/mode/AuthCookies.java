package com.github.jetplugins.yapix.sdk.yapi.mode;


public class AuthCookies {

    private String cookies;
    private long ttl;

    public AuthCookies(String cookies, long ttl) {
        this.cookies = cookies;
        this.ttl = ttl;
    }

    public String getCookies() {
        return cookies;
    }

    public long getTtl() {
        return ttl;
    }
}
