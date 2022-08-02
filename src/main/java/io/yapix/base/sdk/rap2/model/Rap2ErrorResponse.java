package io.yapix.base.sdk.rap2.model;

import lombok.Data;

@Data
public class Rap2ErrorResponse {

    private Boolean isOk;

    private String errMsg;

    public Boolean getOk() {
        return isOk;
    }

    public void setOk(Boolean ok) {
        isOk = ok;
    }

}
