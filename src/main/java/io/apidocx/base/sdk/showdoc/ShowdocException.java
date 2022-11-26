package io.apidocx.base.sdk.showdoc;

import lombok.Getter;

/**
 * Yapi客户端基异常
 */
@Getter
public class ShowdocException extends RuntimeException {

    private String path;
    private Integer errorCode;
    private String errorMessage;

    public ShowdocException(String path, String errorMessage) {
        super(errorMessage);
        this.path = path;
        this.errorMessage = errorMessage;
    }

    public ShowdocException(String path, Response response) {
        super(response.getErrorMessage());
        this.path = path;
        this.errorMessage = response.getErrorMessage();
        this.errorCode = response.getErrorCode();
    }

    public ShowdocException(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public boolean isNeedAuth() {
        return errorCode != null && errorCode.equals(10102);
    }

    public boolean isCaptchaError() {
        return errorCode != null && errorCode.equals(10206);
    }

    public boolean isAccountPasswordError() {
        return errorCode != null && errorCode.equals(10210);
    }

}
