package io.yapix.base.sdk.eolinker.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EolinkerProperty {

    /** 参数名 */
    private String paramKey;
    /** 参数类型 */
    private String paramType;
    /** 是否允许为空 */
    private String paramNotNull;
    /** 参数描述 */
    private String paramName;

    private String paramValue;

    private String paramLimit;

    private String paramNote;

    /** 默认值 */
    @SerializedName("default")
    private String defaultValue;

    /** 子节点 */
    private List<EolinkerProperty> childList;

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamNotNull() {
        return paramNotNull;
    }

    public void setParamNotNull(String paramNotNull) {
        this.paramNotNull = paramNotNull;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamLimit() {
        return paramLimit;
    }

    public void setParamLimit(String paramLimit) {
        this.paramLimit = paramLimit;
    }

    public String getParamNote() {
        return paramNote;
    }

    public void setParamNote(String paramNote) {
        this.paramNote = paramNote;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<EolinkerProperty> getChildList() {
        return childList;
    }

    public void setChildList(List<EolinkerProperty> childList) {
        this.childList = childList;
    }
}
