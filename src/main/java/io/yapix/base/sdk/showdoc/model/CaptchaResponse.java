package io.yapix.base.sdk.showdoc.model;


import io.yapix.base.sdk.showdoc.AbstractClient.HttpSession;

public class CaptchaResponse {

    private byte[] bytes;
    private HttpSession session;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}
