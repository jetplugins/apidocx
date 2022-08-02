package io.yapix.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * 参数
 */
@Data
public class YapiProperty {

    /** 类型 */
    private String type;

    /** 描述 */
    private String description;

    /** 是否必须 */
    private Set<String> required;

    @SerializedName("default")
    private String defaultValue;

    /** 当type为object */
    private Map<String, YapiProperty> properties;

    /** 当type为array */
    private YapiProperty items;

    /** 当type为array, item元素是否唯一 */
    private Boolean uniqueItems;

    /** 当type为array, 最小元素个数 */
    private Integer minItems;

    /** 当type为array, 最大元素个数 */
    private Integer maxItems;

    /** 响应mock */
    private YapiMock mock;

}


