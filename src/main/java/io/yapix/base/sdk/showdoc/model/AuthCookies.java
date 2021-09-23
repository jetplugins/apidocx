package io.yapix.base.sdk.showdoc.model;


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
