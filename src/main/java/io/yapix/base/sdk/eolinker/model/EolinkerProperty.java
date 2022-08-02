package io.yapix.base.sdk.eolinker.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

@Data
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

}
