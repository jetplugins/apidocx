package io.yapix.base.sdk.showdoc;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * 响应数据格式
 */
public class ShowdocResponse implements Serializable {

    @SerializedName("error_code")
    private Integer errorCode;

    @SerializedName("error_message")
    private String errorMessage;

    private Object data;

    public boolean isOk() {
        return errorCode != null && errorCode == 0;
    }

    //-------------generated------------------//

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
