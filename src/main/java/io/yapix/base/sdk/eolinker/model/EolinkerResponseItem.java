package io.yapix.base.sdk.eolinker.model;

import java.util.List;

/**
 * @author chengliang
 * @date 2022/7/29 15:36
 */
public class EolinkerResponseItem {

    private Integer responseID;

    private final String responseCode = "200";

    private final String responseName = "成功";

    private final Integer responseType = 0;

    private final Integer paramJsonType = 0;

    private List<EolinkerProperty> paramList;

    private final String raw = "";

    private final String binary = "";

    private final Integer isDefault = 1;

    public EolinkerResponseItem(List<EolinkerProperty> paramList) {
        this.paramList = paramList;
    }

    public Integer getResponseID() {
        return responseID;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseName() {
        return responseName;
    }

    public Integer getResponseType() {
        return responseType;
    }

    public Integer getParamJsonType() {
        return paramJsonType;
    }

    public List<EolinkerProperty> getParamList() {
        return paramList;
    }

    public void setParamList(List<EolinkerProperty> paramList) {
        this.paramList = paramList;
    }

    public String getRaw() {
        return raw;
    }

    public String getBinary() {
        return binary;
    }

    public Integer getIsDefault() {
        return isDefault;
    }
}
