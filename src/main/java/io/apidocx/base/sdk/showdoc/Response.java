package io.apidocx.base.sdk.showdoc;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 响应数据格式
 */
@Data
public class Response<T> {

    @SerializedName("error_code")
    private Integer errorCode;

    @SerializedName("error_message")
    private String errorMessage;

    private T data;

    public boolean isSuccess() {
        return errorCode != null && errorCode == 0;
    }

}
