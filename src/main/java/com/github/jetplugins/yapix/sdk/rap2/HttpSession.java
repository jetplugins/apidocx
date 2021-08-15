package com.github.jetplugins.yapix.sdk.rap2;

import org.apache.commons.lang3.StringUtils;

public class HttpSession {

    private String cookies;
    private long cookiesTtl;

    public HttpSession() {
    }

    public HttpSession(String cookies, long cookiesTtl) {
        this.cookies = cookies;
        this.cookiesTtl = cookiesTtl;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(cookies) && cookiesTtl > System.currentTimeMillis();
    }

    //-------------------generated-----------------//

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public Long getCookiesTtl() {
        return cookiesTtl;
    }

    public void setCookiesTtl(Long cookiesTtl) {
        this.cookiesTtl = cookiesTtl;
    }
}
