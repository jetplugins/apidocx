package io.apidocx.base.sdk.eolink;

public interface EolinkConstants {

    String Login = "/userCenter/common/sso/login";
    String PageApiList = "/home/api_studio/inside/api/list";

    static boolean isLoginPath(String path) {
        return path != null && path.contains(Login);
    }
}
