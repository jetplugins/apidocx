package io.apidocx.base.sdk.showdoc;

public interface ShowdocConstants {

    String GetCaptcha = "/api/common/verify";
    String LoginPath = "/api/user/login";
    String AccountInfoPath = "/api/user/info";
    String GetItemKey = "/api/item/getKey";
    String UpdatePageOpenApi = "/api/item/updateByApi";

    static boolean isLoginPath(String path) {
        return path != null && path.contains(LoginPath);
    }

    static boolean isCaptchaPath(String path) {
        return path != null && path.contains(GetCaptcha);
    }
}
