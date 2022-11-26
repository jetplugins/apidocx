package io.apidocx.base.sdk.eolink.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Data;

@Data
public class ApiProperty {

    /**
     * 参数名
     */
    private String paramKey;
    /**
     * 参数类型
     */
    private String paramType;
    /**
     * 是否允许为空
     */
    private String paramNotNull;
    /**
     * 参数描述
     */
    private String paramName;

    private String paramValue;

    private String paramLimit;

    private String paramNote;

    private String paramMock;

    /**
     * 默认值
     */
    @SerializedName("default")
    private String defaultValue;

    /**
     * 子节点
     */
    private List<ApiProperty> childList;

}
