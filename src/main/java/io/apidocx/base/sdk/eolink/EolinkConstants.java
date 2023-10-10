package io.apidocx.base.sdk.eolink;

public interface EolinkConstants {

    String Login = "/userCenter/common/sso/login";

    static boolean isLoginPath(String path) {
        return path != null && path.contains(Login);
    }
}
