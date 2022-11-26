package io.apidocx.base.sdk.yapi.model;

import com.google.common.base.Strings;

/**
 * 接口状态
 */
public enum ApiInterfaceStatus {
    done("已发布"),
    design("设计中"),
    undone("开发中"),
    testing("已提测"),
    deprecated("已过时"),
    stoping("暂停发布");

    private String message;


    ApiInterfaceStatus() {
    }


    ApiInterfaceStatus(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String getStatus(String message) {
        if (Strings.isNullOrEmpty(message)) {
            return undone.name();
        }
        if (message.equals(done.getMessage()) || message.equals(done.name())) {
            return done.name();
        }
        if (message.equals(design.getMessage()) || message.equals(design.name())) {
            return design.name();
        }
        if (message.equals(undone.getMessage()) || message.equals(undone.name())) {
            return undone.name();
        }
        if (message.equals(testing.getMessage()) || message.equals(testing.name())) {
            return testing.name();
        }
        if (message.equals(deprecated.getMessage()) || message.equals(deprecated.name())) {
            return deprecated.name();
        }
        if (message.equals(stoping.getMessage()) || message.equals(stoping.name())) {
            return stoping.name();
        }
        return undone.name();
    }
}
